package org.nds.dbdroid.remoting.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JavaProxyFactory<T> implements IProxyFactory<T> {
    @SuppressWarnings("unchecked")
    public T createProxy(Class<T> clazz, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, handler);
    }
}
