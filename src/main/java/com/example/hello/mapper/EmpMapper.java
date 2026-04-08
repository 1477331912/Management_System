package com.example.hello.mapper;

import com.example.hello.entity.Emp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 员工数据访问层。
 */
@Mapper
public interface EmpMapper {
    List<Emp> pageQuery(@Param("name") String name,
                        @Param("gender") Integer gender,
                        @Param("beginDate") LocalDate beginDate,
                        @Param("endDate") LocalDate endDate);

    Optional<Emp> findById(@Param("id") Integer id);

    int insert(Emp emp);

    int update(Emp emp);

    List<Emp> listAll();

    int deleteByIds(@Param("ids") List<Integer> ids);
}

