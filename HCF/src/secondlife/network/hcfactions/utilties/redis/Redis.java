package secondlife.network.hcfactions.utilties.redis;

import redis.clients.jedis.Jedis;
import secondlife.network.hcfactions.HCF;

public class Redis {

	private static Long lastError;

	public static <T> T runRedisCommand(RedisCommand<T> redisCommand) {
		Jedis jedis = HCF.redis.getResource();
		T result = null;

		try {
			result = redisCommand.run(jedis);
		} catch(Exception e) {
			e.printStackTrace();

			Redis.lastError = System.currentTimeMillis();

			if(jedis != null) {
				HCF.redis.returnBrokenResource(jedis);

				jedis = null;
			}
		} finally {
			if(jedis != null) HCF.redis.returnResource(jedis);
		}

		return result;
	}

	public static Long getLastError() {
		return Redis.lastError;
	}

	public interface RedisCommand<T> {
		T run(Jedis p0);
	}
}
