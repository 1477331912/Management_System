package com.example.hello.mapper;

import com.example.hello.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工数据访问层。
 */
@Mapper
public interface EmpMapper {
    List<Emp> pageQuery(@Param("name") String name,
                        @Param("gender") Integer gender,
                        @Param("beginDate") LocalDate beginDate,
                        @Param("endDate") LocalDate endDate);

    Emp findById(@Param("id") Integer id);

    /**
     * 根据用户名和加密密码查询员工（登录校验）。
     *
     * @param username 用户名
     * @param password MD5密码
     * @return 匹配员工
     */
    Emp findByUsernameAndPassword(@Param("username") String username,
                                  @Param("password") String password);

    int insert(Emp emp);

    int update(Emp emp);

    List<Emp> listAll();

    int deleteByIds(@Param("ids") List<Integer> ids);
}

