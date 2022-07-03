package com.mpeixoto.jms.consumer.services;

import com.mpeixoto.jms.consumer.exception.MessageException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Class responsible for Testing the MessageReceiver class and its methods.
 *
 * @author mpeixoto
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageReceiverTest {
    @Mock
    private Connection connection;
    @Mock
    private MessageConsumer consumerQueue;
    @Mock
    private MessageConsumer consumerTopic;
    @Captor
    private ArgumentCaptor<MapMessageListener> mapMessageListenerArgumentCaptor;
    /**
     * Rule responsible for testing if the expected exceptions has been thrown.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private MessageReceiver messageReceiver;

    /**
     * Method responsible for setting everything before each test.
     */
    @Before
    public void setUp() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        messageReceiver = new MessageReceiver(connectionFactory, consumerQueue, consumerTopic, connection);
        MessageReceiver.setAnswer("This is a Response Message ");
    }

    /**
     * Method responsible for verifying if the receiveMessageFromQueue method's execution is running as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'setMessageListener' or 'start' has failed
     */
    @Test
    public void receiveMessageFromQueue() throws JMSException, MessageException {
        doNothing().when(consumerQueue).setMessageListener(mapMessageListenerArgumentCaptor.capture());
        messageReceiver.receiveMessageFromQueue();
        verify(consumerQueue, times(1)).setMessageListener(mapMessageListenerArgumentCaptor.getValue());
        verify(connection, times(1)).start();
    }

    /**
     * Method responsible for testing if the receiveMessageFromQueue method has failed as expected.
     *
     * @throws JMSException     Exception that is thrown in case the method 'setMessageListener' has failed
     * @throws MessageException Exception that is thrown in case that the method 'receiveMessageFromQueue' has failed
     */
    @Test
    public void receiveMessageFromQueueShouldThrownAMessageException() throws JMSException, MessageException {
        doThrow(new JMSException("Negative test")).when(consumerQueue).setMessageListener(mapMessageListenerArgumentCaptor.capture());
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("It was not possible to receive the message from a queue");
        messageReceiver.receiveMessageFromQueue();
    }

    /**
     * Method responsible for verifying if the receiveMessageFromTopic method's execution is running as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'setMessageListener' or 'start' has failed
     */
    @Test
    public void receiveMessageFromTopic() throws JMSException, MessageException {
        doNothing().when(consumerTopic).setMessageListener(mapMessageListenerArgumentCaptor.capture());
        messageReceiver.receiveMessageFromTopic();
        verify(consumerTopic, times(1)).setMessageListener(mapMessageListenerArgumentCaptor.getValue());
        verify(connection, times(1)).start();
    }

    /**
     * Method responsible for verifying if the receiveMessageFromTopic method has failed as expected.
     *
     * @throws JMSException Exception that is thrown in case the method 'setMessageListener' or 'start' has failed
     */
    @Test
    public void receiveMessageFromTopicShouldThrownAMessageException() throws JMSException, MessageException {
        doThrow(new JMSException("Negative test")).when(consumerTopic).setMessageListener(mapMessageListenerArgumentCaptor.capture());
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("It was not possible to receive the message from a topic");
        messageReceiver.receiveMessageFromTopic();
    }
}
