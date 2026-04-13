package com.example.hello.mapper;

import com.example.hello.entity.Pet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 宠物表 {@code pet} Mapper。
 */
public interface PetMapper {

    List<Pet> pageQuery(@Param("nickname") String nickname,
                        @Param("breed") String breed,
                        @Param("vaccineStatus") Integer vaccineStatus,
                        @Param("customerId") Integer customerId);

    Pet findById(@Param("id") Integer id);

    int insert(Pet pet);

    int update(Pet pet);

    int deleteById(@Param("id") Integer id);
}
