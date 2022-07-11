package com.example.notebook.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.notebook.entity.Setmeal;

import java.util.List;

/**
 * @Author William
 * @Date 2022/6/23 9:33
 * @Version 1.0
 */
public interface SetmealService extends IService<Setmeal> {

    public List<Setmeal> getAll();
}
