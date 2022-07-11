package com.example.notebook.reflect;

import com.example.notebook.entity.Employee;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Employee employee = new Employee();
        Class<?> classReflect = Class.forName("com.example.notebook.entity.Employee");
        Object o = classReflect.newInstance();
        System.out.println(o);

        Field[] fields = classReflect.getFields();


        Method[] methods = classReflect.getMethods();
        for(int i = 0; i < methods.length; i++) {
            System.out.println(methods[i].getName());

        }


    }
}
