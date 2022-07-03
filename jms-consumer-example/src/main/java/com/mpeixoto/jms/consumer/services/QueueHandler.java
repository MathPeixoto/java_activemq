package com.mpeixoto.jms.consumer.services;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Abstract class responsible for providing usual features to its children.
 *
 * @author mpeixoto
 */
abstract class QueueHandler {
  protected Destination destination;
  protected Topic topic;
  protected Connection connection = null;
  protected Session session;
  protected String queueName;

  protected void createConnection(ConnectionFactory connectionFactory) {

    try {
      ConnectionFactory activeMQConnectionFactory;
      if (connectionFactory == null)
        activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
      else {
        activeMQConnectionFactory = connectionFactory;
      }
      connection = activeMQConnectionFactory.createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      topic = session.createTopic("topicJms");
      destination = session.createQueue(queueName);
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }

  protected void setQueueName(String queueName) {
    this.queueName = queueName;
  }
}
