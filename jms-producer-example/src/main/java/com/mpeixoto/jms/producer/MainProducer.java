package com.mpeixoto.jms.producer;

import com.mpeixoto.jms.producer.exception.MessageException;
import com.mpeixoto.jms.producer.services.MessageSender;

/**
 * The main class.
 *
 * @author mpeixoto
 */
public class MainProducer {

  /**
   * The main method.
   *
   * @param args Type: Array of String
   * @throws MessageException It's thrown if an error occurred during a communication
   */
  public static void main(String[] args) throws MessageException {

    MessageSender messageSender = new MessageSender();
    messageSender.receiveMessageFromQueue();
    messageSender.sendMessageToAQueue("This is a Request Message to a queue ");
    messageSender.sendMessageToATopic("This is a Request Message to a topic ");
  }
}
