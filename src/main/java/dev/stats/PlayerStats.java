package dev.stats;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.IceWars;
import dev.util.MongoConnection;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

@Getter
@Setter
public class PlayerStats {

    private static final DecimalFormat format;

    static {
        format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.CEILING);
    }

    private final UUID uuid;
    private int kills = 0;
    private int deaths = 0;
    private int victories = 0;
    private int gamesPlayed = 0;
    private int destroyedIceBlocks = 0;
    private long updated;
    private String name;

    public PlayerStats(Player p) {
        this(p.getName());
    }

    public PlayerStats(String name) {
        this.name = name;
        this.uuid = IceWars.getUUID(name);
    }

    public double getKD() {
        return deaths == 0 ? 0 : Double.valueOf(format.format(kills / deaths));
    }

    public void addKill() {
        setKills(getKills() + 1);
        updated = System.currentTimeMillis();
    }

    public void addDeath() {
        setDeaths(getDeaths() + 1);
        updated = System.currentTimeMillis();
    }

    public void addVictory() {
        setVictories(getVictories() + 1);
        updated = System.currentTimeMillis();
    }

    public void addGamesPlayed() {
        setGamesPlayed(getGamesPlayed() + 1);
        updated = System.currentTimeMillis();
    }

    public void addDestroyedIceBlock() {
        setDestroyedIceBlocks(getDestroyedIceBlocks() + 1);
        updated = System.currentTimeMillis();
    }
    public boolean loadStats() {
        MongoCollection<Document> collection = MongoConnection.getCollection("icewars", "stats");
        Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
        updated = System.currentTimeMillis();
        if (doc != null) {
            kills = doc.getInteger("kills");
            deaths = doc.getInteger("deaths");
            victories = doc.getInteger("victories");
            gamesPlayed = doc.getInteger("gamesPlayed");
            destroyedIceBlocks = doc.getInteger("destroyedBeds");
            return true;
        }
        return false;
    }

    public void saveStats() {
        MongoCollection<Document> collection = MongoConnection.getCollection("icewars", "stats");

        Document doc = new Document("uuid", uuid.toString())
                .append("kills", kills)
                .append("deaths", deaths)
                .append("victories", victories)
                .append("gamesPlayed", gamesPlayed)
                .append("destroyedBeds", destroyedIceBlocks);

        if (collection.find(Filters.eq("uuid", uuid.toString())).first() == null) {
            collection.insertOne(doc);
        } else {
            collection.replaceOne(Filters.eq("uuid", uuid.toString()), doc);
        }
    }
}
