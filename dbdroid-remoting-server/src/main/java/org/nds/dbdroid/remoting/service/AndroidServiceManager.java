package org.nds.dbdroid.remoting.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class AndroidServiceManager extends ServiceManager implements IServiceManager, BeanPostProcessor {

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        registerService(bean, beanName);
        return bean;
    }

}
