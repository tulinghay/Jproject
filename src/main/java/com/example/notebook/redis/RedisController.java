package com.example.notebook.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @Author William
 * @Date 2022/6/27 9:13
 * @Version 1.0
 */
@Controller
public class RedisController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void list(){
        // 弹出，从队列两端
        redisTemplate.opsForList().leftPop("keys", 1);
        redisTemplate.opsForList().leftPush("keys", "value1");
        //取一个范围的值
        List list = redisTemplate.opsForList().range("keys", 1, 3);
        // 取某个index的值
        redisTemplate.opsForList().index("keys", 1);
        // 取某个值的所在位置
        redisTemplate.opsForList().indexOf("keys", "value1");
        //取大小
        redisTemplate.opsForList().size("keys");
    }

    public void set(){
        // 加入元素
        redisTemplate.opsForSet().add("key", "value1");
        // 取出key中所有对象
        Set keys = redisTemplate.opsForSet().members("keys");
        // 是否存在
        redisTemplate.opsForSet().isMember("key","value");
        // 删除
        redisTemplate.opsForSet().remove("keys", "value");
        // 合并两个set
        redisTemplate.opsForSet().union("keys", "keys2");
        // 对比set的不同元素
        Set difference = redisTemplate.opsForSet().difference("keys", "keys2");
        // 取并集
        Set union = redisTemplate.opsForSet().union("keys1", "keys2");
        // 取交集
        Set intersect = redisTemplate.opsForSet().intersect("keys1", "keys2");
        // 取不同然后另存为keys3
        redisTemplate.opsForSet().differenceAndStore("keys1", "keys2", "keys3");
        // 从keys1中随机取一个值
        Object keys1 = redisTemplate.opsForSet().randomMember("keys1");
        // 随机取3个
        List<Object> keys2 = redisTemplate.opsForSet().randomMembers("keys", 3);
        // 将keys1中的value1 移动keys2中
        redisTemplate.opsForSet().move("keys1", "value1", "keys2");
    }

    public void hash(){
        // 查看keys1中是否有name这个field
        redisTemplate.opsForHash().hasKey("keys1", "name");
        // 获取keys1中name属性
        Object o = redisTemplate.opsForHash().get("keys1", "name");
        // 返回删除的数量
        Long delete = redisTemplate.opsForHash().delete("keys1", "keys2");
        // 获取所有的键值对
        redisTemplate.opsForHash().entries("keys1");
        //获取所有的key
        redisTemplate.opsForHash().keys("keys1");
        // 获取所有的值
        redisTemplate.opsForHash().values("keys1");
        // 不存在则放入
        redisTemplate.opsForHash().putIfAbsent("keys", "field", "value");
        // 放入所有
        redisTemplate.opsForHash().putAll("keys1", new HashMap<>());
        // 得到list中所有
        redisTemplate.opsForHash().multiGet("keys1", new ArrayList<>());
        // field属性自增，自增步长为1
        redisTemplate.opsForHash().increment("keys1", "field", 1);
        // 往keys1中添加field属性
        redisTemplate.opsForHash().put("keys1", "field", "qwe");
        // 获取keys2对应值的长度
        redisTemplate.opsForHash().lengthOfValue("keys1", "keys2");
    }
}
