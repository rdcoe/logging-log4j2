package org.apache.logging.log4j.test.appender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 *
 */
@Plugin(name="Deadlock", category ="Core",elementType="appender",printObject=true)
public class DeadlockAppender extends AbstractAppender {

    private WorkerThread thread = null;

    private DeadlockAppender(final String name) {
        super(name, null, null, false);
        thread = new WorkerThread();
    }

    @Override
    public void start() {
        super.start();

    }

    @Override
    public void stop() {
        super.stop();
        thread.start();
        try {
            thread.join();
        } catch (Exception ex) {
            System.out.println("Thread interrupted");
        }
    }

    @Override
    public void append(final LogEvent event) {
        throw new LoggingException("Always fail");
    }

    @PluginFactory
    public static DeadlockAppender createAppender(@PluginAttribute("name") final String name) {
        if (name == null) {
            LOGGER.error("A name for the Appender must be specified");
            return null;
        }

        return new DeadlockAppender(name);
    }

    private class WorkerThread extends Thread {

        @Override
        public void run() {
            Logger logger = LogManager.getLogger("org.apache.logging.log4j.test.WorkerThread");
            logger.debug("Worker is running");
        }
    }
}
