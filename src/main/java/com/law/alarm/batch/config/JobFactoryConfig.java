package com.law.alarm.batch.config;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobFactoryConfig {

    private final AutowireCapableBeanFactory beanFactory;

    public JobFactoryConfig(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory() {
        return new AutowiringSpringBeanJobFactory(beanFactory);
    }

}
