package com.example.hello.service;

import com.example.hello.dto.UserCustomerProfileDTO;
import com.example.hello.dto.UserAccountProfileUpdateDTO;
import com.example.hello.entity.Customer;
import com.example.hello.vo.UserAccountProfileVO;

/**
 * 用户端个人中心业务。
 */
public interface UserProfileService {
    UserAccountProfileVO getMyAccountProfile(Integer userAccountId);
    UserAccountProfileVO updateMyAccountProfile(Integer userAccountId, UserAccountProfileUpdateDTO dto);
    Customer getMyCustomerProfile(Integer userAccountId);
    Customer createMyCustomerProfile(Integer userAccountId, UserCustomerProfileDTO dto);
    Customer updateMyCustomerProfile(Integer userAccountId, UserCustomerProfileDTO dto);
    boolean unbindMyCustomerProfile(Integer userAccountId);
}
