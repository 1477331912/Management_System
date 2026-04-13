package com.example.hello.service;

import com.example.hello.dto.PetCreateDTO;
import com.example.hello.dto.PetUpdateDTO;
import com.example.hello.entity.Pet;
import com.example.hello.vo.PageResultVO;

/**
 * 宠物管理业务。
 */
public interface PetService {

    PageResultVO<Pet> pageQuery(String nickname, String breed, Integer vaccineStatus, Integer customerId,
                                Integer page, Integer pageSize);

    Pet findDetailById(Integer id);

    Pet create(PetCreateDTO req);

    Pet update(PetUpdateDTO req);

    boolean delete(Integer id);
}
