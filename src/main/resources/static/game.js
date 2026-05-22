'use strict';

// ── Telegram Mini App ──────────────────────────────────────────────────────
const tg = window.Telegram?.WebApp;
if (tg) { tg.ready(); tg.expand(); }

// ── Player identity ────────────────────────────────────────────────────────
const urlParams   = new URLSearchParams(location.search);
const PLAYER_ID   = urlParams.get('player')
    || String(tg?.initDataUnsafe?.user?.id || '')
    || 'guest_' + Math.random().toString(36).slice(2, 8);
const PLAYER_NAME = tg?.initDataUnsafe?.user?.first_name || 'Player';

// ── Game state ─────────────────────────────────────────────────────────────
let SESSION_ID       = urlParams.get('session') || null;
let waitingMode      = 'public';   // 'public' | 'private'
let pollTimer        = null;
let selectedMajorIdx = null;
let lastPhase        = null;

// ── Animation tracking ─────────────────────────────────────────────────────
let lastRenderedRound   = 0;
let lastCommunitySize   = 0;
let currentGameState    = null;   // last state passed to renderGame (for explosion diffing)

// ── Perspective helpers ────────────────────────────────────────────────────
function getMe(s)  { return s.playerNumber === 1 ? s.player1 : s.player2; }
function getOpp(s) { return s.playerNumber === 1 ? s.player2 : s.player1; }

// ── Explosion tracking ─────────────────────────────────────────────────────
let explosionSignatures = null;   // Set<'SUIT:POWER'> — cards to animate with explosion

function markExplosions(oldState, newState) {
  explosionSignatures = new Set();
  const oldComm = new Set((oldState?.communityCards || []).map(c => `${c.suit}:${c.power}`));
  for (const c of (newState.communityCards || [])) {
    if (!oldComm.has(`${c.suit}:${c.power}`)) explosionSignatures.add(`${c.suit}:${c.power}`);
  }
  const oldHole = new Set((getMe(oldState)?.holeCards || []).map(c => `${c.suit}:${c.power}`));
  for (const c of (getMe(newState)?.holeCards || [])) {
    if (!oldHole.has(`${c.suit}:${c.power}`)) explosionSignatures.add(`${c.suit}:${c.power}`);
  }
}

function clearExplosions() { explosionSignatures = null; }

// ── Event reveal banner ────────────────────────────────────────────────────
function showEventBanner(icon, name, effect) {
  const el = document.getElementById('event-reveal');
  el.classList.remove('banner-out', 'hidden');
  void el.offsetWidth;   // force reflow so bannerDown always replays
  document.getElementById('event-reveal-icon').textContent   = icon;
  document.getElementById('event-reveal-name').textContent   = name;
  document.getElementById('event-reveal-effect').textContent = effect;
}

function hideEventBanner() {
  return new Promise(resolve => {
    const el = document.getElementById('event-reveal');
    if (el.classList.contains('hidden')) { resolve(); return; }
    el.classList.add('banner-out');
    setTimeout(() => { el.classList.add('hidden'); el.classList.remove('banner-out'); resolve(); }, 300);
  });
}

function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

// ── Move timer ─────────────────────────────────────────────────────────────
let timerInterval = null;
let timerDeadline = 0;   // epoch ms when the window closes

function startMoveTimer(remainingMs) {
  timerDeadline = Date.now() + remainingMs;   // sync to server's authoritative value
  if (!timerInterval) {
    tickTimer();
    timerInterval = setInterval(tickTimer, 250);
  }
}

function clearMoveTimer() {
  if (timerInterval) { clearInterval(timerInterval); timerInterval = null; }
  timerDeadline = 0;
  const el = document.getElementById('move-timer');
  if (el) el.classList.add('hidden');
}

function tickTimer() {
  const el = document.getElementById('move-timer');
  if (!el) return;
  const remaining = Math.max(0, timerDeadline - Date.now());
  const secs = Math.ceil(remaining / 1000);
  el.textContent = `⏱ ${secs}s`;
  el.classList.remove('hidden');
  el.classList.toggle('timer-urgent', secs <= 5);
  if (remaining === 0) clearMoveTimer();
}

// ── Boot ───────────────────────────────────────────────────────────────────
(function boot() {
  if (SESSION_ID) {
    startPolling();
    showScreen('game');
  } else {
    showScreen('menu');
  }
})();

// ══════════════════════════════════════════════════════════════════════════
// SCREEN NAVIGATION
// ══════════════════════════════════════════════════════════════════════════

function showScreen(name) {
  document.querySelectorAll('.screen').forEach(el => {
    el.classList.toggle('hidden', el.id !== 'screen-' + name);
  });
  if (name !== 'round-end') document.getElementById('screen-round-end').classList.add('hidden');
  if (name !== 'match-end') document.getElementById('screen-match-end').classList.add('hidden');
}

function showPanel(name) {
  document.getElementById('panel-' + name).classList.remove('hidden');
  document.getElementById('panel-backdrop').classList.remove('hidden');
}

function hidePanel() {
  document.querySelectorAll('.side-panel').forEach(p => p.classList.add('hidden'));
  document.getElementById('panel-backdrop').classList.add('hidden');
}

// ══════════════════════════════════════════════════════════════════════════
// GAME ENTRY POINTS
// ══════════════════════════════════════════════════════════════════════════

// ── 1 Player: vs Bot ──────────────────────────────────────────────────────
async function startSolo() {
  try {
    const state = await api('/game/match/create/bot', 'POST',
        { userId: PLAYER_ID, username: PLAYER_NAME });
    enterGame(state);
  } catch (e) {
    alert('Could not start game: ' + e.message);
  }
}

// ── 2P: Quick Match (public) ───────────────────────────────────────────────
async function quickMatch() {
  waitingMode = 'public';
  enterWaiting(null);   // show searching UI immediately

  try {
    const state = await api('/game/match/join', 'POST',
        { userId: PLAYER_ID, username: PLAYER_NAME });
    if (state.phase === 'WAITING_FOR_PLAYER') {
      // Created a public session — wait for someone to join
      SESSION_ID = state.sessionId;
      startPolling();
    } else {
      // Joined an existing public session — game is already running
      enterGame(state);
    }
  } catch (e) {
    stopPolling();
    SESSION_ID = null;
    showScreen('2p-options');
    alert('Quick match failed: ' + e.message);
  }
}

// ── 2P: Private Match (create with code) ──────────────────────────────────
async function createMatch() {
  waitingMode = 'private';          // set BEFORE the API call
  enterWaiting(null);               // show waiting screen immediately (code arrives below)
  try {
    const state = await api('/game/match/create/private', 'POST',
        { userId: PLAYER_ID, username: PLAYER_NAME });
    enterWaiting(state.inviteCode); // update with real code now that we have it
    SESSION_ID = state.sessionId;
    startPolling();
  } catch (e) {
    showScreen('2p-options');
    alert('Could not create game: ' + e.message);
  }
}

// ── 2P: Join by Code ──────────────────────────────────────────────────────
async function joinByCode() {
  const code  = document.getElementById('code-input').value.trim().toUpperCase();
  const errEl = document.getElementById('join-error');
  errEl.classList.add('hidden');

  if (code.length !== 6) {
    errEl.textContent = 'Please enter a 6-character code.';
    errEl.classList.remove('hidden');
    return;
  }
  try {
    const state = await api(`/game/match/join/${code}`, 'POST',
        { userId: PLAYER_ID, username: PLAYER_NAME });
    enterGame(state);
  } catch (e) {
    errEl.textContent = 'Invalid or expired code.';
    errEl.classList.remove('hidden');
  }
}

// ── Back from waiting: abandon session ────────────────────────────────────
async function abandonAndBack() {
  stopPolling();
  if (SESSION_ID) {
    // Fire-and-forget — don't block the UI
    fetch(`/game/match/${SESSION_ID}?userId=${PLAYER_ID}`, { method: 'DELETE' }).catch(() => {});
    SESSION_ID = null;
  }
  lastPhase = null;
  showScreen('2p-options');
}

// ══════════════════════════════════════════════════════════════════════════
// HELPERS: screen transitions
// ══════════════════════════════════════════════════════════════════════════

function enterWaiting(inviteCode) {
  const isPrivate = waitingMode === 'private';

  document.getElementById('waiting-title').textContent   = isPrivate ? 'Waiting for Opponent' : 'Finding Match…';
  document.getElementById('waiting-icon').textContent    = isPrivate ? '🔑' : '⚡';
  document.getElementById('waiting-heading').textContent = isPrivate ? 'Waiting for opponent…' : 'Finding opponent…';
  document.getElementById('waiting-hint').textContent    = isPrivate ? '' : 'Public match · anyone can join';

  const codeSection = document.getElementById('waiting-code-section');
  if (isPrivate && inviteCode) {
    document.getElementById('invite-code-display').textContent = inviteCode;
    document.getElementById('invite-code-cmd').textContent     = inviteCode;
    codeSection.classList.remove('hidden');
  } else {
    codeSection.classList.add('hidden');
  }

  showScreen('waiting');
}

function enterGame(state) {
  SESSION_ID = state.sessionId;
  lastPhase  = null;
  selectedMajorIdx = null;
  lastRenderedRound = 0;
  lastCommunitySize = 0;
  startPolling();
  render(state);
}

// ══════════════════════════════════════════════════════════════════════════
// POLLING
// ══════════════════════════════════════════════════════════════════════════

function startPolling() {
  stopPolling();
  poll();
  pollTimer = setInterval(poll, 2000);
}

function stopPolling() {
  if (pollTimer) { clearInterval(pollTimer); pollTimer = null; }
}

async function poll() {
  if (!SESSION_ID) return;
  try {
    const state = await api(`/game/match/${SESSION_ID}?userId=${PLAYER_ID}`);
    render(state);
  } catch (e) {
    console.error('Poll error', e);
  }
}

// ══════════════════════════════════════════════════════════════════════════
// RENDERING
// ══════════════════════════════════════════════════════════════════════════

function render(s) {
  const phase = s.phase;

  // Still waiting for second player — re-apply the correct mode on every poll
  // so public/private panels are always in sync with waitingMode.
  if (phase === 'WAITING_FOR_PLAYER') {
    enterWaiting(waitingMode === 'private' ? s.inviteCode : null);
    return;
  }

  if (phase === 'MATCH_END') {
    showScreen('game');
    renderGame(s, true);   // reveal opponent cards
    renderMatchEnd(s);
    return;
  }

  // First time we see ROUND_END:
  //   1. Flip opponent cards so players can compare hands (5 s)
  //   2. Then show the round-result overlay (auto-advances after 1.5 s)
  // Subsequent polls while still in ROUND_END: do nothing — overlay is showing.
  if (phase === 'ROUND_END') {
    if (lastPhase !== 'ROUND_END') {
      lastPhase = 'ROUND_END';
      showScreen('game');
      renderGame(s, true);   // flip opponent cards
      setTimeout(() => {
        if (lastPhase === 'ROUND_END' && SESSION_ID) renderRoundEnd(s);
      }, 5000);
    }
    return;
  }

  lastPhase = phase;
  showScreen('game');
  renderGame(s);
}

function renderGame(s, revealOpponent = false) {
  currentGameState = s;
  // ── Animation state ──────────────────────────────────────────────────────
  const isNewRound = s.round > lastRenderedRound;
  if (isNewRound) {
    lastRenderedRound = s.round;
    lastCommunitySize = 0;
  }
  const prevCommunitySize = lastCommunitySize;
  lastCommunitySize = s.communityCards ? s.communityCards.length : 0;
  const newCommunityCount = lastCommunitySize - prevCommunitySize;

  const me  = getMe(s);
  const opp = getOpp(s);

  // ── Score bar ─────────────────────────────────────────────────────────────
  document.getElementById('p1-name').textContent  = s.player1?.name || 'P1';
  document.getElementById('p2-name').textContent  = s.player2?.name || 'P2';
  document.getElementById('p1-wins').textContent  = s.player1?.wins ?? 0;
  document.getElementById('p2-wins').textContent  = s.player2?.wins ?? 0;
  document.getElementById('round-label').textContent = `Round ${s.round}`;
  document.getElementById('phase-label').textContent = phaseLabel(s);

  // ── Effect message ────────────────────────────────────────────────────────
  const efEl = document.getElementById('effect-msg');
  if (s.lastEffectMessage) {
    efEl.textContent = s.lastEffectMessage;
    efEl.classList.remove('hidden');
  } else {
    efEl.classList.add('hidden');
  }

  // ── Opponent zone ─────────────────────────────────────────────────────────
  document.getElementById('opp-zone-label').textContent = opp?.name || 'Opponent';

  const oppEl   = document.getElementById('opponent-hole-cards');
  const oppCards = revealOpponent ? opp?.holeCards : null;
  if (oppCards && oppCards.length) {
    oppEl.innerHTML = oppCards.map((c, i) =>
        minorCardHtml(c, true, i * 280, 'card-flip')).join('');
  } else {
    oppEl.innerHTML = faceDownHtml(isNewRound, 0) + faceDownHtml(isNewRound, 180);
  }

  // ── Community grid (2×3 = 6 slots) ───────────────────────────────────────
  document.getElementById('community-grid').innerHTML =
      communityGridHtml(s.communityCards || [], prevCommunitySize, newCommunityCount);

  // ── My hand ───────────────────────────────────────────────────────────────
  document.getElementById('hole-cards').innerHTML =
      (me?.holeCards || []).map((c, i) => minorCardHtml(c, isNewRound, i * 180)).join('');

  // ── Event cards ───────────────────────────────────────────────────────────
  document.getElementById('major-cards').innerHTML =
      (me?.majorCards || []).map((c, i) => majorCardHtml(c, i)).join('');
  document.getElementById('events-used-label').textContent =
      `(${me?.eventsUsed ?? 0}/2 used)`;

  attachMajorListeners(s);
  renderActionPanel(s);
}

function communityGridHtml(cards, prevSize, newCount) {
  const SLOTS = 6;
  let html = '';
  for (let i = 0; i < SLOTS; i++) {
    if (i < cards.length) {
      const animate = i >= prevSize && newCount > 0;
      const delay   = animate ? (i - prevSize) * 180 : 0;
      html += minorCardHtml(cards[i], animate, delay);
    } else {
      html += '<div class="empty-slot"></div>';
    }
  }
  return html;
}

function renderActionPanel(s) {
  const panel      = document.getElementById('action-panel');
  const status     = document.getElementById('action-status');
  const buttons    = document.getElementById('action-buttons');
  const btnTrigger = document.getElementById('btn-trigger');
  const btnPass    = document.getElementById('btn-pass');

  const me  = getMe(s);
  const opp = getOpp(s);
  const inWindow = s.phase === 'EVENT_WINDOW';
  if (!inWindow || s.playerNumber === 0) { panel.classList.add('hidden'); clearMoveTimer(); return; }
  panel.classList.remove('hidden');

  if (me?.acted) {
    clearMoveTimer();
    status.textContent = opp?.acted ? 'Both acted — advancing…' : 'Waiting for opponent…';
    buttons.classList.add('hidden');
    return;
  }

  // Start or re-sync the countdown
  if (s.windowRemainingMs > 0) startMoveTimer(s.windowRemainingMs);

  const sel = selectedMajorIdx !== null ? (me?.majorCards || [])[selectedMajorIdx] : null;
  status.textContent = sel ? `Selected: ${sel.name}` : 'Select an Event Card or pass.';
  buttons.classList.remove('hidden');

  btnTrigger.disabled = selectedMajorIdx === null || (me?.eventsUsed ?? 0) >= 2;

  btnTrigger.onclick = async () => {
    if (selectedMajorIdx === null) return;
    const card    = (getMe(s)?.majorCards || [])[selectedMajorIdx];
    const cardIdx = selectedMajorIdx;
    selectedMajorIdx = null;

    stopPolling();
    clearMoveTimer();

    let state;
    try {
      state = await api(`/game/match/${SESSION_ID}/trigger`, 'PUT',
          { userId: PLAYER_ID, cardIndex: cardIdx });
    } catch (e) {
      startPolling();
      return;
    }

    const oppCard     = state.botLastEventCard;
    const playerFirst = (state.playerNumber === 1) === state.firstActorIsPlayer1;
    const snap        = currentGameState;

    if (playerFirst) {
      // ── Player banner → explosion → 1 s pause → (optional) bot banner ──
      showEventBanner(majorIcon(card.eventId), card.name, majorEffectLabel(card.eventId));
      await sleep(1500);
      markExplosions(snap, state);
      renderGame(state);
      clearExplosions();
      await sleep(700);
      await hideEventBanner();
      await sleep(1000);          // wait before next card appears
      if (oppCard) {
        showEventBanner(majorIcon(oppCard.eventId), oppCard.name, majorEffectLabel(oppCard.eventId));
        await sleep(1500);
        await hideEventBanner();
      }
    } else {
      // ── (Optional) bot banner → explosion → 1 s pause → player banner ──
      if (oppCard) {
        showEventBanner(majorIcon(oppCard.eventId), oppCard.name, majorEffectLabel(oppCard.eventId));
        await sleep(1500);
        markExplosions(snap, state);
        renderGame(state);
        clearExplosions();
        await sleep(700);
        await hideEventBanner();
        await sleep(1000);        // wait before next card appears
      }
      showEventBanner(majorIcon(card.eventId), card.name, majorEffectLabel(card.eventId));
      await sleep(1500);
      if (!oppCard) {             // explosion on player banner if bot had nothing to show first
        markExplosions(snap, state);
        renderGame(state);
        clearExplosions();
        await sleep(700);
        await sleep(1000);
      }
      await hideEventBanner();
    }

    startPolling();
  };

  btnPass.onclick = async () => {
    stopPolling();
    clearMoveTimer();
    selectedMajorIdx = null;

    let state;
    try {
      state = await api(`/game/match/${SESSION_ID}/pass`, 'PUT',
          { userId: PLAYER_ID, cardIndex: 0 });
    } catch (e) {
      startPolling();
      return;
    }

    // If the bot played an event while we passed, show their banner + explosion
    const oppCard = state.botLastEventCard;
    if (oppCard) {
      const snap = currentGameState;
      showEventBanner(majorIcon(oppCard.eventId), oppCard.name, majorEffectLabel(oppCard.eventId));
      await sleep(1500);
      markExplosions(snap, state);
      renderGame(state);
      clearExplosions();
      await sleep(700);
      await hideEventBanner();
      await sleep(1000);          // wait before next card appears
    } else {
      renderGame(state);
    }

    startPolling();
  };
}

function renderRoundEnd(s) {
  const overlay = document.getElementById('screen-round-end');
  overlay.classList.remove('hidden');

  const myName = s.playerNumber === 1 ? s.player1?.name : s.player2?.name;
  const icon = document.getElementById('round-result-icon');
  const text = document.getElementById('round-result-text');
  const sub  = document.getElementById('round-result-sub');

  if (!s.roundWinnerName) {
    icon.textContent = '🤝'; text.textContent = "It's a Tie!"; sub.textContent = 'The round ends in a draw.';
  } else if (s.roundWinnerName === myName) {
    icon.textContent = '🏆'; text.textContent = 'You Win the Round!';
    sub.textContent  = `Score: ${s.player1?.wins ?? 0} — ${s.player2?.wins ?? 0}`;
  } else {
    icon.textContent = '💀'; text.textContent = `${s.roundWinnerName} wins`;
    sub.textContent  = `Score: ${s.player1?.wins ?? 0} — ${s.player2?.wins ?? 0}`;
  }

  setTimeout(async () => {
    overlay.classList.add('hidden');
    lastPhase = null;
    try {
      await api(`/game/match/${SESSION_ID}/next-round`, 'PUT',
          { userId: PLAYER_ID, cardIndex: 0 });
    } catch (_) {}
    await poll();
  }, 1500);
}

function renderMatchEnd(s) {
  const overlay = document.getElementById('screen-match-end');
  overlay.classList.remove('hidden');

  const myName   = s.playerNumber === 1 ? s.player1?.name : s.player2?.name;
  const isWinner = s.roundWinnerName === myName
      || (s.playerNumber === 1 && (s.player1?.wins ?? 0) > (s.player2?.wins ?? 0))
      || (s.playerNumber === 2 && (s.player2?.wins ?? 0) > (s.player1?.wins ?? 0));

  document.getElementById('match-result-text').textContent =
      isWinner ? '🎴 You win the Match!' : `🎴 ${s.roundWinnerName ?? 'Opponent'} wins!`;
  document.getElementById('match-result-sub').textContent =
      `${s.player1?.name ?? 'P1'} ${s.player1?.wins ?? 0} — ${s.player2?.wins ?? 0} ${s.player2?.name ?? 'P2'}`;

  document.getElementById('btn-new-game').onclick = () => {
    stopPolling();
    SESSION_ID = null;
    lastPhase  = null;
    lastRenderedRound = 0;
    lastCommunitySize = 0;
    overlay.classList.add('hidden');
    showScreen('menu');
  };
}

// ══════════════════════════════════════════════════════════════════════════
// CARD HTML
// ══════════════════════════════════════════════════════════════════════════

function minorCardHtml(c, animate = false, delayMs = 0, animClass = 'card-deal') {
  // If this card is in the explosion set, override animation
  const sig = `${c.suit}:${c.power}`;
  if (explosionSignatures && explosionSignatures.has(sig)) {
    animClass = 'card-explosion';
    animate   = true;
    delayMs   = 0;
  }
  const suitClass  = `suit-${(c.suit || '').toLowerCase()}`;
  const cls        = animate ? animClass : '';
  const animStyle  = animate ? ` style="--deal-delay:${delayMs}ms"` : '';
  return `<div class="card ${suitClass} ${cls}"${animStyle}>
    <div class="suit-icon"></div>
    <div class="card-power">${powerToName(c.power)}</div>
    <div class="card-name">${c.suit || ''}</div>
  </div>`;
}

function faceDownHtml(animate = false, delayMs = 0) {
  const animClass = animate ? 'face-down-deal' : '';
  const animStyle = animate ? ` style="--deal-delay:${delayMs}ms"` : '';
  return `<div class="face-down ${animClass}"${animStyle}>🂠</div>`;
}

function majorCardHtml(c, i) {
  const sel = i === selectedMajorIdx ? 'selected' : '';
  return `<div class="major-card ${sel}" data-index="${i}">
    <div class="card-arcana-icon">${majorIcon(c.eventId)}</div>
    <div class="card-arcana-name">${c.name}</div>
    <div class="card-effect">${majorEffectLabel(c.eventId)}</div>
  </div>`;
}

function attachMajorListeners(s) {
  const inWindow = s.phase === 'EVENT_WINDOW';
  if (!inWindow || getMe(s)?.acted) return;
  document.querySelectorAll('.major-card').forEach(el => {
    el.addEventListener('click', () => {
      const idx = Number.parseInt(el.dataset.index);
      selectedMajorIdx = (selectedMajorIdx === idx) ? null : idx;
      renderGame(s);
    });
  });
}

// ══════════════════════════════════════════════════════════════════════════
// HELPERS
// ══════════════════════════════════════════════════════════════════════════

function powerToName(p) {
  return ({ 15: 'A', 14: 'K', 13: 'Q', 12: 'Kn', 11: 'Pg' })[p] ?? String(p);
}

function phaseLabel(s) {
  if (s.phase === 'EVENT_WINDOW')
    return ({ 1: '⚡ Flop', 2: '⚡ Turn', 3: '⚡ River' })[s.windowNumber] ?? '⚡ Event';
  return ({
    WAITING_FOR_PLAYER: 'Waiting',
    ROUND_END: 'Round End',
    MATCH_END: 'Match End',
  })[s.phase] ?? s.phase;
}

function majorIcon(id) {
  return ({
    0:'🤡',1:'🪄',2:'🌙',3:'👑',4:'🏛️',5:'📜',6:'💞',7:'🏇',
    8:'🦁',9:'🕯️',10:'🎡',11:'⚖️',12:'🙃',13:'💀',14:'🌊',
    15:'😈',16:'🗼',17:'⭐',18:'🌑',19:'☀️',20:'📯',21:'🌍'
  })[id] ?? '🔮';
}

function majorEffectLabel(id) {
  return ({
    0:'Add community card', 1:'Swap weakest hole', 3:'Swap weakest hole',
    10:'Reshuffle community', 13:'Remove lowest', 16:'Remove random',
    17:'Add community card', 18:'Remove random', 19:'Add community card',
  })[id] ?? 'Mystic vision';
}


async function api(path, method = 'GET', body = null) {
  const opts = { method };
  if (body) {
    opts.headers = { 'Content-Type': 'application/json' };
    opts.body = JSON.stringify(body);
  }
  const res = await fetch(path, opts);
  if (!res.ok) throw new Error(`${method} ${path} → ${res.status}`);
  return res.json();
}
