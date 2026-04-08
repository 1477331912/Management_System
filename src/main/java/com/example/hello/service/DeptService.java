package com.example.hello.service;

import com.example.hello.entity.Dept;

import java.util.List;
import java.util.Optional;

/**
 * 部门业务接口。
 */
public interface DeptService {
    /**
     * 查询全部部门列表（不分页，按最后操作时间倒序）。
     *
     * @return 部门列表
     */
    List<Dept> listAll();

    /**
     * 根据ID查询部门（用于编辑回显）。
     *
     * @param id 部门ID
     * @return 存在则返回部门，否则为空
     */
    Optional<Dept> findById(Integer id);

    /**
     * 新增部门。
     *
     * @param name 部门名称
     */
    void create(String name);

    /**
     * 修改部门名称。
     *
     * @param id   部门ID
     * @param name 部门名称
     * @return true=更新成功；false=部门不存在
     */
    boolean update(Integer id, String name);

    /**
     * 删除部门。
     *
     * @param id 部门ID
     * @return true=删除成功；false=部门不存在
     */
    boolean delete(Integer id);
}

