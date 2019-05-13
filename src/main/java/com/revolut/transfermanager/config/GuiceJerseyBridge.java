package com.revolut.transfermanager.config;

import com.google.inject.Injector;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class GuiceJerseyBridge extends AbstractBinder {
    private final Injector guiceInjector;

    public GuiceJerseyBridge(Injector guiceInjector) {
        this.guiceInjector = guiceInjector;
    }

    @Override
    protected void configure() {
        guiceInjector.getBindings().forEach((key, value) -> {
            String className = key.getTypeLiteral().getType().getTypeName();
            try {
                Class binding = Class.forName(className);
                bindFactory(new Factory<Object>() {
                    @Override
                    public Object provide() {
                        return guiceInjector.getInstance(binding);
                    }

                    @Override
                    public void dispose(Object instance) {

                    }
                }).to(binding);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to bind class " + className);
            }
        });
    }
}
