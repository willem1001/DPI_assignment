package models;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TemporaryQueue;

public class AwaitingObj {
    private TemporaryQueue queue;
    private MessageConsumer consumer;
    private int receivedCount;
    private int maxResults;

    public AwaitingObj(int maxResults) {
        this.maxResults = maxResults;
    }

    private void close(){
        try {
            consumer.close();
            queue.delete();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public boolean onReceive() {
        receivedCount ++;
        if(receivedCount >= maxResults) {
            close();
            return true;
        }
        return false;
    }

    public TemporaryQueue getQueue() {
        return queue;
    }

    public void setQueue(TemporaryQueue queue) {
        this.queue = queue;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
