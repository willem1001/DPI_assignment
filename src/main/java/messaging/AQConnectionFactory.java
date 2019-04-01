package gateways;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.NamingException;


public class ConnectionFactoryGateway {

    private ConnectionFactoryGateway() {}

    public static Connection getConnection(Context jndiContext) throws NamingException, JMSException {
        ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) jndiContext.lookup("ConnectionFactory");
        connectionFactory.setTrustAllPackages(true);
        return connectionFactory.createConnection();
    }
}


