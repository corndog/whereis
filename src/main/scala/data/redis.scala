// redis
package com.whereis.data.redis

import scala.io.Source
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.Protocol
import com.top10.redis.Redis
import java.io._
import com.top10.redis.SingleRedis
import com.top10.redis.ShardedRedis
import redis.clients.jedis.JedisShardInfo
import redis.clients.jedis.ShardedJedisPool
import scala.collection.JavaConversions._

object RedisData {

	val listName = "items"
	val redisClt = new SingleRedis("localhost", 6379)
	
	redisClt.lpush("cat", "9")

}