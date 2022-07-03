package com.mpeixoto.jms.producer.services;

import com.mpeixoto.jms.producer.exception.MessageException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Class responsible for implementing the methods that will establish a communication.
 *
 * @author mpeixoto
 */
public class MessageSender {
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
  private Connection connection = null;
  private MessageConsumer consumer;
  private MessageProducer producer;
  private Session session;
  private MessageProducer messageProducer;

  /** Default constructor of the class. */
  public MessageSender() {
    ConnectionFactory activeMQConnectionFactory =
        new ActiveMQConnectionFactory("tcp://localhost:61616");
    createConnection(activeMQConnectionFactory);
  }

  /**
   * Constructor that is used only for tests.
   *
   * @param activeMQConnectionFactory Type: ConnectionFactory
   * @param producer Type: MessageProducer
   * @param consumer Type: MessageConsumer
   * @param connection Type: Connection
   * @param messageProducer Type: MessageProducer
   */
  MessageSender(
      ConnectionFactory activeMQConnectionFactory,
      MessageProducer producer,
      MessageConsumer consumer,
      Connection connection,
      MessageProducer messageProducer) {
    createConnection(activeMQConnectionFactory);
    this.producer = producer;
    this.consumer = consumer;
    this.connection = connection;
    this.messageProducer = messageProducer;
  }

  /**
   * Method responsible for setting everything to establish the communication.
   *
   * @param activeMQConnectionFactory Type: ConnectionFactory
   */
  private void createConnection(ConnectionFactory activeMQConnectionFactory) {

    try {
      connection = activeMQConnectionFactory.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination request = session.createQueue("request.message.queue");
      Destination response = session.createQueue("response.message.queue");
      consumer = session.createConsumer(response);
      producer = session.createProducer(request);
      Topic topic = session.createTopic("topicJms");
      messageProducer = session.createProducer(topic);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method responsible for sending a message to a queue.
   *
   * @param message The message that will be sent to the queue
   * @throws MessageException It's thrown in case it wasn't possible to send the message
   */
  public void sendMessageToAQueue(String message) throws MessageException {

    try {
      MapMessage mapMessage = session.createMapMessage();
      mapMessage.setString(
          "Request", message + simpleDateFormat.format(new Date(System.currentTimeMillis())));
      producer.send(mapMessage);
    } catch (Exception e) {
      throw new MessageException("It was not possible to send the message to the queue", e);
    }
  }

  /**
   * Method responsible for receive messages from a queue.
   *
   * @throws MessageException It's thrown in case it wasn't possible to receive the message
   */
  public void receiveMessageFromQueue() throws MessageException {

    try {
      MapMessageListener mapMessageListener = new MapMessageListener();
      consumer.setMessageListener(mapMessageListener);
      connection.start();
    } catch (Exception e) {
      throw new MessageException("It was not possible to receive the message", e);
    }
  }

  /**
   * Method responsible for sending a message to a topic.
   *
   * @param message The message that will be sent to the topic
   * @throws MessageException It's thrown in case it wasn't possible to send the message
   */
  public void sendMessageToATopic(String message) throws MessageException {

    try {
      TextMessage textMessage = session.createTextMessage();
      textMessage.setStringProperty(
          "Request", message + simpleDateFormat.format(new Date(System.currentTimeMillis())));
      messageProducer.send(textMessage);
    } catch (JMSException e) {
      throw new MessageException("It was not possible to send the message to the topic", e);
    }
  }
}
