package com.example.hello.mapper;

import com.example.hello.entity.Emp;
import com.example.hello.vo.EmpOptionVO;
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

    /**
     * 按部门查询在职服务师列表，用于预约可用时段计算。
     *
     * @param deptId 部门ID
     * @return 该部门下的员工列表
     */
    List<Emp> listByDeptId(@Param("deptId") Integer deptId);

    /**
     * 按部门查询服务师下拉数据。
     *
     * @param deptId 部门ID
     * @return 下拉列表
     */
    List<EmpOptionVO> listOptionsByDeptId(@Param("deptId") Integer deptId);

    /**
     * 按部门和日期查询有可用排班的服务师下拉数据。
     *
     * @param deptId   部门ID
     * @param workDate 排班日期
     * @return 下拉列表
     */
    List<EmpOptionVO> listOptionsByDeptIdAndDate(@Param("deptId") Integer deptId,
                                                  @Param("workDate") LocalDate workDate);

    int deleteByIds(@Param("ids") List<Integer> ids);
}

