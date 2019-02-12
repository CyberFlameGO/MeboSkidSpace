package secondlife.network.meetupgame.utilities.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bukkit.configuration.file.FileConfiguration;
import secondlife.network.meetupgame.MeetupGame;

import java.util.Arrays;

/**
 * Created by Marko on 11.06.2018.
 */
public class MeetupDatabase {

    public static MongoClient client;
    public static MongoDatabase meetupDatabase;
    public static MongoCollection profiles;
    public static boolean devMode;

    public MeetupDatabase() {
        devMode = false;

        FileConfiguration config = MeetupGame.getInstance().getMainConfig().getConfiguration();

        if(devMode) {
            String url = "mongodb://VISUAL:kurac@cluster0-shard-00-00-ishpi.mongodb.net:27017,cluster0-shard-00-01-ishpi.mongodb.net:27017,cluster0-shard-00-02-ishpi.mongodb.net:27017/admin?replicaSet=Cluster0-shard-0&ssl=true";

            MongoClientURI uri = new MongoClientURI(url);

            client = new MongoClient(uri);
        } else {
            if(config.getBoolean("DATABASE.AUTHENTICATION.ENABLED")) {
                client = new MongoClient(
                        new ServerAddress(
                                config.getString("DATABASE.HOST"),
                                config.getInt("DATABASE.PORT")),
                        Arrays.asList(MongoCredential.createCredential(
                                config.getString("DATABASE.AUTHENTICATION.USER"),
                                config.getString("DATABASE.AUTHENTICATION.DATABASE"),
                                config.getString("DATABASE.AUTHENTICATION.PASSWORD").toCharArray())));
            } else {
                client = new MongoClient(new ServerAddress(
                        config.getString("DATABASE.HOST"),
                        config.getInt("DATABASE.PORT")));
            }
        }

        // uhcmeetup
        meetupDatabase = client.getDatabase("uhcmeetup");
        profiles = meetupDatabase.getCollection("profiles");
    }
}
