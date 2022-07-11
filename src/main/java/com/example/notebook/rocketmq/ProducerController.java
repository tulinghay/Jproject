package com.example.notebook.rocketmq;

import com.example.notebook.rocketmq.entity.ConsumerOrder;
import com.example.notebook.rocketmq.entity.Order;
import jdk.nashorn.internal.ir.Block;
import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author William
 * @Date 2022/6/28 18:59
 * @Version 1.0
 */
@Slf4j
@RestController
public class ProducerController {
    /*
    1.在服务器安装rocketmq，并启动namesrv服务和broker服务
    2.搭建rocketmq-dashboard平台,
    3.配置yml文件
     */
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @PostMapping("/rocketmq1")
    public void rocket() {
        rocketMQTemplate.convertAndSend("key", "firstmessage");
        System.out.println("success");
    }

    /**
     * 消息发送的一些细节
     */
    public void details() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // producerGroup，对普通消息用处不大，针对的是事务消息和高可用
        DefaultMQProducer producer = new DefaultMQProducer("producerName");
        // 设置默认情况下topic在每一个broker的队列数量
        producer.setDefaultTopicQueueNums(8);
        // 发送消息默认超时时间
        producer.setSendMsgTimeout(3000);
        // 消息体超过4k则启用压缩
        producer.setCompressMsgBodyOverHowmuch(1024*4);
        // 同步发送消息重试次数，默认为2，总共执行三次
        producer.setRetryTimesWhenSendFailed(2);
        // 异步发送消息重试次数，默认为2，总共执行三次
        producer.setRetryTimesWhenSendAsyncFailed(2);
        // 消息重试时选择另一个broker时（消息没有存储成功是否发送到另一个broker），默认为false
        producer.setRetryAnotherBrokerWhenNotStoreOK(false);
        // 允许发送的最大消息长度
        producer.setMaxMessageSize(1024*1024*4);
        // 设置nameserver地址
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        // 查找TopicTest主题的所有消息队列
        List<MessageQueue> topicTest = producer.fetchPublishMessageQueues("TopicTest");

        // 消息发送
        for(int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTest", "tag", "hello".getBytes(StandardCharsets.UTF_8));
            //TODO 单向发送
            // 消息单向发送，发送后没有任何返回结果，不能保证消息是否真的发送成功
            producer.sendOneway(msg);
            // 消息单向发送，发送后没有任何返回结果，不能保证消息是否真的发送成功
            // 添加了选择器，可以选择往那个messagequeue发送，保证消息顺序
            producer.sendOneway(msg, new MessageQueueSelector(){

                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    return list.get(0);
                }
            }, null);
            // 单向发送，指定队列单向发送
            producer.sendOneway(msg, topicTest.get(0));

            //TODO 同步发送
            SendResult send = producer.send(msg);
            // 同步发送设置超时时间
            SendResult send1 = producer.send(msg, 1000 * 3);
            // 通过select指定队列同步发送
            producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    return list.get(0);
                }
            }, null);
            // 指定队列发送
            producer.send(msg, topicTest.get(0));

            //TODO 异步发送
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("success");
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("failed");
                }
            });

            // 添加超时时间
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("success");
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("failed");
                }
            }, 3000);

            // 指定队列
            producer.send(msg,
                    new MessageQueueSelector(){

                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    return list.get(0);
                }
            }, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("success");
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("failed");
                }
            });
            producer.shutdown();
        }

    }


    /**
     * 消息同步发送
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @PostMapping("/sync")
    public void sync() throws MQClientException, MQBrokerException, RemotingException, InterruptedException, UnsupportedEncodingException {
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setSendLatencyFaultEnable(true);
        Lock lock = new ReentrantLock();
        


        producer.start();
        for(int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTest", "TagA",
                    ("hello rocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        producer.shutdown();
    }

    /**
     * 消息异步发送
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @PostMapping("/async")
    public void async() throws MQClientException, MQBrokerException, RemotingException, InterruptedException, UnsupportedEncodingException {
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setSendLatencyFaultEnable(true);

        producer.start();
        for(int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTest", "TagA",
                    ("hello rocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.send(msg,new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("success");
                }

                @Override
                public void onException(Throwable throwable) {
                    System.out.println("failed");
                }
            });

            //System.out.printf("%s%n", sendResult);
        }
        Thread.sleep(1000);
        producer.shutdown();
    }

    /**
     * 消息单向发送，发送后没有任何返回结果，不能保证消息是否真的发送成功
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    @PostMapping("/oneway")
    public void oneway() throws MQClientException, MQBrokerException, RemotingException, InterruptedException, UnsupportedEncodingException {
        DefaultMQProducer producer = new DefaultMQProducer("group_test");
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 当某个broker节点发送失败和发送耗时较长,则在一段时间内不再选择该broker
        producer.setSendLatencyFaultEnable(true);

        producer.start();
        for(int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTest", "TagA",
                    ("hello rocketMQ "+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            producer.sendOneway(msg);

            //System.out.printf("%s%n", sendResult);
        }
        Thread.sleep(1000);
        producer.shutdown();
    }


    /**
     * 部分消息的顺序生产，主要是将对应订单下的内容放到同一个queue中
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void localorder() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        List<Order> orders = ConsumerOrder.build();
        for(int i = 0; i < orders.size(); i++) {
            String body = orders.get(i).toString();
            Message msg = new Message("PartOrder1", null, "KEY"+i, body.getBytes());
            // select方法中的object为send第二个传入的参数，
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                // 选择对应的messagequeue，保证同一个订单会传到同一个messagequeue中
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                    Long id = (Long) o;
                    long index = id % list.size();
                    return list.get((int) index);

                }
            }, orders.get(i).getId());

            System.out.println(String.format("SendResult status:%s, queueId:%d, body:%s",
                    sendResult.getSendStatus(),
                    sendResult.getMessageQueue().getQueueId(),
                    body));
        }
        producer.shutdown();
    }

    /**
     * 延迟发送消息到broker
     * @throws MQClientException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void delay() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("scheduledProducer");
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        int totalMessagesToSend = 10;
        for(int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("ScheduledTopic", ("hello scheduled message" + i).getBytes());
            // delaylevel 1s  5s  10s  30s 1m 2m  3m  4m  5m  6m  7m  8m  9m  10m  20m  30m  1h  2h
            // 4为30s
            message.setDelayTimeLevel(4);
            producer.send(message);
        }
        producer.shutdown();
    }

    @Value("${rocketmq.name-server}")
    public String server;

    /**
     * 批量消息
     * @throws MQClientException
     */
    public void batch() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("BatchProducer");
        producer.setNamesrvAddr(server);
        producer.start();
        String topic = "BatchTest";

        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            messages.add(new Message(topic, "Tag", "OrderID00" + i, ("Hello world "+i).getBytes()));

        try {
            producer.send(messages);
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            producer.shutdown();
        }
    }

    public void tagfilter() throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    //public static void main(String[] args)  throws MQClientException, MQBrokerException, RemotingException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("Filter");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        String[] tags = {"tag1", "tag2", "tag3"};

        for(int i = 0; i < 3; i++) {
            Message msg = new Message("FilterTopic", tags[i % tags.length], "hello world".getBytes());
            // 添加一个属性a，并赋予值
            msg.putUserProperty("a", String.valueOf(i));
            SendResult sendResult = producer.send(msg);

            System.out.println(sendResult);
        }

        producer.shutdown();
    }

    public void transaction() throws MQClientException, InterruptedException {
        TransactionListener transactionListener = new TransactionListenerImpl();

        TransactionMQProducer producer = new TransactionMQProducer("transactionProducer");
        producer.setNamesrvAddr("127.0.0.1:9876");
        ExecutorService executorService = new ThreadPoolExecutor(2,
                2, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction");
                return thread;
            }
        });

        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.start();
        // RemotingHelper.DEFAULT_CHARSET
        try{
            Message msg = new Message("TransactionTopic", "tag", ("A 向 B系统转钱".getBytes()));
            SendResult sendResult = producer.sendMessageInTransaction(msg, null);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println(sendResult.getSendStatus() +"-"+format.format(new Date()));
        }catch (Exception e){
            //TODO 事务回滚rollback
            e.printStackTrace();
        }

        //TODO 半事务发送成功
        for(int i = 0; i < 1000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();

    }

    class TransactionListenerImpl implements TransactionListener{

        // 执行本地事务
        @Override
        public LocalTransactionState executeLocalTransaction(Message message, Object o) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //TODO 执行本地食物
            //TODO 情况1：本地事务执行成功
            //TODO 情况2：本地事务执行失败
            //TODO 情况3：业务复杂，还未执行完成，不知道执行结果unknow
            return LocalTransactionState.UNKNOW;
        }
        //事务回查， 默认60s查一次
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //TODO 业务回查成功
            return LocalTransactionState.COMMIT_MESSAGE;
            //TODO 业务回滚

            //TODO 业务回查还是unknow
        }
    }


    public class ListSplitter implements Iterator<List<Message>> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public List<Message> next() {
            return null;
        }
    }
    //将一个大的消息分成多个小的消息
    public List<Message> next(List<Message> messages){
        int sizeLimit = 1000 * 1000;
        int curIndex = 0;
        int nextIndex =1 ;
        return null;
    }

}



























