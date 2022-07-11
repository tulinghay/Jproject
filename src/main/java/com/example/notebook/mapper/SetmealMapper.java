package com.example.notebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.notebook.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * @Author William
 * @Date 2022/6/23 9:32
 * @Version 1.0
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    @Select("select * from setmeal")
    public List<Setmeal> getAll();
}
