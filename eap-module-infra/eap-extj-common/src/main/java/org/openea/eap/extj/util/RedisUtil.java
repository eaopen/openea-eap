package org.openea.eap.extj.util;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class RedisUtil {
    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);
    public static final int CAHCETIME = 60;
    public static final int CAHCEHOUR = 3600;
    public static final int CAHCEDAY = 86400;
    public static final int CAHCEWEEK = 604800;
    public static final int CAHCEMONTH = 18144000;
    public static final int CAHCEYEAR = 217728000;
    private static long expiresIn;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheKeyUtil cacheKeyUtil;

    public RedisUtil() {
    }

    public Set<String> getAllVisiualKeys() {
        Set<String> allKey = new HashSet(16);
        allKey.addAll((Collection) Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getAllUser() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getCompanySelect() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getDictionary() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getDynamic() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getOrganizeList() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getPositionList() + "*")));
        allKey.addAll((Collection)Objects.requireNonNull(this.redisTemplate.keys("*" + this.cacheKeyUtil.getVisiualData() + "*")));
        return allKey;
    }

    public Set<String> getAllKeys() {
        Set<String> keys = this.redisTemplate.keys("*");
        if (CollectionUtils.isNotEmpty(keys)) {
            keys = (Set)keys.stream().filter((s) -> {
                return !s.startsWith(CacheKeyUtil.IDGENERATOR);
            }).collect(Collectors.toSet());
        }

        return keys;
    }

    public Long getLiveTime(String key) {
        return this.redisTemplate.getExpire(key);
    }

    public void remove(String key) {
        if (this.exists(key)) {
            this.redisTemplate.delete(key);
        }

    }

    public void removeAll() {
        Set<String> keys = this.getAllKeys();
        if (CollectionUtils.isNotEmpty(keys)) {
            keys.forEach((k) -> {
                this.redisTemplate.delete(k);
            });
        }

    }

    public boolean exists(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public void expire(String key, long time) {
        if (time > 0L) {
            this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } else {
            this.redisTemplate.expire(key, expiresIn, TimeUnit.SECONDS);
        }

    }

    public void insert(String key, Object object) {
        this.insert(key, object, 0L);
    }

    public void insert(String key, Object object, long time) {
        if (object instanceof Map) {
            this.redisTemplate.opsForHash().putAll(key, (Map)object);
        } else if (object instanceof List) {
            this.redisTemplate.opsForList().rightPushAll(key, (List)object);
        } else if (object instanceof Set) {
            this.redisTemplate.opsForSet().add(key, ((Set)object).toArray());
        } else {
            this.redisTemplate.opsForValue().set(key, this.toJson(object));
        }

        this.expire(key, time);
    }

    private String toJson(Object object) {
        return !(object instanceof Integer) && !(object instanceof Long) && !(object instanceof Float) && !(object instanceof Double) && !(object instanceof Boolean) && !(object instanceof String) ? JsonUtil.getObjectToString(object) : String.valueOf(object);
    }

    public void rename(String oldKey, String newKey) {
        this.redisTemplate.rename(oldKey, newKey);
    }

    public String getType(String key) {
        return this.redisTemplate.type(key).code();
    }

    public Object getString(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String hashId, String key) {
        return this.redisTemplate.opsForHash().hasKey(hashId, key);
    }

    public List<String> getHashKeys(String hashId) {
        List<String> list = new ArrayList();
        Map<Object, Object> map = this.getMap(hashId);
        Iterator var4 = map.keySet().iterator();

        while(var4.hasNext()) {
            Object object = var4.next();
            if (object instanceof String) {
                list.add(String.valueOf(object));
            }
        }

        return list;
    }

    public List<String> getHashValues(String hashId) {
        List<String> list = new ArrayList();
        Map<Object, Object> map = this.getMap(hashId);
        Iterator var4 = map.keySet().iterator();

        while(var4.hasNext()) {
            Object object = var4.next();
            if (map.get(object) instanceof String) {
                list.add(String.valueOf(map.get(object)));
            }
        }

        return list;
    }

    public String getHashValues(String hashId, String key) {
        Object object = this.redisTemplate.opsForHash().get(hashId, key);
        return object != null ? String.valueOf(object) : null;
    }

    public void removeHash(String hashId, String key) {
        if (this.hasKey(hashId, key)) {
            this.redisTemplate.opsForHash().delete(hashId, new Object[]{key});
        }

    }

    public <K, V> Map<K, V> getMap(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public void insertHash(String hashId, String key, String value) {
        this.redisTemplate.opsForHash().put(hashId, key, value);
    }

    public Set<Object> getSet(String key) {
        try {
            return this.redisTemplate.opsForSet().members(key);
        } catch (Exception var3) {
            log.error(key, var3);
            return null;
        }
    }

    public long getSetSize(String key) {
        try {
            return this.redisTemplate.opsForSet().size(key);
        } catch (Exception var3) {
            log.error(key, var3);
            return 0L;
        }
    }

    public List<Object> get(String key, long start, long end) {
        try {
            return this.redisTemplate.opsForList().range(key, start, end);
        } catch (Exception var7) {
            log.error(key, var7);
            return null;
        }
    }

    public long getListSize(String key) {
        try {
            return this.redisTemplate.opsForList().size(key);
        } catch (Exception var3) {
            log.error(key, var3);
            return 0L;
        }
    }

    public Object getIndex(String key, long index) {
        try {
            return this.redisTemplate.opsForList().index(key, index);
        } catch (Exception var5) {
            log.error(key, var5);
            return null;
        }
    }

    static {
        expiresIn = TimeUnit.SECONDS.toSeconds(28800L);
    }
}
