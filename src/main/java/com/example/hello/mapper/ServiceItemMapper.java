package com.example.hello.mapper;

import com.example.hello.entity.ServiceItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 服务项目表 {@code service_item} Mapper。
 */
public interface ServiceItemMapper {

    List<ServiceItem> pageQuery(@Param("name") String name, @Param("deptId") Integer deptId);
    List<ServiceItem> listAll();

    ServiceItem findById(@Param("id") Integer id);

    int insert(ServiceItem item);

    int update(ServiceItem item);

    int deleteById(@Param("id") Integer id);
}
