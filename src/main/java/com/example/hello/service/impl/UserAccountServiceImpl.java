package com.example.hello.service.impl;

import com.example.hello.dto.UserLoginDTO;
import com.example.hello.dto.UserRegisterDTO;
import com.example.hello.entity.Customer;
import com.example.hello.entity.UserAccount;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.mapper.UserAccountMapper;
import com.example.hello.service.UserAccountService;
import com.example.hello.util.JwtUtils;
import com.example.hello.vo.UserLoginVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端账号业务实现。
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountMapper userAccountMapper;
    private final CustomerMapper customerMapper;
    private final JwtUtils jwtUtils;

    public UserAccountServiceImpl(UserAccountMapper userAccountMapper, CustomerMapper customerMapper, JwtUtils jwtUtils) {
        this.userAccountMapper = userAccountMapper;
        this.customerMapper = customerMapper;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO register(UserRegisterDTO dto) {
        String username = dto.getUsername() != null ? dto.getUsername().trim() : null;
        String phone = dto.getPhone() != null ? dto.getPhone().trim() : null;
        String nickname = dto.getNickname() != null ? dto.getNickname().trim() : null;
        String customerName = dto.getCustomerName() != null ? dto.getCustomerName().trim() : null;

        if (userAccountMapper.findByUsername(username) != null) {
            throw new IllegalArgumentException("用户名已被注册，请更换用户名");
        }
        if (userAccountMapper.findByPhone(phone) != null) {
            throw new IllegalArgumentException("手机号已被注册，请直接登录或更换手机号");
        }

        UserAccount ua = new UserAccount();
        ua.setUsername(username);
        ua.setPhone(phone);
        ua.setPassword(md5(dto.getPassword()));
        ua.setNickname(nickname);
        ua.setStatus(1);
        userAccountMapper.insert(ua);

        // customerName 为可选：不填时仅注册账号，后续可再完善客户档案。
        Integer customerId = null;
        if (StringUtils.hasText(customerName)) {
            Customer c = new Customer();
            c.setName(customerName);
            c.setPhone(phone);
            c.setMemberLevel(1);
            customerMapper.insert(c);
            customerMapper.bindUserAccount(c.getId(), ua.getId());
            customerId = c.getId();
        }
        return buildLoginVO(ua, customerId);
    }

    @Override
    public UserLoginVO login(UserLoginDTO dto) {
        UserAccount ua = userAccountMapper.findByAccountAndPassword(dto.getAccount(), md5(dto.getPassword()));
        if (ua == null) {
            return null;
        }
        userAccountMapper.updateLastLoginTime(ua.getId());
        Customer customer = customerMapper.findByUserAccountId(ua.getId());
        return buildLoginVO(ua, customer != null ? customer.getId() : null);
    }

    private UserLoginVO buildLoginVO(UserAccount ua, Integer customerId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", String.valueOf(ua.getId()));
        claims.put("role", "user");
        claims.put("username", ua.getUsername());

        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(ua.getId());
        vo.setCustomerId(customerId);
        vo.setUsername(ua.getUsername());
        vo.setPhone(ua.getPhone());
        vo.setToken(jwtUtils.generateToken(claims));
        return vo;
    }

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }
}
