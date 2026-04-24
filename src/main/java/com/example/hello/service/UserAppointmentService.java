package com.example.hello.service;

import com.example.hello.dto.UserAppointmentCancelDTO;
import com.example.hello.dto.UserAppointmentCreateDTO;
import com.example.hello.dto.UserAppointmentRescheduleDTO;
import com.example.hello.dto.UserPetCreateDTO;
import com.example.hello.entity.BizOrder;
import com.example.hello.entity.Pet;
import com.example.hello.vo.AvailableSlotVO;
import com.example.hello.vo.EmpOptionVO;
import com.example.hello.vo.OrderCreateResultVO;
import com.example.hello.vo.OrderDetailVO;
import com.example.hello.vo.PageResultVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户端预约业务。
 */
public interface UserAppointmentService {
    List<Pet> myPets(Integer userAccountId);
    Pet createMyPet(Integer userAccountId, UserPetCreateDTO dto);
    boolean deleteMyPet(Integer userAccountId, Integer petId);
    List<EmpOptionVO> empOptions(Integer serviceItemId, LocalDate date);
    List<AvailableSlotVO> availableSlots(LocalDate date, Integer serviceItemId, Integer empId);
    OrderCreateResultVO create(Integer userAccountId, UserAppointmentCreateDTO dto);
    PageResultVO<BizOrder> myOrders(Integer userAccountId, String orderNo, Integer status, LocalDate beginTime, LocalDate endTime, Integer page, Integer pageSize);
    OrderDetailVO myOrderDetail(Integer userAccountId, Integer orderId);
    BizOrder cancel(Integer userAccountId, Integer orderId, UserAppointmentCancelDTO dto);
    BizOrder reschedule(Integer userAccountId, Integer orderId, UserAppointmentRescheduleDTO dto);
}
