package com.mpeixoto.jms.producer.services;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.hamcrest.core.Is;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/** Class responsible for providing the tools needed for testing the messages inside loggers. */
public class LoggerTest implements TestRule {
    private final List<LogEvent> capturedEvents = new ArrayList<>();
    private final Class loggerClass;
    private Appender mockAppender;
    private Logger logger;

    /**
     * Constructor of the LoggerTest class.
     *
     * @param loggerClass The class that will have the logs tested
     */
    public LoggerTest(Class loggerClass) {
        this.loggerClass = loggerClass;
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                mockAppender = Mockito.mock(Appender.class);

                when(mockAppender.getName()).thenReturn("MockAppender");
                when(mockAppender.isStarted()).thenReturn(true);
                capturedEvents.clear();
                Mockito.doAnswer(
                        invocationOnMock -> {
                            capturedEvents.add(((LogEvent) invocationOnMock.getArguments()[0]).toImmutable());
                            return null;
                        })
                        .when(mockAppender)
                        .append(ArgumentMatchers.any(LogEvent.class));
                logger = (Logger) LogManager.getLogger(loggerClass);
                logger.addAppender(mockAppender);
                logger.setLevel(Level.INFO);

                statement.evaluate();

                logger.removeAppender(mockAppender);
            }
        };
    }

    /**
     * Method responsible for making the test comparing the messages' characters. * * @param messages
     * Type: Arrays of Strings
     *
     * @param messages Arrays of strings
     */
    public void verifyMessages(String... messages) {
        assertThat(capturedEvents.size(), Is.is(messages.length));
        int i = 0;
        for (LogEvent loggingEvent : capturedEvents) {
            assertEquals(messages[i++], loggingEvent.getMessage().getFormattedMessage());
        }
    }
}
