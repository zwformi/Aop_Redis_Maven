package com.zw.test;

import com.zw.bean.Player;
import com.zw.service.CacheAopTestService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:applicationContext.xml"})
public class CacheAopTest {
    private final static Logger log =  Logger.getLogger(CacheAopTest.class);
    @Resource
    private CacheAopTestService cacheAopTestService;
    @Resource
    private JedisPool jedisPool;

    @Test
    public void testConnection() {
        Jedis jedis =jedisPool.getResource();
        jedis.set("mobile", "1388888888");
        System.out.println("已连接上redis");
        System.out.println("mobile值为："+jedis.get("mobile"));
        log.info("===log测试===");
    }


    /**
     * 测试method的参数为基本类型
     */
    @Test
    public void testPrimitive() {
        System.out.println("====普通类型参数测试====");
        cacheAopTestService.getPrimitiveData(1,10);
    }
    //第一次输出
    //已缓存缓存:key=CacheAopTestService_getPrimitiveData_1_10

    //第二次输出
    //<========从缓存中读取>
    //<=======:key   = #{pageNo}_#{pageSize}>
    //<=======:keyVal= CacheAopTestService_getPrimitiveData_1_10>
    //<=======:val   = [{"age":0,"userName":"massive"}]>



    /**
     * 测试method的参数为Javabean
     */
    @Test
    public void testJavaBean() {
        System.out.println("====bean类型参数测试====");
        Player player = new Player();
        player.setUserName("zwbean");
        player.setAge(27);
        cacheAopTestService.getBeanData(player);
        System.out.println(player);
    }
    //第一次输出
    //<已缓存缓存:key=player_Stephen Curry

    //第二次输出
    //<========从缓存中读取>
    //<=======:key   = #{player.userName}>
    //<=======:keyVal= player_Stephen Curry>
    //<=======:val   = Player{userName='Stephen Curry', age=27}>



    /**
     * 测试method的参数为Map
     */
    @Test
    public void testMap() {
        System.out.println("====Map类型参数测试====");
        Map phone = new HashMap();
        phone.put("cpu","Intel");
        phone.put("ram","4GB");
        cacheAopTestService.getMapData(phone);
    }
    //第一次输出
    //已缓存缓存:key=forMapTest_Intel_4GB

    //第二次输出
    //<========从缓存中读取>
    //<=======:key   = #{phone[cpu]}_#{phone[ram]}>
    //<=======:keyVal= forMapTest_Intel_4GB>
    //<=======:val   = {ram=4GB, cpu=Intel}>



    /**
     * 测试method的参数和返回都是混合且复杂
     */
    @Test
    public void testMix() {
        System.out.println("====混合类型参数测试====");
        Map phone = new HashMap();
        phone.put("cpu","Intel");
        phone.put("ram","4GB");

        Player player = new Player();
        player.setUserName("zwMix");
        player.setAge(27);

        cacheAopTestService.getMixData(player,phone,1,100);
    }
    //第一次输出
    //已缓存缓存:key=mix_Curry_Intel_4GB_1_100

    //第二次输出
    //<========从缓存中读取>
    //<=======:key   = #{player.userName}_#{phone[cpu]}_#{phone[ram]}_#{pageNo}_#{pageSize}>
    //<=======:keyVal= mix_Curry_Intel_4GB_1_100>
    //<=======:val   = [{"type":"mix"}, {"ram":"4GB","cpu":"Intel"}]>

}
