package com.example.notebook.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.notebook.entity.Dish;
import com.example.notebook.mapper.DishMapper;
import com.example.notebook.service.DishService;
import org.springframework.stereotype.Service;

/**
 * @Author William
 * @Date 2022/6/23 9:21
 * @Version 1.0
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
