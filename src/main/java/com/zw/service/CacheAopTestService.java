package com.zw.service;

import com.zw.annotation.Cacheable;
import com.zw.bean.Player;
import com.zw.util.DateUnit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/18.
 */
@Service("CacheAopTestService")
public class CacheAopTestService {
    /**
     * 参数为基本类型
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Cacheable(key = "#{pageNo}_#{pageSize}")
    public List getPrimitiveData(Integer pageNo, Integer pageSize) {
        Player player = new Player();
        player.setUserName("zhouwen");
        List list = new ArrayList();
        list.add(player);
        return list;
    }

    /**
     * 方法参数为Javabean,缓存200秒
     * @param player
     * @return
     */
    @Cacheable(category="player",key="#{player.userName}",expire = 200)
    public Player getBeanData(Player player) {
        return player;
    }

    /**
     * 方法参数为Map；expire = 1,dateUnit = DateUnit.HOURS  缓存一小时
     * @param phone
     * @return
     */
    @Cacheable(category = "forMapTest",key = "#{phone[cpu]}_#{phone[ram]}",expire = 1,dateUnit = DateUnit.HOURS)
    public Map getMapData(Map phone) {
        return phone;
    }

    /**
     * 方法参数为复合类型，包括Javabean,Map,Integer等，缓存永存时间
     * @param player
     * @param phone
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Cacheable(category = "mix",key = "#{player.userName}_#{phone[cpu]}_#{phone[ram]}_#{pageNo}_#{pageSize}")
    public List<Map> getMixData(Player player, Map phone, Integer pageNo, Integer pageSize) {
        Map map = new HashMap();
        map.put("type","mix");
        List<Map> list = new ArrayList<Map>();
        list.add(map);
        list.add(phone);
        return list;
    }

/*    @CacheEvict(category = "forTest",key = "#{map[userName]}")
    public Map updateMapData(Map map) {
        System.out.println("this is evict map test...");
        return map;
    }*/
}
