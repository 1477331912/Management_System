package com.example.hello.mapper;

import com.example.hello.entity.EmpExpr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工工作经历数据访问层。
 */
@Mapper
public interface EmpExprMapper {
    List<EmpExpr> findByEmpId(@Param("empId") Integer empId);

    int insertBatch(@Param("empId") Integer empId, @Param("exprList") List<EmpExpr> exprList);

    int deleteByEmpId(@Param("empId") Integer empId);

    int deleteByEmpIds(@Param("empIds") List<Integer> empIds);
}

