package com.zljin.flashbuy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    private RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ============================== Common ==============================

    /**
     * 获取匹配的key列表
     * @param pattern 匹配模式
     * @return 匹配的key集合，空集合表示无匹配或异常
     */
    public Set<String> keys(String pattern) {
        try {
            Set<String> result = redisTemplate.keys(pattern);
            return result != null ? result : Collections.emptySet();
        } catch (Exception e) {
            log.error("Redis keys操作失败, pattern: {}", pattern, e);
            return Collections.emptySet();
        }
    }

    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                return Boolean.TRUE.equals(redisTemplate.expire(key, time, TimeUnit.SECONDS));
            }
            return false;
        } catch (Exception e) {
            log.error("Redis设置过期时间失败, key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 根据key获取过期时间
     * @param key 键
     * @return 时间(秒) 返回-2表示key不存在，-1表示永久有效
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Redis获取过期时间失败, key: {}", key, e);
            return -2;
        }
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis判断key存在失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     * @param keys 可以传一个或多个key
     * @return 成功删除的key数量
     */
    public long delete(String... keys) {
        if (keys == null || keys.length == 0) {
            return 0;
        }

        try {
            if (keys.length == 1) {
                Boolean result = redisTemplate.delete(keys[0]);
                return Boolean.TRUE.equals(result) ? 1 : 0;
            } else {
                List<String> keyList = Arrays.asList(keys);
                Long count = redisTemplate.delete(keyList);
                return count != null ? count : 0;
            }
        } catch (Exception e) {
            log.error("Redis删除key失败, keys: {}", Arrays.toString(keys), e);
            return 0;
        }
    }

    // ============================== String Operations ==============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        if (key == null) {
            return null;
        }
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis获取值失败, key: {}", key, e);
            return null;
        }
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis设置值失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0
     * @return true成功 false失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis设置带过期时间的值失败, key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 如果不存在则设置（原子操作）
     * @param key 键
     * @param value 值
     * @return true设置成功 false已存在
     */
    public boolean setIfAbsent(String key, Object value) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("Redis setIfAbsent操作失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * 如果不存在则设置并设置过期时间（原子操作）
     * @param key 键
     * @param value 值
     * @param time 过期时间(秒)
     * @return true设置成功 false已存在
     */
    public boolean setIfAbsent(String key, Object value, long time) {
        try {
            if (time > 0) {
                Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
                return Boolean.TRUE.equals(result);
            } else {
                return setIfAbsent(key, value);
            }
        } catch (Exception e) {
            log.error("Redis setIfAbsent带过期时间操作失败, key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 递增因子（大于0）
     * @return 递增后的值，-1表示失败
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于0");
        }
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : -1;
        } catch (Exception e) {
            log.error("Redis递增操作失败, key: {}, delta: {}", key, delta, e);
            return -1;
        }
    }

    /**
     * 递减
     * @param key 键
     * @param delta 递减因子（大于0）
     * @return 递减后的值，-1表示失败
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于0");
        }
        try {
            Long result = redisTemplate.opsForValue().increment(key, -delta);
            return result != null ? result : -1;
        } catch (Exception e) {
            log.error("Redis递减操作失败, key: {}, delta: {}", key, delta, e);
            return -1;
        }
    }

    // ============================== Hash Operations ==============================

    /**
     * HashGet
     * @param key 键
     * @param field 字段
     * @return 值
     */
    public Object hashGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Redis HashGet操作失败, key: {}, field: {}", key, field, e);
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hashGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis获取所有Hash字段失败, key: {}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * HashSet多个字段
     * @param key 键
     * @param map 对应多个键值
     * @return true成功 false失败
     */
    public boolean hashPutAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("Redis HashSet多个字段失败, key: {}", key, e);
            return false;
        }
    }

    /**
     * HashSet多个字段并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hashPutAll(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis HashSet多个字段带过期时间失败, key: {}, time: {}", key, time, e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据
     * @param key 键
     * @param field 字段
     * @param value 值
     * @return true成功 false失败
     */
    public boolean hashPut(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("Redis HashPut操作失败, key: {}, field: {}", key, field, e);
            return false;
        }
    }

    // 其他Hash操作类似改进...

    // ============================== 泛型方法改进 ==============================

    /**
     * 获取缓存（带类型转换）
     * @param key 键
     * @param clazz 目标类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            log.error("Redis值类型转换失败, key: {}, 期望类型: {}, 实际类型: {}",
                    key, clazz.getSimpleName(), value.getClass().getSimpleName(), e);
            return null;
        }
    }

    // ============================== 批量操作改进 ==============================

    /**
     * 批量获取
     * @param keys 键集合
     * @return 值列表
     */
    public List<Object> multiGet(Collection<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }
        try {
            List<Object> result = redisTemplate.opsForValue().multiGet(keys);
            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            log.error("Redis批量获取失败, keys: {}", keys, e);
            return Collections.emptyList();
        }
    }

    /**
     * 批量设置
     * @param map 键值对
     * @return 是否成功
     */
    public boolean multiSet(Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return false;
        }
        try {
            redisTemplate.opsForValue().multiSet(map);
            return true;
        } catch (Exception e) {
            log.error("Redis批量设置失败, mapSize: {}", map.size(), e);
            return false;
        }
    }
}