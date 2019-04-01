package gateways;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class MessageSenderGateway {

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    public MessageSenderGateway(Session session) {
       /* try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            Context jndiContext = new InitialContext(props);
            connection = ConnectionFactoryGateway.getConnection(jndiContext);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
        } catch (JMSException | NamingException e) {
            e.printStackTrace();
        } */
        this.session = session;
    }

    public void send(String message, Destination destination, Long timeSend) {
        try {
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage m = session.createTextMessage(message);
            m.setText(message);
            m.setStringProperty("from", "client");
            m.setLongProperty("timeSend", timeSend);
            producer.send(m);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
