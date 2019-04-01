package gateways;

import messaging.MessageSender;
import messaging.TopicSender;
import models.AwaitingObj;
import models.Client;

import javax.jms.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClientGateway implements MessageListener {

    private TopicSender topicSender;
    private MessageSender messageSender;
    private Client client;
    private Map<String, AwaitingObj> awaiting = new HashMap<>();

    protected ClientGateway(Client client) {
        this.client = client;
    }

    public void broadcastMessage(String message) {
        awaiting.putAll(topicSender.broadCast(message, client, this));
    }

    public void onUpload() {
        topicSender.changeFilter(client, this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String from = textMessage.getStringProperty("from");
            switch (from) {
                case "client":
                    String correlationId = message.getJMSCorrelationID();
                    AwaitingObj awaitingObj = awaiting.get(correlationId);
                    if(awaitingObj.onReceive()) {
                        awaiting.remove(correlationId);
                    }
                    Long timeReceived = System.currentTimeMillis();
                    Long timeSend = textMessage.getLongProperty("timeSend");
                    Long sendTime = timeReceived - timeSend;
                    onClientMessageReceived(textMessage.getText(), sendTime);
                    break;
                case "topic":
                    String keyword = textMessage.getStringProperty("keyword");
                    List<String> results = onMatchesRequested(keyword);
                    StringBuilder builder = new StringBuilder();
                    builder.append("client " + client.getId() + " has " + results.size() + " results \n");
                    for (String result: results
                         ) {
                        builder.append(result);
                        builder.append(System.lineSeparator());
                    }
                    messageSender.send(builder.toString(), textMessage);
                    break;
                default:
                    break;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setSession(Session session) {
        topicSender = new TopicSender(session);
        topicSender.subscribe(client, this);
        messageSender = new MessageSender(session);
    }

    public abstract void onClientMessageReceived(String message, Long sendTime);

    public abstract List<String> onMatchesRequested(String keyword);
}
