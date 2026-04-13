package com.example.hello.service.impl;

import com.example.hello.dto.PetCreateDTO;
import com.example.hello.dto.PetUpdateDTO;
import com.example.hello.entity.Customer;
import com.example.hello.entity.Pet;
import com.example.hello.mapper.CustomerMapper;
import com.example.hello.mapper.PetMapper;
import com.example.hello.service.PetService;
import com.example.hello.vo.PageResultVO;
import com.example.hello.vo.PetCustomerInfo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 宠物管理业务实现。
 * <p>
 * 删除接口文档中“未完成订单”约束需订单模块支持，当前仅按主键删除。
 */
@Service
public class PetServiceImpl implements PetService {

    private final PetMapper petMapper;
    private final CustomerMapper customerMapper;

    public PetServiceImpl(PetMapper petMapper, CustomerMapper customerMapper) {
        this.petMapper = petMapper;
        this.customerMapper = customerMapper;
    }

    @Override
    public PageResultVO<Pet> pageQuery(String nickname, String breed, Integer vaccineStatus, Integer customerId,
                                       Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<Pet> rows = (Page<Pet>) petMapper.pageQuery(nickname, breed, vaccineStatus, customerId);
        PageResultVO<Pet> result = new PageResultVO<>();
        result.setTotal(rows.getTotal());
        result.setRows(rows.getResult());
        return result;
    }

    @Override
    public Pet findDetailById(Integer id) {
        Pet pet = petMapper.findById(id);
        if (pet == null) {
            return null;
        }
        Customer c = customerMapper.findById(pet.getCustomerId());
        if (c != null) {
            PetCustomerInfo info = new PetCustomerInfo();
            info.setId(c.getId());
            info.setName(c.getName());
            info.setPhone(c.getPhone());
            info.setMemberLevel(c.getMemberLevel());
            pet.setCustomer(info);
        }
        return pet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pet create(PetCreateDTO req) {
        requireCustomerExists(req.getCustomerId());
        Pet p = new Pet();
        p.setNickname(req.getNickname());
        p.setBreed(req.getBreed());
        p.setGender(req.getGender());
        p.setBirthday(req.getBirthday());
        p.setWeight(req.getWeight());
        p.setVaccineStatus(req.getVaccineStatus() != null ? req.getVaccineStatus() : 0);
        p.setAllergyHistory(req.getAllergyHistory());
        p.setImage(req.getImage());
        p.setCustomerId(req.getCustomerId());
        petMapper.insert(p);
        return petMapper.findById(p.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Pet update(PetUpdateDTO req) {
        Pet existing = petMapper.findById(req.getId());
        if (existing == null) {
            return null;
        }
        requireCustomerExists(req.getCustomerId());
        Pet p = new Pet();
        p.setId(req.getId());
        p.setNickname(req.getNickname());
        p.setBreed(req.getBreed());
        p.setGender(req.getGender());
        p.setCustomerId(req.getCustomerId());
        p.setBirthday(req.getBirthday() != null ? req.getBirthday() : existing.getBirthday());
        p.setWeight(req.getWeight() != null ? req.getWeight() : existing.getWeight());
        p.setVaccineStatus(req.getVaccineStatus() != null ? req.getVaccineStatus() : existing.getVaccineStatus());
        p.setAllergyHistory(req.getAllergyHistory() != null ? req.getAllergyHistory() : existing.getAllergyHistory());
        p.setImage(req.getImage() != null ? req.getImage() : existing.getImage());
        petMapper.update(p);
        return petMapper.findById(req.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Integer id) {
        if (petMapper.findById(id) == null) {
            return false;
        }
        return petMapper.deleteById(id) > 0;
    }

    private void requireCustomerExists(Integer customerId) {
        if (customerMapper.findById(customerId) == null) {
            throw new IllegalArgumentException("客户不存在");
        }
    }
}
