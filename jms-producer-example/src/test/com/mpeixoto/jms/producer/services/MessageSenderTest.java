package com.mpeixoto.jms.producer.services;

import com.mpeixoto.jms.producer.exception.MessageException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Class responsible for testing the MessageSender class and its methods.
 *
 * @author mpeixoto
 */
@RunWith(MockitoJUnitRunner.class)
public class MessageSenderTest {

    private MessageSender messageSender;
    @Mock
    private MessageProducer messageProducer;
    @Mock
    private MessageProducer producer;
    @Mock
    private MessageConsumer consumer;
    @Mock
    private Connection connection;
    @Captor
    private ArgumentCaptor<MapMessage> mapMessageArgumentCaptor;
    @Captor
    private ArgumentCaptor<MapMessageListener> mapMessageListenerArgumentCaptor;
    @Captor
    private ArgumentCaptor<TextMessage> textMessageArgumentCaptor;
    /**
     * Rule responsible for testing if the expected exceptions has been thrown.
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Method responsible for establishing a connections for the tests and for instantiating a messageSender before each test.
     */
    @Before
    public void setUp() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
        messageSender = new MessageSender(connectionFactory, producer, consumer, connection, messageProducer);
    }

    /**
     * Method responsible for testing if a message has been sent to a queue.
     *
     * @throws JMSException     Exception that is thrown in case the method 'send' has failed
     * @throws MessageException Exception that is thrown in case that the method 'sendMessageToAQueue' has failed
     */
    @Test
    public void sendMessageToAQueueGivenAMessage() throws JMSException, MessageException {
        doNothing().when(producer).send(mapMessageArgumentCaptor.capture());
        messageSender.sendMessageToAQueue("test");
        verify(producer, times(1)).send(mapMessageArgumentCaptor.getValue());
    }

    /**
     * Method responsible for testing if the exception has been thrown as expected.
     *
     * @throws JMSException     Exception that is thrown in case the method 'send' has failed
     * @throws MessageException Exception that is thrown in case that the method 'sendMessageToAQueue' has failed
     */
    @Test
    public void sendMessageToAQueueShouldThrownAMessageException() throws JMSException, MessageException {
        doThrow(new JMSException("Negative test")).when(producer).send(any(MapMessage.class));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("It was not possible to send the message to the queue");
        messageSender.sendMessageToAQueue("test");
    }

    /**
     * Method responsible for testing if the reception message is executing as well as expected.
     *
     * @throws JMSException     Exception that is thrown in case the method 'setMessageListener' has failed
     * @throws MessageException Exception that is thrown in case that the method 'receiveMessageFromQueue' has failed
     */
    @Test
    public void receiveMessageFromQueue() throws JMSException, MessageException {
        doNothing().when(consumer).setMessageListener(mapMessageListenerArgumentCaptor.capture());
        messageSender.receiveMessageFromQueue();
        verify(consumer, times(1)).setMessageListener(mapMessageListenerArgumentCaptor.getValue());
        verify(connection, times(1)).start();
    }

    /**
     * Method responsible for testing if the reception message has failed as expected.
     *
     * @throws JMSException     Exception that is thrown in case the method 'setMessageListener' has failed
     * @throws MessageException Exception that is thrown in case that the method 'receiveMessageFromQueue' has failed
     */
    @Test
    public void receiveMessageFromQueueShouldThrownAMessageException() throws JMSException, MessageException {
        doThrow(new JMSException("Negative test")).when(consumer).setMessageListener(any(MapMessageListener.class));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("It was not possible to receive the message");
        messageSender.receiveMessageFromQueue();
    }

    /**
     * Method responsible for testing if a message has been sent to a topic.
     *
     * @throws JMSException     Exception that is thrown in case the method 'send' has failed
     * @throws MessageException Exception that is thrown in case that the method 'sendMessageToATopic' has failed
     */
    @Test
    public void sendMessageToATopicAMessage() throws JMSException, MessageException {
        doNothing().when(messageProducer).send(textMessageArgumentCaptor.capture());
        messageSender.sendMessageToATopic("test");
        verify(messageProducer, times(1)).send(textMessageArgumentCaptor.getValue());
    }

    /**
     * Method responsible for testing if the exception has been thrown as expected.
     *
     * @throws JMSException     Exception that is thrown in case the method 'send' has failed
     * @throws MessageException Exception that is thrown in case that the method 'sendMessageToATopic' has failed
     */
    @Test
    public void sendMessageToATopicAMessageShouldThrownAMessageException() throws MessageException, JMSException {
        doThrow(new JMSException("Negative test")).when(messageProducer).send(any(TextMessage.class));
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("It was not possible to send the message to the topic");
        messageSender.sendMessageToATopic("test");
    }
}
