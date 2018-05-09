/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.Jedis;

import com.google.inject.Inject;
import java.util.Set;
import redis.clients.jedis.Jedis;

/**
 *
 * @author mou1609
 */

/*
city based not state based
 */
public class JedisService {
    
    @Inject
    Jedis jedis;
    
    public JedisService() {
    }

    public String getValue(String key) {
        return jedis.get(key);
    }

    public void set(String key, String value) {
        jedis.set(key, value);
    }

    public Set<String> getAllKeys() {
        for (String key : jedis.keys("*")) {
            System.out.println(key);
        }

        return jedis.keys("*");
    }

    public Set<String> getAllKeys(String prefix) {
        return jedis.keys(prefix + "*");
    }
}
