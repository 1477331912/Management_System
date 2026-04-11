package com.example.hello.service;

import com.example.hello.dto.EmpCreateDTO;
import com.example.hello.dto.EmpUpdateDTO;
import com.example.hello.entity.Emp;
import com.example.hello.vo.PageResultVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工业务接口。
 */
public interface EmpService {
    PageResultVO<Emp> pageQuery(String name, Integer gender, LocalDate beginDate, LocalDate endDate, Integer page, Integer pageSize);

    Emp findDetailById(Integer id);

    void create(EmpCreateDTO req);

    boolean update(EmpUpdateDTO req);

    List<Emp> listAll();

    void batchDelete(List<Integer> ids);
}

