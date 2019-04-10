package messaging;

import javax.jms.*;

public class MessageSender {
    private Session session;

    public MessageSender(Session session) {
        this.session = session;
    }

    public void send(String message, TextMessage received) {
        try {
            TemporaryQueue temporaryQueue = (TemporaryQueue) received.getJMSReplyTo();
            MessageProducer producer = session.createProducer(temporaryQueue);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage m = session.createTextMessage(message);
            m.setText(message);
            m.setStringProperty("from", "client");
            m.setLongProperty("timeSend", received.getLongProperty("timeSend"));
            m.setJMSCorrelationID(received.getJMSMessageID());
            producer.send(m);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
