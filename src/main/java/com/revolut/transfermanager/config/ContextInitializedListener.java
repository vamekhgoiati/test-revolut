package com.revolut.transfermanager.config;

import com.revolut.transfermanager.db.util.DBUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class ContextInitializedListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ContextInitializedListener.class.getSimpleName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.INFO, "Context initialized. Calling DBUtil.init");
        DBUtil.init("db.properties", "schema.db");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Do nothing
    }
}
