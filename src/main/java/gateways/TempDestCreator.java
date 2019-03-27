package gateways;

import models.Client;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;
import static gateways.ConnectionFactoryGateway.getConnection;

public class TempDestCreator {

    public static Destination createTempDest(MessageListener messageListener, Session session) {
        try {
            /*Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");

            Context jndiContext = new InitialContext(props);
            Connection connection = getConnection(jndiContext);
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); */

            Destination tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(messageListener);
            //connection.start();
            return tempDest;
        }catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }
}
