package messaging;

import javax.jms.*;

class TempQueueCreator {
    private TempQueueCreator() {}

    static TemporaryQueue createTempDest(MessageListener messageListener, Session session) {
        try {
            TemporaryQueue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(messageListener);
            return tempDest;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }
}
