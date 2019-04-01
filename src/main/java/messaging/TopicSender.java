package gateways;

import models.Client;
import javax.jms.*;
import java.util.*;

public class TopicGateway {

    private Session session = null;
    private Topic topic = null;
    private MessageConsumer consumer;
    private String baseFilter;

    public TopicGateway(Session session) {
        try {
            /*Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            Context jndiContext = new InitialContext(props);
            connection = ConnectionFactoryGateway.getConnection(jndiContext);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();*/

            this.session = session;
            topic = session.createTopic("search");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(Client client, MessageListener messageListener) {
        try {
            baseFilter = "NOT(senderId = " + client.getId() + ")";
            String filter = baseFilter;
            filter += " AND (keyword IS NULL)";
            consumer = session.createConsumer(topic, filter);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void changeFilter(Client client, MessageListener messageListener) {
        try {
            Map<String, Object> uploads = client.getUploads();
            consumer.close();
            StringBuilder filter = new StringBuilder(baseFilter);
            if (uploads.size() > 0) {
                filter.append(" AND (");
                Iterator iterator = uploads.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    filter.append("(keyword LIKE '%").append(entry.getKey()).append("%')");
                    if (iterator.hasNext()) {
                        filter.append(" OR ");
                    }
                }
                filter.append(")");
            }

            consumer = session.createConsumer(topic, filter.toString());
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void broadCast(String message, Client client, MessageListener messageListener) {
        try {
            Destination temp = TempDestCreator.createTempDest(messageListener, session);
            TextMessage m = session.createTextMessage(message);

            m.setJMSReplyTo(temp);
            m.setJMSCorrelationID(createRandomString());
            m.setStringProperty("from", "topic");
            m.setIntProperty("senderId", client.getId());
            m.setStringProperty("keyword", message);
            m.setLongProperty("timeSend", System.currentTimeMillis());

            MessageProducer producer = session.createProducer(topic);
            producer.send(m);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    private String createRandomString() {
        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();
        return Long.toHexString(randomLong);
    }

}
