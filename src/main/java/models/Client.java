package models;

import gateways.MessageSenderGateway;
import gateways.TopicGateway;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class Client implements MessageListener {

    private Map<String, Object> uploads = new HashMap<>();
    private TopicGateway topicGateway;
    private MessageSenderGateway messageSenderGateway;
    private int id;

    public Client(Session session, int id) {
        this.id = id;
        topicGateway = new TopicGateway(session);
        topicGateway.subscribe(this);

        messageSenderGateway = new MessageSenderGateway(session);
    }

    public void broadCast(String message) {
        topicGateway.broadCast(message, this);
    }

    public void upload(String keyword, Object object) {
        this.uploads.put(keyword, object);
        topicGateway.changeFilter(this);
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            if(textMessage.getStringProperty("from").equals("topic")) {
                System.out.println("client requests " + textMessage.getText());
                int counter = 0;
                String keyword = textMessage.getStringProperty("keyword");
                for (String key: this.uploads.keySet()
                     ) {
                    if(keyword.matches("(.*)" + key + "(.*)") || key.matches("(.*)" + keyword + "(.*)")) {
                        counter ++;
                    }
                }
                messageSenderGateway.send("client " + this.id + " has " + counter + " results ", textMessage.getJMSReplyTo());
            } else if (textMessage.getStringProperty("from").equals("client")) {
                System.out.println("received back " + textMessage.getText());
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return this.id;
    }

    public Map<String, Object> getUploads() {
        return this.uploads;
    }
}
