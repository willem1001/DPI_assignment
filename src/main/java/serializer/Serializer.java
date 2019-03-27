package serializer;

import com.owlike.genson.Genson;

import javax.jms.Session;

public class Serializer {
    public static String serialize(Session session) {
        return new Genson().serialize(session);
    }

    public static Session deserializer(String session) {
        return new Genson().deserialize(session, Session.class);
    }
}
