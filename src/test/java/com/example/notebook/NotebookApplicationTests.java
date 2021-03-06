package com.example.notebook;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.notebook.entity.Dish;
import com.example.notebook.entity.Setmeal;
import com.example.notebook.service.DishService;
import com.example.notebook.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.common.RemotingUtil;
import org.apache.rocketmq.remoting.exception.RemotingException;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class NotebookApplicationTests {

    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    @Test
    void contextLoads() {


    }
    /**
     * ?????????
     * @param current
     * @param size
     */
    public void page(int current, int size) {
        Page<Dish> page = new Page<>(current, size);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getPrice);
        // queryWrapper.eq(Dish::getName, "asdf");
        Page<Dish> page1 = dishService.page(page, queryWrapper);
        System.out.println(page.getSize());
    }

    public void select() {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getId, 1).or(i->i.eq(Dish::getId, 1));

        // ???????????????key??????????????????value????????????
        Map<String, Object> map = dishService.getMap(queryWrapper);

        // ?????????
        List<Dish> list = dishService.list(new LambdaQueryWrapper<>());
    }



    // ????????????????????????????????????
    // ?????????????????????entity?????????????????????
    public void delete() {
        //ThreadLocal
        // ???????????????
        Dish dish = dishService.getOne(new LambdaQueryWrapper<Dish>().eq(Dish::getName, "???????????????"));
        // ??????????????????
        dishService.removeById(dish.getId());
        // ???????????????????????????
        Dish byId = dishService.getById(dish.getId());
        // ????????????
        dishService.updateById(dish);
    }


    public void deleteset() {
        Setmeal setmeal = setmealService.getOne(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getName, "????????????A??????"));
        boolean b = setmealService.removeById(setmeal.getId());
        List<Setmeal> all = setmealService.getAll();

    }


    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    public void redis(){
        // ?????????
        String keys3 = stringRedisTemplate.opsForValue().get("keys");
        // ????????????
        stringRedisTemplate.opsForValue().set("keys", "value1");

        Boolean keys2 = stringRedisTemplate.expire("keys", 12, TimeUnit.SECONDS);
        Long keys1 = stringRedisTemplate.getExpire("keys");
        //????????????
        Boolean keys = stringRedisTemplate.hasKey("keys");
    }

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    public void rocket() {
        rocketMQTemplate.convertAndSend("key", "firstmessage");
        System.out.println("success");
    }

    @Test
    public void send(){
        rocketMQTemplate.convertAndSend("yes","??????,Java??????" + 1);
        System.out.println("success");
    }


    @Test
    public void sendMessage() throws MQClientException, MQBrokerException, RemotingException, InterruptedException, UnsupportedEncodingException {

    }



}
