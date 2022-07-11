package com.example.notebook.design;

import com.zaxxer.hikari.util.ConcurrentBag;

import java.util.concurrent.ConcurrentHashMap;

public class Singleton {
    private static ConcurrentHashMap<Person, Integer> concurrentHashMap = new ConcurrentHashMap<>();
    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin");

        for(int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 1000; i++) {
                        concurrentHashMap.put(LazyPerson.getInstance(), 1);
//                        concurrentHashMap.put(HungryPerson.getInstance(), 1);
                    }
                }
            }).start();
        }
        Thread.sleep(5000);
        System.out.println(concurrentHashMap.size());
        System.out.println("end");
    }


}

class Person {

}

// 懒汉模式
class LazyPerson extends Person {
    private static volatile  LazyPerson person;
    public static LazyPerson getInstance() {
        if(person == null) {
            synchronized (LazyPerson.class) {
                if(person == null) {
                    person = new LazyPerson();
                }
            }
        }
        return person;
    }
}

// 饿汉单例模式
class HungryPerson extends Person {
    private static final  HungryPerson person = new HungryPerson();
    private HungryPerson(){

    }
    public static HungryPerson getInstance() {
        return person;
    }
}