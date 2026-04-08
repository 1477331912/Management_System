package com.example.hello.service.impl;

import com.example.hello.entity.Dept;
import com.example.hello.mapper.DeptMapper;
import com.example.hello.service.DeptService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 部门业务实现。
 */
@Service
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper;

    /**
     * 构造注入。
     *
     * @param deptMapper mapper
     */
    public DeptServiceImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override
    public List<Dept> listAll() {
        return deptMapper.listAll();
    }

    @Override
    public Optional<Dept> findById(Integer id) {
        return deptMapper.findById(id);
    }

    @Override
    @Transactional
    public void create(String name) {
        Dept dept = new Dept();
        dept.setName(name);
        deptMapper.insert(dept);
    }

    @Override
    @Transactional
    public boolean update(Integer id, String name) {
        return deptMapper.updateNameById(id, name) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        return deptMapper.deleteById(id) > 0;
    }
}

