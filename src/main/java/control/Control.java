package control;

import messaging.AQConnectionFactory;
import models.Client;
import javax.jms.Connection;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.*;

public class Control {

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        ArrayList<Client> clients = new ArrayList<>();
        Session session = null;
        int globalId = 1;
        int index;
        int count;
        String keyword;
        boolean isReading = true;

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            Context jndiContext = new InitialContext(props);
            Connection connection = AQConnectionFactory.getConnection(jndiContext);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("ready");
        while (isReading) {
            String s = in.nextLine();
            try {
                switch (s) {
                    case "create":
                        System.out.println("amount:");
                        int i = in.nextInt();
                        Long timeStart = System.currentTimeMillis();
                        for (int j = 0; j < i; j++) {
                            Client client = new Client(globalId);
                            client.getClientGateway().setSession(session);
                            globalId++;
                            clients.add(client);
                        }
                        Long timeEnd = System.currentTimeMillis();
                        System.out.println("created " + i + " clients in " + (timeEnd - timeStart) + " miliseconds");
                        break;
                    case "broadcast":
                        System.out.println("choose between 1 and " + (clients.size()));
                        index = in.nextInt() - 1;
                        System.out.println("keyword:");
                        in.nextLine();
                        keyword = in.nextLine();
                        clients.get(index).broadCast(keyword);
                        System.out.println("client " + (index + 1) + " broadcasted " + keyword);
                        break;
                    case "upload":
                        System.out.println("choose between 1 and " + (clients.size()));
                        index = in.nextInt() - 1;
                        System.out.println("keyword:");
                        in.nextLine();
                        keyword = in.nextLine();
                        clients.get(index).upload(keyword, "E:\\GitE\\DPI_assignment\\src\\main\\java\\models\\Upload.txt");
                        System.out.println("client " + (index + 1) + " uploaded " + keyword);
                        break;
                    case "exit":
                        isReading = false;
                        break;
                    case "randomUpload":
                        System.out.println("amount:");
                        count = in.nextInt();
                        for (int j = 0; j < count; j++) {
                            int rand = new Random().nextInt(clients.size());
                            clients.get(rand).upload("hoi", "E:\\GitE\\DPI_assignment\\src\\main\\java\\models\\Upload.txt");
                        }
                        System.out.println("done uploading");
                        break;
                    case "":
                        break;
                    default:
                        System.out.println("unknown command");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("not a valid input");
                in.nextLine();
            }
        }
        System.exit(0);
    }
}
