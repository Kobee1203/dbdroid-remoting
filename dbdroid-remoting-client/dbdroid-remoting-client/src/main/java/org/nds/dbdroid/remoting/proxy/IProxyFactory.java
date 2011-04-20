package org.nds.dbdroid.remoting.proxy;

import java.lang.reflect.InvocationHandler;

public interface IProxyFactory<T> {
    T createProxy(Class<T> toMock, InvocationHandler handler);
}
