package com.example.notebook.rocketmq;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.config.ConfigTreeConfigDataResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author William
 * @Date 2022/6/29 18:55
 * @Version 1.0
 */
@Controller
public class ConsumerController {


    public void details() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerName");
        consumer.setNamesrvAddr("localhost:9876");
        //设置消费模式
        consumer.setMessageModel(MessageModel.CLUSTERING);
        // 指定消费偏移量（上次消费偏移量、最大偏移量、最小偏移量、启动时间戳）开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 消费者最大小线程数量
        consumer.setConsumeThreadMin(20);
        consumer.setConsumeThreadMax(30);
        // 推模式下任务间隔时间
        consumer.setPullInterval(0);
        // 推模式下任务拉取的条数
        consumer.setPullBatchSize(32);
        // 消息重试次数 -1代表16次
        consumer.setMaxReconsumeTimes(-1);
        // 消息消费超时时间
        consumer.setConsumeTimeout(15);

        //获取消费者对topic分配了哪些queue
        Set<MessageQueue> topicTest = consumer.fetchSubscribeMessageQueues("TopicTest");

        Iterator<MessageQueue> iterator = topicTest.iterator();
        while(iterator.hasNext()) {
            MessageQueue next = iterator.next();
            System.out.println(next.getQueueId());
        }

        // 订阅主题的所有消息
        consumer.subscribe("TopicTest", "*");
        // 根据sql订阅
        consumer.subscribe("TopicTest", MessageSelector.bySql("a between 0 and 3"));
        // 根据tag订阅
        consumer.subscribe("TopicTest", MessageSelector.byTag("tag1 || tag2"));

        // 注册并发监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try {
                    for(Message msg : list) {
                        System.out.println(msg.getTopic()+ msg.getBody().toString()+ msg.getTags());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //顺序事件消息，顺序消费的时候可以看到每个queue都有一个单独的线程来消费消息，即实现消息有序性
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                try {
                    for(Message msg : list) {
                        System.out.println(msg.getTopic()+ msg.getBody().toString()+ msg.getTags());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //TODO 这里注意意思是西安等一会，一会再处理这批消息，而不是直接放到重试队列中
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
    }

    /**
     * 集群消费模式:每个消费者消费一部分
     * @throws MQClientException
     */
    @PostMapping("/cluster")
    public void cluster() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_consumer");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.subscribe("TopicTest", "*");

        //设置消费模式，默认是集群模式
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List< MessageExt > list, ConsumeConcurrentlyContext
                    consumeConcurrentlyContext) {
                try {
                    for(int i = 0; i < list.size(); i++) {
                        MessageExt msg = list.get(i);
                        String topic = msg.getTopic();
                        String msgBody = new String(msg.getBody(), "utf-8");
                        String tags = msg.getTags();
                        System.out.println("收到消息:" + " topic:" + topic + ", tags:"+tags + ", msg:"+msgBody);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("consumer end");
    }

    /**
     * 广播消费：每个消费者都消费一遍所有消息
     * @throws MQClientException
     */
    public void boardcast() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group_consumer");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.subscribe("TopicTest", "*");

        //设置消费模式，默认是集群模式
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List< MessageExt > list, ConsumeConcurrentlyContext
                    consumeConcurrentlyContext) {
                try {
                    for(int i = 0; i < list.size(); i++) {
                        MessageExt msg = list.get(i);
                        String topic = msg.getTopic();
                        String msgBody = new String(msg.getBody(), "utf-8");
                        String tags = msg.getTags();
                        System.out.println("收到消息:" + " topic:" + topic + ", tags:"+tags + ", msg:"+msgBody);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("consumer end");
    }

    /**
     * 广播消费：每个消费者都消费一遍所有消息
     * @throws MQClientException
     */
    public void localorder() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("OrderConsumer1");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("PartOrder1", "*");

        //设置消费模式，默认是集群模式
        //consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.registerMessageListener(new MessageListenerOrderly() {
            Random random = new Random();
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);
                for(MessageExt msg : msgs) {
                    System.out.printf("consumeThread= %s, queueId=%s, content:%s, messageid = %s\n",
                            Thread.currentThread().getName(), msg.getQueueId(), new String(msg.getBody()), msg.getMsgId() );
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(300));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // 抛了异常，等待一会，然后再处理这批消息，而不是放到重试队列中
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("consumer start");
    }

    @Value("${rocketmq.name-server}")
    public static String server;

    public void schedule() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("scheduledConsumer");
        consumer.setNamesrvAddr(server);
        consumer.subscribe("ScheduledTopic", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for(MessageExt message : messages) {
                    System.out.printf("Receive message[msgId=%s] %s ms later\n", message.getMsgId(), String.valueOf(message.getStoreTimestamp() - message.getBornTimestamp()));

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

    }

    /**
     * 在subscribe中添加匹配tag进行过滤
     * @throws MQClientException
     */
    public void tagfilter() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("FilterConsumer");
        //System.out.println(server);
        consumer.setNamesrvAddr("localhost:9876");

        consumer.subscribe("FilterTopic", "tag1 || tag2");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                    try {
                        for(MessageExt msg : messages) {
                            String topic = msg.getTopic();
                            String tags = msg.getTags();
                            String body = new String(msg.getBody(), "utf-8");
                            System.out.printf("收到消息:%s, topic: %s, tags: %s, msg:%s \n", topic, tags, body);
                            System.out.println();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }

                return null;
            }
        });
        consumer.start();
        System.out.println("consumer start");
    }


    /**
     * 通过sql对tag进行过滤，对应的生产方法见producer中的tagfilter
     * @throws MQClientException
     */
    public void sqlfilter() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("FilterConsumer");
        //System.out.println(server);
        consumer.setNamesrvAddr("localhost:9876");
        // 通过sql表达式进行筛选,同时对TAGS和a属性进行筛选
        consumer.subscribe("FilterTopic", MessageSelector.bySql("TAGS is not null and TAGS in ('tag1', 'tag2')" +
                " and (a is not null and a between 0 and 3)"));
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for(MessageExt msg : messages) {
                        String topic = msg.getTopic();
                        String tags = msg.getTags();
                        String body = new String(msg.getBody(), "utf-8");
                        System.out.printf("收到消息:%s, topic: %s, tags: %s, msg:%s \n", topic, tags, body);
                        System.out.println();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return null;
            }
        });
        consumer.start();
        System.out.println("consumer start");
    }


    public void transaction() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("transaction");
        consumer.setNamesrvAddr("127.0.0.1:9876");
        consumer.subscribe("TransactionTopic", "*");
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                try{
                    //TODO 开启事务
                    for(int i = 0; i < messages.size(); i++) {
                        //TODO 执行本地事务

                        //TODO 执行本地事务成功
                        System.out.println(messages.get(i).getMsgId());
                        //
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }





















}
