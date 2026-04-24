package com.example.hello.service;

import com.example.hello.dto.ServiceItemCreateDTO;
import com.example.hello.dto.ServiceItemUpdateDTO;
import com.example.hello.entity.ServiceItem;
import com.example.hello.vo.PageResultVO;

/**
 * 服务项目管理业务。
 */
public interface ServiceItemService {

    PageResultVO<ServiceItem> pageQuery(String name, Integer deptId, Integer page, Integer pageSize);
    java.util.List<ServiceItem> listAll();

    ServiceItem findById(Integer id);

    ServiceItem create(ServiceItemCreateDTO req);

    ServiceItem update(ServiceItemUpdateDTO req);

    boolean delete(Integer id);
}
