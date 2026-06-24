package org.example.tarotpokerapplication.security;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.authorization.method.MethodAuthorizationDeniedHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FallbackHandler implements MethodAuthorizationDeniedHandler {

    @Override
    public Object handleDeniedInvocation(MethodInvocation methodInvocation, AuthorizationResult authorizationResult) {
        Class<?> returnType = methodInvocation.getMethod().getReturnType();
        if (returnType == void.class || returnType == Void.class) return null;
        if (returnType == String.class) return null;
        if (returnType == boolean.class || returnType == Boolean.class) return false;
        if (returnType == int.class || returnType == Integer.class) return 0;
        if (returnType == long.class || returnType == Long.class) return 0L;
        if (returnType == double.class || returnType == Double.class) return 0.0;
        if (List.class.isAssignableFrom(returnType)) return Collections.emptyList();
        return null;
    }
}
