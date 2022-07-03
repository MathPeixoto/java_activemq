package com.mpeixoto.jms.producer.services;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class responsible for implementing the method that will be listening to a queue.
 *
 * @author mpeixoto
 */
public class MapMessageListener implements MessageListener {

  private static final Logger LOGGER = LogManager.getLogger(MapMessageListener.class);

  /**
   * Method that will be listening to a queue waiting for a given message.
   *
   * @param message the message that was received by a queue
   */
  @Override
  public void onMessage(Message message) {
    if (message instanceof MapMessage) {
      MapMessage mapMessage = (MapMessage) message;
      try {
        LOGGER.info(mapMessage.getString("Request"));
        LOGGER.info(mapMessage.getString("Response"));
      } catch (JMSException e) {
        throw new RuntimeException("error", e);
      }
    } else {
      LOGGER.error("Invalid Message Received");
    }
  }
}
