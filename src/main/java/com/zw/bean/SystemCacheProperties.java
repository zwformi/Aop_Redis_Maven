package com.zw.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/5/18.
 */
@Component
public class SystemCacheProperties {
    @Value("${system.cache.enable}")
    private String enable;
    @Value("${system.cache.database}")
    private Integer database;
    @Value("${system.cache.expire.default.enable}")
    private String expire_default_enable;
    @Value("${system.cache.expire.default.seconds}")
    private String expire_default_seconds;

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Integer getDatabase() {
        return database;
    }

    public void setDatabase(Integer database) {
        this.database = database;
    }

    public String getExpire_default_enable() {
        return expire_default_enable;
    }

    public void setExpire_default_enable(String expire_default_enable) {
        this.expire_default_enable = expire_default_enable;
    }

    public String getExpire_default_seconds() {
        return expire_default_seconds;
    }

    public void setExpire_default_seconds(String expire_default_seconds) {
        this.expire_default_seconds = expire_default_seconds;
    }


}
