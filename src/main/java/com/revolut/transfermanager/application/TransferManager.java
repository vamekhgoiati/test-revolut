package com.revolut.transfermanager.application;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.revolut.transfermanager.config.GuiceJerseyBridge;
import com.revolut.transfermanager.ioc.TransferManagerModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransferManager {
    private static final Logger logger = Logger.getLogger(TransferManager.class.getSimpleName());

    public static void main(String[] args) {

        logger.log(Level.INFO, "Starting Jetty server ...");
        Server server = new Server(8080);

        ServletContextHandler ctx =
                new ServletContextHandler(ServletContextHandler.SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletContainer servletContainer = new ServletContainer(getResourceConfig());
        ServletHolder serHol = new ServletHolder(servletContainer);
        serHol.setInitOrder(1);
        ctx.addServlet(serHol, "/*");

        try {
            server.start();
            server.join();
            logger.log(Level.INFO, "Jetty server started.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            server.destroy();
        }
    }

    private static ResourceConfig getResourceConfig() {
        Injector injector = Guice.createInjector(Collections.singletonList(new TransferManagerModule()));
        ResourceConfig config = new ResourceConfig();
        config.register(new GuiceJerseyBridge(injector));
        config.packages(true, "com.revolut.transfermanager");
        return config;
    }
}
