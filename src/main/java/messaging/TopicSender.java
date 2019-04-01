package messaging;

import logic.FilterCreator;
import models.AwaitingObj;
import models.Client;
import javax.jms.*;
import java.util.*;

public class TopicSender {
    private Session session = null;
    private Topic topic = null;
    private MessageConsumer consumer;
    private MessageProducer producer;

    public TopicSender(Session session) {
        try {
            this.session = session;
            this.topic = session.createTopic("search");
            this.producer = session.createProducer(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(Client client, MessageListener messageListener) {
        try {
            String filter = FilterCreator.createFilter(client);
            consumer = session.createConsumer(topic, filter);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void changeFilter(Client client, MessageListener messageListener) {
        try {
            consumer.close();
            String filter = FilterCreator.createFilter(client);
            consumer = session.createConsumer(topic, filter);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public Map<String, AwaitingObj> broadCast(String message, Client client, MessageListener messageListener) {
        try {
            AwaitingObj awaitingObj = new AwaitingObj(client.getMaxResults());
            TemporaryQueue temp = TempReceiverQueueCreator.createTempDest(messageListener, session, awaitingObj);
            TextMessage m = session.createTextMessage(message);
            m.setJMSReplyTo(temp);
            m.setStringProperty("from", "topic");
            m.setIntProperty("senderId", client.getId());
            m.setStringProperty("keyword", message);
            m.setLongProperty("timeSend", System.currentTimeMillis());

            producer.send(m);
            Map<String, AwaitingObj> map = new HashMap<>();
            map.put(m.getJMSMessageID(), awaitingObj);
            return map;

        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }
}
