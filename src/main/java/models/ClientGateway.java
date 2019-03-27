package models;

import gateways.MessageSenderGateway;
import gateways.TopicGateway;

import javax.jms.*;

public abstract class ClientGateway implements MessageListener {

    private TopicGateway topicGateway;
    private MessageSenderGateway messageSenderGateway;
    private Client client;

    ClientGateway(Client client) {
        this.client = client;

    }

    void broadcastMessage(String message) {
        topicGateway.broadCast(message, client, this);
    }

    private void sendReturnMessage(String message, TextMessage request) {
        try {
            Long timeSend = request.getLongProperty("timeSend");
            messageSenderGateway.send(message, request.getJMSReplyTo(), timeSend);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    void onUpload() {
        topicGateway.changeFilter(client, this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String from = textMessage.getStringProperty("from");
            switch (from) {
                case "client":

                    Long timeReceived = System.currentTimeMillis();
                    Long timeSend = textMessage.getLongProperty("timeSend");

                    Long sendTime = timeReceived - timeSend;

                    onClientMessageReceived(textMessage.getText(), sendTime);
                    break;
                case "topic":
                    String keyword = textMessage.getStringProperty("keyword");
                    int results = onMatchesRequested(keyword);
                    sendReturnMessage("client " + client.getId() + " has " + results + " results", textMessage);
                    break;
                default:
                    break;
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setSession(Session session) {
        topicGateway = new TopicGateway(session);
        topicGateway.subscribe(client, this);

        messageSenderGateway = new MessageSenderGateway(session);
    }

    abstract void onClientMessageReceived(String message, Long sendTime);

    abstract int onMatchesRequested(String keyword);
}
