package com.example.hello.mapper;

import com.example.hello.entity.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 客户表 {@code customer} Mapper。
 */
public interface CustomerMapper {

    List<Customer> pageQuery(@Param("name") String name,
                             @Param("phone") String phone,
                             @Param("memberLevel") Integer memberLevel);

    Customer findById(@Param("id") Integer id);
    Customer findByUserAccountId(@Param("userAccountId") Integer userAccountId);

    int insert(Customer customer);
    int bindUserAccount(@Param("id") Integer id, @Param("userAccountId") Integer userAccountId);
    int unbindByUserAccountId(@Param("userAccountId") Integer userAccountId);

    int update(Customer customer);

    int deleteById(@Param("id") Integer id);
}
