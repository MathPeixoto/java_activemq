package com.mpeixoto.jms.consumer;

import com.mpeixoto.jms.consumer.services.MessageReceiver;
import java.net.URI;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

/**
 * The main class.
 *
 * @author mpeixoto
 */
public class MainConsumer {

  /**
   * The main method.
   *
   * @param args Type: Array of String
   * @throws Exception It's thrown if an error occurred during a communication
   */
  public static void main(String[] args) throws Exception {

    BrokerService broker = BrokerFactory.createBroker(new URI("broker:tcp://localhost:61616"));
    broker.start();

    String answer = "This is a Response Message ";
    MessageReceiver messageReceiver = new MessageReceiver();
    MessageReceiver.setAnswer(answer);
    messageReceiver.receiveMessageFromQueue();
    messageReceiver.receiveMessageFromTopic();
  }
}
