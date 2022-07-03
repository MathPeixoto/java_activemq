package com.mpeixoto.jms.consumer.services;

import com.mpeixoto.jms.consumer.exception.MessageException;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Class responsible for testing if the queue's listener is working as expected.
 *
 * @author mpeixoto
 */
@RunWith(MockitoJUnitRunner.class)
public class MapMessageListenerTest {
    private final String REPLY = "test request";
    @Mock
    private MapMessage mapMessage;
    @Mock
    private TextMessage textMessage;
    @Mock
    private MapMessage responseMessage;
    @Mock
    private MessageProducer producer;

    private MapMessageListener mapMessageListener;
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

    /**
     * Method responsible for instantiating a mapMessageListener before each test.
     */
    @Before
    public void setUp() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        mapMessageListener = new MapMessageListener(connectionFactory);
        mapMessageListener.setResponseMessage(responseMessage);
        mapMessageListener.setProducer(producer);
    }

    /**
     * Method responsible for testing if the message has been logged as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageGivenAMapMessageShouldLogTheMessageThaCameFromAQueue() throws JMSException {
        when(mapMessage.getString("Request")).thenReturn(REPLY);
        mapMessageListener.onMessage(mapMessage);
        loggerTest.verifyMessages("test request");
        verify(producer, times(1)).send(responseMessage);
    }

    /**
     * Method responsible for testing if method 'onMessage' has failed as expected given a mapMessage.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageGivenAMapMessageShouldFail() throws JMSException {
        when(mapMessage.getString("Request")).thenThrow(JMSException.class);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error while getting the message from a queue");
        mapMessageListener.onMessage(mapMessage);
    }

    /**
     * Method responsible for testing if the message has been logged as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageGivenAMapMessageShouldLogTheMessageThaCameFromATopic() throws JMSException {
        when(textMessage.getStringProperty("Request")).thenReturn(REPLY);
        mapMessageListener.onMessage(textMessage);
        loggerTest.verifyMessages("test request");
        verify(producer, times(1)).send(responseMessage);
    }

    /**
     * Method responsible for testing if the message has failed as expected given a textMessage.
     *
     * @throws JMSException Exception that is thrown in case the method 'getString' has failed
     */
    @Test
    public void onMessageGivenATextMessageShouldFail() throws JMSException {
        when(textMessage.getStringProperty("Request")).thenThrow(JMSException.class);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error while getting the message from a topic");
        mapMessageListener.onMessage(textMessage);
    }

    /**
     * Testing if the method answerRequest has failed as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'send' has failed
     */
    @Test
    public void answerRequestShouldFail() throws JMSException {
        when(textMessage.getStringProperty("Request")).thenReturn(REPLY);
        doThrow(JMSException.class).when(producer).send(responseMessage);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error while getting the message from a topic");
        expectedException.expectCause(is(instanceOf(MessageException.class)));
        mapMessageListener.onMessage(textMessage);
    }
}
