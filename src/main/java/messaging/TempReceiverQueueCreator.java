package messaging;

import models.AwaitingObj;

import javax.jms.*;

class TempReceiverQueueCreator {
    private TempReceiverQueueCreator() {}

    static TemporaryQueue createTempDest(MessageListener messageListener, Session session, AwaitingObj awaitingObj) {
        try {
            TemporaryQueue tempDest = session.createTemporaryQueue();
            MessageConsumer responseConsumer = session.createConsumer(tempDest);
            responseConsumer.setMessageListener(messageListener);
            awaitingObj.setConsumer(responseConsumer);
            awaitingObj.setQueue(tempDest);
            return tempDest;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }
}
