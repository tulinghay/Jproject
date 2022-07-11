package com.example.notebook.rocketmq.entity;

import com.example.notebook.rocketmq.entity.Order;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author William
 * @Date 2022/6/29 19:56
 * @Version 1.0
 */
public class ConsumerOrder {

    public static List<Order> build() {
        List<Order> list = new ArrayList<>();

        for(int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setId(0L + i);
            order.setName(i + "-create");
            list.add(order);
        }

        for(int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setId(0L + i);
            order.setName(i + "-push");
            list.add(order);
        }
        for(int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setId(0L + i);
            order.setName(i + "-add");
            list.add(order);
        }
        return list;
    }
}
