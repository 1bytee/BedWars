package dev.util;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dev.IceWars;
import org.bson.Document;

public class MongoConnection {

    private static final MongoClient client;

    static {
        client = new MongoClient("localhost");
    }

    public static MongoDatabase getDatabase(String name) {
        return client.getDatabase(name);
    }

    public static MongoCollection<Document> getCollection(String database, String name) {
        return getDatabase(database).getCollection(name);
    }

    public static Document info() {
        MongoCollection<Document> collection = getCollection("server", "iwinfo");

        return collection.find(Filters.eq("server", IceWars.SERVER)).first();
    }

}
