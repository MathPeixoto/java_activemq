package com.mpeixoto.jms.consumer.services;

import com.mpeixoto.jms.consumer.exception.MessageException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

/**
 * Class responsible for implementing the communication with the producer.
 *
 * @author mpeixoto
 */
public class MessageReceiver extends QueueHandler {
  private static final String QUEUE_NAME_REQUEST = "request.message.queue";
  private final MapMessageListener mapMessageListener;
  private MessageConsumer consumerQueue;
  private MessageConsumer consumerTopic;
  private static String answer;

  /** Default constructor of the class. */
  public MessageReceiver() {
    setQueueName(QUEUE_NAME_REQUEST);
    createConnection(null);
    mapMessageListener = new MapMessageListener();
    try {
      consumerQueue = session.createConsumer(destination);
      consumerTopic = session.createConsumer(topic);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method that is used only for tests.
   *
   * @param activeMQConnectionFactory Type: ConnectionFactory
   * @param consumerQueue Type: MessageConsumer
   * @param consumerTopic Type: MessageConsumer
   * @param connection Type: Connection
   */
  MessageReceiver(
      ConnectionFactory activeMQConnectionFactory,
      MessageConsumer consumerQueue,
      MessageConsumer consumerTopic,
      Connection connection) {
    setQueueName(QUEUE_NAME_REQUEST);
    createConnection(activeMQConnectionFactory);
    mapMessageListener = new MapMessageListener(activeMQConnectionFactory);
    this.consumerQueue = consumerQueue;
    this.consumerTopic = consumerTopic;
    super.connection = connection;
  }

  /**
   * Method responsible for receive messages from a queue.
   *
   * @throws MessageException It's thrown in case it wasn't possible to receive the message
   */
  public void receiveMessageFromQueue() throws MessageException {

    try {
      connection.start();
      consumerQueue.setMessageListener(mapMessageListener);
    } catch (Exception e) {
      e.printStackTrace();
      throw new MessageException("It was not possible to receive the message from a queue", e);
    }
  }

  /**
   * Method responsible for receive messages from a topic.
   *
   * @throws MessageException It's thrown in case it wasn't possible to receive the message
   */
  public void receiveMessageFromTopic() throws MessageException {

    try {
      connection.start();
      consumerTopic.setMessageListener(mapMessageListener);
    } catch (JMSException e) {
      e.printStackTrace();
      throw new MessageException("It was not possible to receive the message from a topic", e);
    }
  }

  /**
   * Set the default answer.
   *
   * @param answer The default answer
   */
  public static void setAnswer(String answer) {
    MessageReceiver.answer = answer;
  }

  /**
   * Get the default answer.
   *
   * @return String
   */
  public static String getAnswer() {
    return answer;
  }
}
