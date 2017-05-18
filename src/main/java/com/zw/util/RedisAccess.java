package com.zw.util;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by Administrator on 2017/5/18.
 */
public class RedisAccess {

    private JedisPool jedisPool;

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void set(String key,Object o,Integer seconds){
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, JSONObject.toJSONString(o));
        if (seconds != null && seconds > 0) {
            jedis.expire(key,seconds);
        }
        jedisPool.returnResource(jedis);
    }

    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String text = jedis.get(key);
        jedisPool.returnResource(jedis);
        return text;
    }


    public <T> T get(String key,Class<T> clazz){
        String text = get(key);
        T result = JSONObject.parseObject(text, clazz);
        return result;
    }

    public void del(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedisPool.returnResource(jedis);
    }

    /**
     * 清空某个DB的数据
     */
    public void flushDB() {
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
        jedisPool.returnResource(jedis);
    }
}
