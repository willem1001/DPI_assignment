package models;

import java.util.HashMap;
import java.util.Map;

public class Client {

    private Map<String, Object> uploads = new HashMap<>();
    private ClientGateway clientGateway;
    private int id;

    public Client(int id) {
        this.id = id;
        clientGateway = new ClientGateway(this) {
            @Override
            public void onClientMessageReceived(String message, Long sendTime) {
                System.out.println("received back " + message + " in " + sendTime + " miliseconds");
            }

            @Override
            public int onMatchesRequested(String keyword) {
                int counter = 0;
                for (String key : uploads.keySet()
                ) {
                    if (keyword.matches("(.*)" + key + "(.*)") || key.matches("(.*)" + keyword + "(.*)")) {
                        counter++;
                    }
                }
                return counter;
            }
        };
    }

    public void broadCast(String message) {
        clientGateway.broadcastMessage(message);
    }

    public void upload(String keyword, Object object) {
        this.uploads.put(keyword, object);
        clientGateway.onUpload();
    }

    public int getId() {
        return this.id;
    }

    public Map<String, Object> getUploads() {
        return this.uploads;
    }

    public ClientGateway getClientGateway() {
        return this.clientGateway;
    }
}
