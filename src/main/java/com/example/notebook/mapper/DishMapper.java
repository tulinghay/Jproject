package com.example.notebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.notebook.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author William
 * @Date 2022/6/23 9:19
 * @Version 1.0
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
