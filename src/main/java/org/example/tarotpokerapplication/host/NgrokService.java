package org.example.tarotpokerapplication.host;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;

@Slf4j
@Service
public class NgrokService {

    @Value("${NGROK_TOKEN:}")
    private String ngrokToken;

    @Getter
    private final String publicUrl = "https://goldsmith-overkill-popsicle.ngrok-free.dev";

    private Process ngrokProcess;

    @PostConstruct
    public void start() {

        if (ngrokToken == null || ngrokToken.isBlank()) {
            log.warn("NgrokService: NGROK_TOKEN missing. Using fallback URL.");
            return;
        }

        Thread.ofVirtual().start(() -> {
            try {
                log.info("NgrokService: starting static tunnel -> {}", publicUrl);

                ProcessBuilder pb = new ProcessBuilder(
                        "ngrok", "http", "8080",
                        "--domain=" + publicUrl.replace("https://", "")
                );

                pb.environment().put("NGROK_AUTHTOKEN", ngrokToken);
                pb.redirectErrorStream(true);
                ngrokProcess = pb.start();

                log.info("NgrokService: Process started successfully.");

                try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(ngrokProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.debug("ngrok: {}", line);
                    }
                }
            } catch (IOException e) {
                log.error("NgrokService: Failed to start process. Is ngrok installed?", e);
            }
        });
    }

    @PreDestroy
    public void stop() {
        if (ngrokProcess != null) {
            ngrokProcess.destroy();
            log.info("NgrokService: stopped.");
        }
    }
}
