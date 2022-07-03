package com.mpeixoto.jms.producer.services;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * Class responsible for testing if the queue's listener is working as expected.
 *
 * @author mpeixoto
 */
@RunWith(MockitoJUnitRunner.class)
public class MapMessageListenerTest {
    @Mock
    private MapMessage mapMessage;
    /**
     * A Rule that provides the method responsible for checking the log messages.
     */
    @Rule
    public LoggerTest loggerTest = new LoggerTest(MapMessageListener.class);
    /**
     * Rule responsible for testing if the expected exceptions has been thrown.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MapMessageListener mapMessageListener;

    /**
     * Method responsible for instantiating a mapMessageListener before each test.
     */
    @Before
    public void setUp() {
        mapMessageListener = new MapMessageListener();
    }

    /**
     * Method responsible for testing if the message has been logged as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageGivenAMapMessageShouldLogTheMessage() throws JMSException {
        when(mapMessage.getString("Request")).thenReturn("test request");
        when(mapMessage.getString("Response")).thenReturn("test response");
        mapMessageListener.onMessage(mapMessage);
        loggerTest.verifyMessages("test request", "test response");
    }

    /**
     * Method responsible for testing if a RuntimeException has been thrown as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageShouldThrowARuntimeException() throws JMSException {
        when(mapMessage.getString("Request")).thenThrow(new JMSException("negative test"));
        expectedException.expect(RuntimeException.class);
        mapMessageListener.onMessage(mapMessage);
    }

    /**
     * Method responsible for testing if the error message has been logged as expected.
     */
    @Test
    public void onMessageGivenNullShouldLogAnError() {
        mapMessageListener.onMessage(null);
        loggerTest.verifyMessages("Invalid Message Received");
    }
}
