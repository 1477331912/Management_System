package com.example.hello.mapper;

import com.example.hello.entity.Dept;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Optional;

/**
 * 部门数据访问层（MyBatis Mapper）。
 * <p>
 * 说明：本项目开启了驼峰映射 {@code map-underscore-to-camel-case=true}，并在SQL中显式别名确保字段映射稳定。
 */
@Mapper
public interface DeptMapper {

    /**
     * 查询全部部门列表，按最后操作时间倒序。
     *
     * @return 部门列表
     */
    @Select("""
            select id, name, create_time as createTime, update_time as updateTime
            from dept
            order by update_time desc
            """)
    List<Dept> listAll();

    /**
     * 根据ID查询部门。
     *
     * @param id 部门ID
     * @return 存在则返回部门，否则为空
     */
    @Select("""
            select id, name, create_time as createTime, update_time as updateTime
            from dept
            where id = #{id}
            """)
    Optional<Dept> findById(@Param("id") Integer id);

    /**
     * 新增部门。
     *
     * @param dept 部门（仅name必填）
     * @return 影响行数
     */
    @Insert("""
            insert into dept(name)
            values(#{name})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Dept dept);

    /**
     * 根据ID更新部门名称。
     *
     * @param id   部门ID
     * @param name 部门名称
     * @return 影响行数
     */
    @Update("""
            update dept
            set name = #{name}
            where id = #{id}
            """)
    int updateNameById(@Param("id") Integer id, @Param("name") String name);

    /**
     * 根据ID删除部门。
     *
     * @param id 部门ID
     * @return 影响行数
     */
    @Delete("""
            delete from dept
            where id = #{id}
            """)
    int deleteById(@Param("id") Integer id);

    /**
     * 批量删除部门。
     *
     * @param ids 部门ID列表（非空）
     * @return 影响行数
     */
    @Delete("""
            <script>
            delete from dept where id in
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
            </script>
            """)
    int deleteByIds(@Param("ids") List<Integer> ids);
}

