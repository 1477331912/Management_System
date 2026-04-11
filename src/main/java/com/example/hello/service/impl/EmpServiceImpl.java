package com.example.hello.service.impl;

import com.example.hello.dto.EmpCreateDTO;
import com.example.hello.dto.EmpExprDTO;
import com.example.hello.dto.EmpUpdateDTO;
import com.example.hello.entity.Emp;
import com.example.hello.entity.EmpExpr;
import com.example.hello.mapper.EmpExprMapper;
import com.example.hello.mapper.EmpMapper;
import com.example.hello.service.EmpService;
import com.example.hello.vo.PageResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工业务实现。
 */
@Service
public class EmpServiceImpl implements EmpService {
    private static final String DEFAULT_PASSWORD_MD5 = "e10adc3949ba59abbe56e057f20f883e";

    private final EmpMapper empMapper;
    private final EmpExprMapper empExprMapper;

    public EmpServiceImpl(EmpMapper empMapper, EmpExprMapper empExprMapper) {
        this.empMapper = empMapper;
        this.empExprMapper = empExprMapper;
    }

    @Override
    public PageResultVO<Emp> pageQuery(String name, Integer gender, java.time.LocalDate beginDate, java.time.LocalDate endDate, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<Emp> rows = (Page<Emp>) empMapper.pageQuery(name, gender, beginDate, endDate);
        PageResultVO<Emp> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    @Override
    public Emp findDetailById(Integer id) {
        Emp emp = empMapper.findById(id);
        if (emp != null) {
            emp.setExprList(empExprMapper.findByEmpId(emp.getId()));
        }
        return emp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(EmpCreateDTO req) {
        Emp emp = new Emp();
        emp.setUsername(req.getUsername());
        emp.setPassword(DEFAULT_PASSWORD_MD5);
        emp.setName(req.getName());
        emp.setGender(req.getGender());
        emp.setPhone(req.getPhone());
        emp.setSalary(req.getSalary());
        emp.setDeptId(req.getDeptId());
        emp.setPosition(req.getPosition());
        emp.setEntryDate(req.getEntryDate());
        emp.setImage(req.getImage());
        empMapper.insert(emp);

        List<EmpExpr> exprList = convertExprReq(req.getExprList());
        if (!CollectionUtils.isEmpty(exprList)) {
            empExprMapper.insertBatch(emp.getId(), exprList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(EmpUpdateDTO req) {
        Emp emp = new Emp();
        emp.setId(req.getId());
        emp.setUsername(req.getUsername());
        emp.setName(req.getName());
        emp.setGender(req.getGender());
        emp.setPhone(req.getPhone());
        emp.setSalary(req.getSalary());
        emp.setDeptId(req.getDeptId());
        emp.setPosition(req.getPosition());
        emp.setEntryDate(req.getEntryDate());
        emp.setImage(req.getImage());

        int affected = empMapper.update(emp);
        if (affected <= 0) {
            return false;
        }

        empExprMapper.deleteByEmpId(req.getId());
        List<EmpExpr> exprList = convertExprReq(req.getExprList());
        if (!CollectionUtils.isEmpty(exprList)) {
            empExprMapper.insertBatch(req.getId(), exprList);
        }
        return true;
    }

    @Override
    public List<Emp> listAll() {
        return empMapper.listAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        empExprMapper.deleteByEmpIds(ids);
        empMapper.deleteByIds(ids);
    }

    private List<EmpExpr> convertExprReq(List<EmpExprDTO> reqList) {
        if (CollectionUtils.isEmpty(reqList)) {
            return new ArrayList<>();
        }
        List<EmpExpr> exprList = new ArrayList<>(reqList.size());
        for (EmpExprDTO req : reqList) {
            EmpExpr expr = new EmpExpr();
            expr.setStartDate(req.getStartDate());
            expr.setEndDate(req.getEndDate());
            expr.setCompany(req.getCompany());
            expr.setPosition(req.getPosition());
            exprList.add(expr);
        }
        return exprList;
    }
}

