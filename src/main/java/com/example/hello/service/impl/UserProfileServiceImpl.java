package com.example.hello.service.impl;

import com.example.hello.dto.UserAccountProfileUpdateDTO;
import com.example.hello.dto.UserCustomerProfileDTO;
import com.example.hello.entity.Customer;
import com.example.hello.entity.UserAccount;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.mapper.UserAccountMapper;
import com.example.hello.service.UserProfileService;
import com.example.hello.vo.UserAccountProfileVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户端个人中心业务实现。
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

    private final CustomerMapper customerMapper;
    private final UserAccountMapper userAccountMapper;

    public UserProfileServiceImpl(CustomerMapper customerMapper, UserAccountMapper userAccountMapper) {
        this.customerMapper = customerMapper;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public UserAccountProfileVO getMyAccountProfile(Integer userAccountId) {
        UserAccount ua = userAccountMapper.findById(userAccountId);
        if (ua == null) {
            return null;
        }
        return toAccountProfileVO(ua);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAccountProfileVO updateMyAccountProfile(Integer userAccountId, UserAccountProfileUpdateDTO dto) {
        UserAccount ua = userAccountMapper.findById(userAccountId);
        if (ua == null) {
            throw new IllegalArgumentException("当前用户不存在");
        }
        String newUsername = dto.getUsername().trim();
        if (!newUsername.equals(ua.getUsername())) {
            UserAccount other = userAccountMapper.findByUsername(newUsername);
            if (other != null && !other.getId().equals(userAccountId)) {
                throw new IllegalArgumentException("用户名已被注册，请更换用户名");
            }
        }
        userAccountMapper.updateUsernameAndNicknameById(userAccountId, newUsername, dto.getNickname());
        return toAccountProfileVO(userAccountMapper.findById(userAccountId));
    }

    @Override
    public Customer getMyCustomerProfile(Integer userAccountId) {
        return customerMapper.findByUserAccountId(userAccountId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer createMyCustomerProfile(Integer userAccountId, UserCustomerProfileDTO dto) {
        Customer existing = customerMapper.findByUserAccountId(userAccountId);
        if (existing != null) {
            throw new IllegalArgumentException("已绑定客户档案，请使用修改接口");
        }
        UserAccount ua = userAccountMapper.findById(userAccountId);
        if (ua == null) {
            throw new IllegalArgumentException("当前用户不存在");
        }
        Customer c = new Customer();
        c.setName(dto.getName());
        c.setPhone(ua.getPhone());
        c.setAddress(dto.getAddress());
        c.setMemberLevel(dto.getMemberLevel() != null ? dto.getMemberLevel() : 1);
        customerMapper.insert(c);
        customerMapper.bindUserAccount(c.getId(), userAccountId);
        return customerMapper.findById(c.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Customer updateMyCustomerProfile(Integer userAccountId, UserCustomerProfileDTO dto) {
        Customer existing = customerMapper.findByUserAccountId(userAccountId);
        if (existing == null) {
            throw new IllegalArgumentException("请先绑定客户档案");
        }
        UserAccount ua = userAccountMapper.findById(userAccountId);
        if (ua == null) {
            throw new IllegalArgumentException("当前用户不存在");
        }
        String targetPhone = ua.getPhone();
        if (StringUtils.hasText(dto.getPhone())) {
            String newPhone = dto.getPhone().trim();
            if (!newPhone.equals(ua.getPhone())) {
                UserAccount other = userAccountMapper.findByPhone(newPhone);
                if (other != null && !other.getId().equals(userAccountId)) {
                    throw new IllegalArgumentException("手机号已被注册，请更换手机号");
                }
                userAccountMapper.updatePhoneById(userAccountId, newPhone);
                targetPhone = newPhone;
            }
        }
        Customer c = new Customer();
        c.setId(existing.getId());
        c.setName(dto.getName());
        // customer.phone 与 user_account.phone 保持一致。
        c.setPhone(targetPhone);
        c.setAddress(dto.getAddress());
        c.setMemberLevel(dto.getMemberLevel() != null ? dto.getMemberLevel() : existing.getMemberLevel());
        customerMapper.update(c);
        return customerMapper.findById(existing.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindMyCustomerProfile(Integer userAccountId) {
        return customerMapper.unbindByUserAccountId(userAccountId) > 0;
    }

    private UserAccountProfileVO toAccountProfileVO(UserAccount ua) {
        UserAccountProfileVO vo = new UserAccountProfileVO();
        vo.setId(ua.getId());
        vo.setUsername(ua.getUsername());
        vo.setPhone(ua.getPhone());
        vo.setNickname(ua.getNickname());
        vo.setStatus(ua.getStatus());
        vo.setUpdateTime(ua.getUpdateTime());
        return vo;
    }
}
