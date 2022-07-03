package com.mpeixoto.jms.consumer.services;

import com.mpeixoto.jms.consumer.exception.MessageException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class responsible for implementing the method that will be listening to a queue and a topic.
 *
 * @author mpeixoto
 */
public class MapMessageListener extends QueueHandler implements MessageListener {
    private static final Logger LOGGER = LogManager.getLogger(MapMessageListener.class);
    private static final String QUEUE_NAME_RESPONSE = "response.message.queue";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    private MessageProducer producer;
    private MapMessage responseMessage;

    /**
     * Default constructor of the class.
     */
    public MapMessageListener() {
        setQueueName(QUEUE_NAME_RESPONSE);
        createConnection(null);
        try {
            producer = session.createProducer(destination);
            responseMessage = session.createMapMessage();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that is used only for tests.
     *
     * @param activeMQConnectionFactory Type: ConnectionFactory
     */
    MapMessageListener(ConnectionFactory activeMQConnectionFactory) {
        setQueueName(QUEUE_NAME_RESPONSE);
        createConnection(activeMQConnectionFactory);
    }

    /**
     * Method that will be listening to a queue/topic waiting for a given message.
     *
     * @param message the message that was received by a queue/topic
     */
    @Override
    public void onMessage(Message message) {
        String replyRequest;

        // listen to a queue
        if (message instanceof MapMessage) {
            MapMessage mapMessage = (MapMessage) message;
            try {
                replyRequest = mapMessage.getString("Request");
                LOGGER.info(replyRequest);
                answerRequest(replyRequest);
            } catch (JMSException | MessageException e) {
                e.printStackTrace();
                throw new RuntimeException("Error while getting the message from a queue", e);
            }
        }

        // listen to a topic
        else if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                replyRequest = textMessage.getStringProperty("Request");
                LOGGER.info(replyRequest);
                answerRequest(replyRequest);
            } catch (JMSException | MessageException e) {
                e.printStackTrace();
                throw new RuntimeException("Error while getting the message from a topic", e);
            }
        } else {
            LOGGER.error("Invalid Message Received");
        }
    }

    private void answerRequest(String replyRequest) throws MessageException {
        try {
            responseMessage.setString("Request", "Request : " + replyRequest);
            responseMessage.setString(
                    "Response",
                    "Response : "
                            + MessageReceiver.getAnswer()
                            + simpleDateFormat.format(Clock.systemDefaultZone().millis()));
            producer.send(responseMessage);
        } catch (JMSException e) {
            e.printStackTrace();
            throw new MessageException("Error sending a response to the producer", e);
        }
    }

    void setResponseMessage(MapMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    void setProducer(MessageProducer producer) {
        this.producer = producer;
    }
}
