package com.example.hello.mapper;

import com.example.hello.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 用户账号表 Mapper。
 */
public interface UserAccountMapper {
    UserAccount findById(@Param("id") Integer id);
    UserAccount findByUsername(@Param("username") String username);
    UserAccount findByPhone(@Param("phone") String phone);
    UserAccount findByAccountAndPassword(@Param("account") String account, @Param("password") String password);
    int insert(UserAccount userAccount);
    int updateLastLoginTime(@Param("id") Integer id);
    int updatePhoneById(@Param("id") Integer id, @Param("phone") String phone);
    int updateUsernameAndNicknameById(@Param("id") Integer id,
                                      @Param("username") String username,
                                      @Param("nickname") String nickname);
}
