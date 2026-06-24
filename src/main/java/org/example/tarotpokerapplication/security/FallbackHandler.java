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
        if (returnType == String.class) return "Oops you can't do this";
        if (List.class.isAssignableFrom(returnType)) return Collections.emptyList();
        return null;
    }
}
