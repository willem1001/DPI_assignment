package models;

import gateways.ClientGateway;
import gateways.ReaderWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private Map<String, String> uploads = new HashMap<>();
    private ClientGateway clientGateway;
    private int id;
    private int maxResults = 5;

    public Client(int id) {
        this.id = id;
        clientGateway = new ClientGateway(this) {
            @Override
            public void onClientMessageReceived(String message, Long sendTime) {
                System.out.println("received back " + message + " in " + sendTime + " miliseconds");
            }

            @Override
            public List<String> onMatchesRequested(String keyword) {
                List<String> results = new ArrayList<>();
                for (String key : uploads.keySet()
                ) {
                    if (keyword.matches("(.*)" + key + "(.*)") || key.matches("(.*)" + keyword + "(.*)")) {
                        results.add(uploads.get(key));
                    }
                }
                return results;
            }
        };
    }

    public void broadCast(String message) {
        clientGateway.broadcastMessage(message);
    }

    public void upload(String keyword, String fileName) {
        this.uploads.put(keyword, ReaderWriter.read(fileName));
        clientGateway.onUpload();
    }

    public int getId() {
        return this.id;
    }

    public Map<String, String> getUploads() {
        return this.uploads;
    }

    public ClientGateway getClientGateway() {
        return this.clientGateway;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
