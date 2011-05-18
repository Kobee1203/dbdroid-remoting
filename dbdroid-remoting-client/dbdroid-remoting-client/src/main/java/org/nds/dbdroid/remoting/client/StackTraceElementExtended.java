package org.nds.dbdroid.remoting.client;

import java.lang.reflect.Method;

import org.apache.commons.lang.reflect.MethodUtils;

public class StackTraceElementExtended {
    private StackTraceElement stackTraceElement;

    private Class<?> declaringClass;
    private final Method method;

    public StackTraceElementExtended(StackTraceElement stackTraceElement, Class<?>[] parameterTypes) {
        this.stackTraceElement = stackTraceElement;
        try {
            this.declaringClass = Class.forName(this.stackTraceElement.getClassName());
        } catch (ClassNotFoundException e1) {
        }
        this.method = MethodUtils.getMatchingAccessibleMethod(this.declaringClass, this.stackTraceElement.getMethodName(), parameterTypes);
    }

    public StackTraceElement getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(StackTraceElement stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public Method getMethod() {
        return method;
    }
}
