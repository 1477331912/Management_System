package com.example.hello.service.impl;

import com.example.hello.dto.CustomerCreateDTO;
import com.example.hello.dto.CustomerUpdateDTO;
import com.example.hello.entity.Customer;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.service.CustomerService;
import com.example.hello.vo.PageResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户管理业务实现（仅操作 {@code customer} 表）。
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public PageResultVO<Customer> pageQuery(String name, String phone, Integer memberLevel, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<Customer> rows = (Page<Customer>) customerMapper.pageQuery(name, phone, memberLevel);
        PageResultVO<Customer> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    @Override
    public Customer findById(Integer id) {
        return customerMapper.findById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer create(CustomerCreateDTO req) {
        Customer c = new Customer();
        c.setName(req.getName());
        c.setPhone(req.getPhone());
        c.setAddress(req.getAddress());
        c.setMemberLevel(req.getMemberLevel() != null ? req.getMemberLevel() : 1);
        customerMapper.insert(c);
        return customerMapper.findById(c.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer update(CustomerUpdateDTO req) {
        Customer existing = customerMapper.findById(req.getId());
        if (existing == null) {
            return null;
        }
        Customer c = new Customer();
        c.setId(req.getId());
        c.setName(req.getName());
        c.setPhone(req.getPhone());
        if (req.getAddress() != null) {
            c.setAddress(req.getAddress());
        } else {
            c.setAddress(existing.getAddress());
        }
        if (req.getMemberLevel() != null) {
            c.setMemberLevel(req.getMemberLevel());
        } else {
            c.setMemberLevel(existing.getMemberLevel());
        }
        customerMapper.update(c);
        return customerMapper.findById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Integer id) {
        if (customerMapper.findById(id) == null) {
            return false;
        }
        return customerMapper.deleteById(id) > 0;
    }
}
