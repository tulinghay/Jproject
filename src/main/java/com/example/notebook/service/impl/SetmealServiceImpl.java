package com.example.notebook.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.notebook.entity.Setmeal;
import com.example.notebook.mapper.SetmealMapper;
import com.example.notebook.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author William
 * @Date 2022/6/23 9:33
 * @Version 1.0
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealMapper setmealMapper;

    @Override
    public List<Setmeal> getAll() {
        return setmealMapper.getAll();
    }
}
