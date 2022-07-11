package com.example.notebook.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dankun.autoconfig.DankunService;
//import com.example.huang.DemoConfiguration;
//import com.example.huang.DemoService;
import com.example.notebook.entity.Dish;
import com.example.notebook.entity.Employee;
import com.example.notebook.service.DishService;
import com.example.notebook.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RestController
public class HDTController {

    // 取application.yml中配置的hdt.name参数
    @Value("${hdt.name}")
    private String name;
    // 取application.properties中配置的hdt.name参数
    @Value("${hdt.password}")
    private String password;

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DishService dishService;

    @PostMapping("/hello")
    public String getHello(String content){
        System.out.println(1/0);
        return content + "-get";
    }

    @GetMapping("/hello")
    public String postHello(String content){
        System.out.println(1/0);
        return content + "-post";
    }

    @GetMapping("/test")
    public String test() {
        return name + password;
    }

    /**
     * mybatis-plus分页，需要在config中配置拦截器
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public String page(int page, int pageSize, String name) {
        Page pageInfo = new Page(page,pageSize);
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(!StringUtils.isEmpty(name), Employee::getName, name);
        lambdaQueryWrapper.orderByAsc(Employee::getId);
        employeeService.page(pageInfo, lambdaQueryWrapper);

        return JSON.toJSON(pageInfo).toString();
    }
    // gt 大于  ge 大于等于  lt 小于  le 小于等于
    @GetMapping("/select")
    public String select() {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        // id = 1 and name = huangda
        // wrapper.eq(Employee::getId, 1).eq(Employee::getName, "huagda");
        // id == 1 or (name = "huangda" and id >= 1)
        wrapper.ne(Employee::getId, 1).or(i -> i.eq(Employee::getName, "haungda").ge(Employee::getId, 1));
        wrapper.between(Employee::getId,1,2);

        Employee one = employeeService.getOne(wrapper);

        if(one == null) {
            return "null";
        }
        return JSON.toJSON(one).toString();
    }

    /**
     * 方式一：传入一个对象用来更新
     * 方拾二：使用set方法更新
     */
    @GetMapping("/update")
    public void update() {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, "鱼香炒鸡蛋");
        Dish one = dishService.getOne(queryWrapper);
        one.setPrice(BigDecimal.valueOf(2000.00));
        dishService.update(one, queryWrapper);

        UpdateWrapper<Dish> wrapper = new UpdateWrapper<>();
        wrapper.eq("name", "鱼香炒鸡蛋").set("price", BigDecimal.valueOf(2000.00));
        boolean update = dishService.update(wrapper);
        List<Dish> idList = new ArrayList<>();
        boolean b = dishService.updateBatchById(idList, idList.size());
    }

    @PostMapping("/allatt")
    public String allAtt( @RequestBody MultiValueMap<String, Object> map1) {
        //map.forEach((k,v)-> System.out.println(k + v));
        System.out.println("=====================");
        map1.forEach((k,v)-> System.out.println(k + v));
        return "success";
    }


    @PostMapping("/common/upload")
    public String upload(MultipartFile file) throws IOException {
        file.transferTo(new File("D:\\test.png"));
        return "upload success";
    }

    @Autowired
    private DankunService dankunService;

    @PostMapping("/auto")
    public String auto(MultipartFile file) throws IOException {

        return dankunService.wrap("me");
    }
    //@Autowired
    //DemoService demoService;
    //
    //public String config(){
    //    return demoService.wrap();
    //}
}
