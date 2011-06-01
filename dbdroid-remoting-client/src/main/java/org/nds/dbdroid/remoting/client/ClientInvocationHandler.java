package org.nds.dbdroid.remoting.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

class ClientInvocationHandler implements InvocationHandler {

    private final ClientManager clientManager;

    private transient Method equalsMethod;

    private transient Method hashCodeMethod;

    private transient Method toStringMethod;

    public <T> ClientInvocationHandler(Class<T> clazz, ClientManager clientManager) {
        this.clientManager = clientManager;

        try {
            Class<T> clazz_ = clazz;
            if (clazz_.isInterface()) {
                clazz_ = (Class<T>) Object.class;
            }
            equalsMethod = clazz_.getMethod("equals", new Class[] { Object.class });
            hashCodeMethod = clazz_.getMethod("hashCode", (Class[]) null);
            toStringMethod = clazz_.getMethod("toString", (Class[]) null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("An Object method could not be found!");
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (equalsMethod.equals(method)) {
            return Boolean.valueOf(proxy == args[0]);
        }
        if (hashCodeMethod.equals(method)) {
            return Integer.valueOf(System.identityHashCode(proxy));
        }
        if (toStringMethod.equals(method)) {
            return toString(proxy);
        }

        return this.clientManager.invoke(proxy, method, args);
    }

    private String toString(Object proxy) {
        return "Proxy for " + type(proxy);
    }

    private String type(Object proxy) {
        if (Proxy.isProxyClass(proxy.getClass())) {
            return proxy.getClass().getInterfaces()[0].toString();
        } else {
            return proxy.getClass().getSuperclass().toString();
        }
    }
}
