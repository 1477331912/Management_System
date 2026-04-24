package com.example.hello.service.impl;

import com.example.hello.dto.ServiceItemCreateDTO;
import com.example.hello.dto.ServiceItemUpdateDTO;
import com.example.hello.entity.ServiceItem;
import com.example.hello.mapper.DeptMapper;
import com.example.hello.mapper.ServiceItemMapper;
import com.example.hello.service.ServiceItemService;
import com.example.hello.vo.PageResultVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务项目管理业务实现。
 * <p>
 * 删除接口文档中“已被订单引用”约束需订单模块支持，当前仅按主键删除。
 */
@Service
public class ServiceItemServiceImpl implements ServiceItemService {

    private final ServiceItemMapper serviceItemMapper;
    private final DeptMapper deptMapper;

    public ServiceItemServiceImpl(ServiceItemMapper serviceItemMapper, DeptMapper deptMapper) {
        this.serviceItemMapper = serviceItemMapper;
        this.deptMapper = deptMapper;
    }

    @Override
    public PageResultVO<ServiceItem> pageQuery(String name, Integer deptId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<ServiceItem> rows = (Page<ServiceItem>) serviceItemMapper.pageQuery(name, deptId);
        PageResultVO<ServiceItem> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    @Override
    public java.util.List<ServiceItem> listAll() {
        return serviceItemMapper.listAll();
    }

    @Override
    public ServiceItem findById(Integer id) {
        return serviceItemMapper.findById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceItem create(ServiceItemCreateDTO req) {
        requireDeptExists(req.getDeptId());
        ServiceItem item = new ServiceItem();
        item.setName(req.getName());
        item.setDeptId(req.getDeptId());
        item.setPrice(req.getPrice());
        item.setDurationMinutes(req.getDurationMinutes());
        item.setQualificationRequired(req.getQualificationRequired());
        serviceItemMapper.insert(item);
        return serviceItemMapper.findById(item.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceItem update(ServiceItemUpdateDTO req) {
        ServiceItem existing = serviceItemMapper.findById(req.getId());
        if (existing == null) {
            return null;
        }
        requireDeptExists(req.getDeptId());
        ServiceItem item = new ServiceItem();
        item.setId(req.getId());
        item.setName(req.getName());
        item.setDeptId(req.getDeptId());
        item.setPrice(req.getPrice());
        item.setDurationMinutes(req.getDurationMinutes());
        item.setQualificationRequired(req.getQualificationRequired() != null
                ? req.getQualificationRequired()
                : existing.getQualificationRequired());
        serviceItemMapper.update(item);
        return serviceItemMapper.findById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Integer id) {
        if (serviceItemMapper.findById(id) == null) {
            return false;
        }
        return serviceItemMapper.deleteById(id) > 0;
    }

    private void requireDeptExists(Integer deptId) {
        if (deptMapper.findById(deptId).isEmpty()) {
            throw new IllegalArgumentException("部门不存在");
        }
    }
}
