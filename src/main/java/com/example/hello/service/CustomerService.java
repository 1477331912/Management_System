package com.example.hello.service;

import com.example.hello.dto.CustomerCreateDTO;
import com.example.hello.dto.CustomerUpdateDTO;
import com.example.hello.entity.Customer;
import com.example.hello.vo.PageResultVO;

/**
 * 客户管理业务。
 */
public interface CustomerService {

    PageResultVO<Customer> pageQuery(String name, String phone, Integer memberLevel, Integer page, Integer pageSize);

    Customer findById(Integer id);

    Customer create(CustomerCreateDTO req);

    Customer update(CustomerUpdateDTO req);

    boolean delete(Integer id);
}
