package com.zw.aopaspect;

import com.zw.annotation.Cacheable;
import com.zw.bean.SystemCacheProperties;
import com.zw.util.*;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/5/18.
 */
public class CacheAopAspect {
    private final static Logger log =  Logger.getLogger(CacheAopAspect.class);

/*    Properties SystemCacheProperties = new Properties();
    SystemCacheProperties.load(new FileInputStream("systemCache.properties"));*/
    @Autowired
    private SystemCacheProperties systemCacheProperties;

    RedisAccess redisAccess;

    public RedisAccess getRedisAccess() {
        return redisAccess;
    }

    public void setRedisAccess(RedisAccess redisAccess) {
        this.redisAccess = redisAccess;
    }

    public Object doCacheable(ProceedingJoinPoint pjp) throws Throwable {
        log.info("=====已经进入切面方法doCacheable======");
        Object result=null;
        Method method = AopUtils.getMethod(pjp);

        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        Boolean isCacheEnable = "enable".equals(systemCacheProperties.getEnable());

        if(cacheable != null && !isCacheEnable) {
            log.debug("没有开启缓存");
        }

        //-----------------------------------------------------------------------
        // 如果拦截的方法中没有Cacheable注解
        // 或者system.cache.enable的开关没打开
        // 则直接执行方法并返回结果
        //-----------------------------------------------------------------------
        if (cacheable == null || !isCacheEnable) {
            try {
                result = pjp.proceed();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return result;
        }

        String key = cacheable.key();

        //----------------------------------------------------------
        // 用SpEL解释key值
        //----------------------------------------------------------
        String keyVal = SpringExpressionUtils.parseKey(key, method, pjp.getArgs());

        if (!StringUtils.isEmpty(cacheable.category())){
            keyVal = cacheable.category() + "_" + keyVal;
        } else {
            //----------------------------------------------------------
            // 如果cacheable的注解中category为空取 类名+方法名
            //----------------------------------------------------------
            keyVal = pjp.getTarget().getClass().getSimpleName() + "_"
                    + method.getName() + "_" + keyVal;
        }

        Class returnType = ((MethodSignature)pjp.getSignature()).getReturnType();


        //-----------------------------------------------------------------------
        // 从redis读取keyVal，并且转换成returnType的类型
        //-----------------------------------------------------------------------
        result = redisAccess.get(keyVal, returnType);

        if (result == null) {
            try {
                //-----------------------------------------------------------------------
                // 如果redis没有数据则执行拦截的方法体
                //-----------------------------------------------------------------------
                result = pjp.proceed();
                int expireSeconds = 0;

                //-----------------------------------------------------------------------
                // 如果Cacheable注解中的expire为默认(默认值为-1)
                // 并且systemCache.properties中的system.cache.expire.default.enable开关为true
                // 则取system.cache.expire.default.seconds的值为缓存的数据
                //-----------------------------------------------------------------------
                if (cacheable.expire() == -1 &&
                        "enable".equals(systemCacheProperties.getExpire_default_enable())) {
                    expireSeconds = new Integer(systemCacheProperties.getExpire_default_seconds());
                } else {
                    expireSeconds = getExpireSeconds(cacheable);
                }
                //-----------------------------------------------------------------------
                // 把拦截的方法体得到的数据设置进redis，过期时间为计算出来的expireSeconds
                //-----------------------------------------------------------------------
                redisAccess.set(keyVal, result, expireSeconds);
                log.debug("已缓存缓存:key=" +  keyVal);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return result;
        }
        log.debug("========从缓存中读取");
        log.debug("=======:key   = " + key);
        log.debug("=======:keyVal= " + keyVal);
        log.debug("=======:val   = " + result);
        return result;
    }

    /**
     * 计算根据Cacheable注解的expire和DateUnit计算要缓存的秒数
     * @param cacheable
     * @return
     */
    public int getExpireSeconds(Cacheable cacheable) {
        int expire = cacheable.expire();
        DateUnit unit = cacheable.dateUnit();
        if (expire <= 0) {
            return 0;
        }
        if (unit == DateUnit.MINUTES) {
            return expire * 60;
        } else if(unit == DateUnit.HOURS) {
            return expire * 60 * 60;
        } else if(unit == DateUnit.DAYS) {
            return expire * 60 * 60 * 24;
        } else if(unit == DateUnit.MONTHS) {
            return expire * 60 * 60 * 24 * 30;
        } else if(unit == DateUnit.YEARS) {
            return expire * 60 * 60 * 24 * 365;
        }
        return expire;
    }
}
