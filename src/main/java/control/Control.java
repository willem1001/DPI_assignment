package control;

import gateways.ConnectionFactoryGateway;
import models.Client;

import javax.jms.Connection;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Control {

    public static void main(String args[]) {

        Scanner in = new Scanner(System.in);
        ArrayList<Client> clients = new ArrayList<>();
        Session session = null;
        int globalId = 1;
        int index;
        String keyword;
        boolean isReading = true;

        try {
            Properties props = new Properties();
            props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                    "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            Context jndiContext = new InitialContext(props);
            Connection connection = ConnectionFactoryGateway.getConnection(jndiContext);
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
                        Long timeStart = new Date().getTime();
                        int i = in.nextInt();
                        for (int j = 0; j < i; j++) {
                            Client client = new Client(session, globalId);
                            globalId++;
                            clients.add(client);

                        }
                        Long timeEnd = new Date().getTime();
                        System.out.println("created " + i + " clients in " + (timeEnd - timeStart)/1000 + " seconds");
                        break;
                    case "broadcast":
                        System.out.println("choose between 0 and " + (clients.size() - 1));
                        index = in.nextInt();
                        System.out.println("keyword:");
                        in.nextLine();
                        keyword = in.nextLine();
                        clients.get(index).broadCast(keyword);
                        System.out.println("client " + index + " broadcasted " + keyword);
                        break;
                    case "upload":
                        System.out.println("choose between 0 and " + (clients.size() - 1));
                        index = in.nextInt();
                        System.out.println("keyword:");
                        in.nextLine();
                        keyword = in.nextLine();
                        clients.get(index).upload(keyword, new Object());
                        System.out.println("client " + index + " uploaded " + keyword);
                        break;
                    case "exit":
                        isReading = false;
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
