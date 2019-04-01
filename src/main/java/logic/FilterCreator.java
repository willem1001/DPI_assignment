package logic;

import models.Client;
import java.util.Iterator;
import java.util.Map;

public abstract class FilterCreator {

    private FilterCreator() {}

    public static String createFilter(Client client) {

        Map<String, String> uploads = client.getUploads();

        StringBuilder filter = new StringBuilder( "NOT(senderId = " + client.getId() + ")");
        if (uploads.size() > 0) {
            filter.append(" AND (");
            Iterator iterator = uploads.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                filter.append("(keyword LIKE '%").append(entry.getKey()).append("%')");
                if (iterator.hasNext()) {
                    filter.append(" OR ");
                }
            }
            filter.append(")");
        } else {
            filter.append(" AND (keyword IS NULL)");
        }
        return filter.toString();
    }
}
