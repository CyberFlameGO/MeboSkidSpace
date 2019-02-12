package secondlife.network.vituz.managers;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import secondlife.network.vituz.Vituz;
import secondlife.network.vituz.VituzAPI;
import secondlife.network.vituz.punishments.redis.PunishSubscriber;
import secondlife.network.vituz.ranks.redis.RankSubscriber;
import secondlife.network.vituz.status.Server;
import secondlife.network.vituz.status.handlers.DataSubscriptionHandler;
import secondlife.network.vituz.status.handlers.ServerSubscriptionHandler;
import secondlife.network.vituz.status.jedis.JedisPublisher;
import secondlife.network.vituz.status.jedis.JedisSettings;
import secondlife.network.vituz.status.jedis.JedisSubscriber;
import secondlife.network.vituz.status.thread.UpdateThread;
import secondlife.network.vituz.utilties.Manager;
import secondlife.network.vituz.utilties.Msg;
import secondlife.network.vituz.utilties.Tasks;

import java.util.Arrays;
import java.util.List;

@Getter
public class DatabaseManager extends Manager {

	private MongoClient client;
	private MongoDatabase
			punishDatabase,
			ranksDatabase,
			essDatabase,
			crateDatabase,
			challengeDatabase,
			authmeDatabase,
			securityDatabase,
	        practiceDatabase,
			uhcDatabase,
	        hcfDatabase,
	        factionsDatabase,
	        meetupDatabase;
	private MongoCollection
			punishProfiles,
			ranksGrants,
			ranksProfiles,
			essData,
			crateData,
			challengeData,
			authmeProfiles,
	        securityProfiles,
	        practiceProfiles,
	        uhcProfiles,
	        uhcDeaths,
			hcfProfiles,
	        factions,
	        factionsProfiles,
	        meetupProfiles;

	private boolean crates, connected;
	private final boolean devMode = false;
	private final int port = 27017;
	private final String user = Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.USER");
	private final String local = "127.0.0.1";
	private final String dedihost = "137.74.4.87";
	private final char[] password = Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD").toCharArray();
	private final boolean auth = Vituz.getInstance().getConfig().getBoolean("DATABASE.AUTHENTICATION.ENABLED");

	private JedisSettings settings;
	private JedisPublisher publisher;
	private JedisSubscriber subscriber;
	private JedisSubscriber fisrtSubscriber;
	private JedisPublisher firstPublisher;
	private Server vituzServer;

	private JedisPool pool;
	private String addres;

	private PunishSubscriber punishSubscriber;
	private RankSubscriber rankSubscriber;

	public DatabaseManager(Vituz plugin) {
		super(plugin);

		connected = false;
		crates = Vituz.getInstance().getConfig().getBoolean("CRATES");

		addres = VituzAPI.getServerName().equalsIgnoreCase("Hub") ? local : dedihost;

		if(devMode) {
			String url = "mongodb://VISUAL:kurac@cluster0-shard-00-00-ishpi.mongodb.net:27017,cluster0-shard-00-01-ishpi.mongodb.net:27017,cluster0-shard-00-02-ishpi.mongodb.net:27017/admin?replicaSet=Cluster0-shard-0&ssl=true";

			MongoClientURI uri = new MongoClientURI(url);

			client = new MongoClient(uri);
		} else {
			if(auth) {
				List<MongoCredential> credentials = Arrays.asList(
						MongoCredential.createCredential(user, "punishments", password),
						MongoCredential.createCredential(user, "ranks", password),
						MongoCredential.createCredential(user, "essentials", password),
						MongoCredential.createCredential(user, "challenge", password),
						MongoCredential.createCredential(user, "crates", password),
						MongoCredential.createCredential(user, "practice", password),
						MongoCredential.createCredential(user, "security", password),
						MongoCredential.createCredential(user, "uhc", password),
						MongoCredential.createCredential(user, "authme", password),
				        MongoCredential.createCredential(user, "kitmap", password),
				        MongoCredential.createCredential(user, "uhcmeetup", password),
						MongoCredential.createCredential(user, "authme", password),
						MongoCredential.createCredential(user, "kitmap", password),
						MongoCredential.createCredential(user, "uhcmeetup", password),
				        MongoCredential.createCredential(user, "factions", password));
				if(VituzAPI.getServerName().equalsIgnoreCase("Hub")) {
					client = new MongoClient(new ServerAddress(local, port), credentials);
				} else {
					client = new MongoClient(new ServerAddress(dedihost, port), credentials);
				}
	        } else {
				if(VituzAPI.getServerName().equalsIgnoreCase("Hub")) {
					client = new MongoClient(new ServerAddress(local, port));
				} else {
					client = new MongoClient(new ServerAddress(dedihost, port));
				}
	        }
		}

		// punishments
		punishDatabase = client.getDatabase("punishments");
		punishProfiles = punishDatabase.getCollection("profiles");

		Msg.logConsole("&a&lPunishments database connected.");

		// ranks
		ranksDatabase = client.getDatabase("ranks");
		ranksGrants = ranksDatabase.getCollection("grants2");
		ranksProfiles = ranksDatabase.getCollection("profiles2");

		Msg.logConsole("&a&lRanks database connected.");

		// ess
		essDatabase = client.getDatabase("essentials");
		essData = essDatabase.getCollection("profile1");

		Msg.logConsole("&a&lEssentials database connected.");

		// challenge
		challengeDatabase = client.getDatabase("challenge");
		challengeData = challengeDatabase.getCollection("profiles");

		Msg.logConsole("&a&lChallenge database connected.");

		// crates
		if(crates) {
			crateDatabase = client.getDatabase("crates");
			crateData = crateDatabase.getCollection("data");

			Msg.logConsole("&a&lCrates database connected.");
		}

		practiceDatabase = client.getDatabase("practice");
		practiceProfiles = practiceDatabase.getCollection("profiles");

		Msg.logConsole("&a&lPractice database connected.");

		securityDatabase = client.getDatabase("security");
		securityProfiles = securityDatabase.getCollection("profiles");

		Msg.logConsole("&a&lSecurity database connected.");

		authmeDatabase = client.getDatabase("authme");
		authmeProfiles = authmeDatabase.getCollection("profiles");

		Msg.logConsole("&a&lAuthMe database connected.");

		uhcDatabase = client.getDatabase("uhc");
		uhcProfiles = uhcDatabase.getCollection("profiles");
		uhcDeaths = uhcDatabase.getCollection("deaths");

		Msg.logConsole("&a&lUHC database connected.");

		hcfDatabase = client.getDatabase("kitmap");
		hcfProfiles = hcfDatabase.getCollection("profiles");
		factions = hcfDatabase.getCollection("factions");

		Msg.logConsole("&a&lKitMap database connected.");

		meetupDatabase = client.getDatabase("uhcmeetup");
		meetupProfiles = meetupDatabase.getCollection("profiles");

		Msg.logConsole("&a&lUHC-Meetup database connected.");

		factionsDatabase = client.getDatabase("factions");
		factionsProfiles = factionsDatabase.getCollection("profiles");

		Msg.logConsole("&a&lFactions database connected.");

		Tasks.runLater(() -> {
				connected = true;
				Msg.logConsole("&4&lSuccessfully connected all databases to mongo!");
		}, 20L);
	}

	public void setupRedis() {
		pool = new JedisPool(addres);

		this.punishSubscriber = new PunishSubscriber();
		this.rankSubscriber = new RankSubscriber();
	}

	public void loadStatus() {
		vituzServer = new Server(VituzAPI.getServerName(), true);

		settings = new JedisSettings((VituzAPI.getServerName().equalsIgnoreCase("Hub")) ? local : dedihost, 6379,
				Vituz.getInstance().getConfig().getString("DATABASE.AUTHENTICATION.PASSWORD"));

		fisrtSubscriber = new JedisSubscriber(JedisSubscriber.INDEPENDENT, settings, new ServerSubscriptionHandler());
		firstPublisher = new JedisPublisher(settings);
		firstPublisher.start();

		subscriber = new JedisSubscriber(JedisSubscriber.BUKKIT, settings, new DataSubscriptionHandler());
		publisher = new JedisPublisher(settings);
		publisher.start();

		new UpdateThread().start();
	}
}
