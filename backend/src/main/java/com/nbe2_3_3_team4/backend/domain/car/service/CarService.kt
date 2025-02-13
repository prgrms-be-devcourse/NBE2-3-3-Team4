package com.nbe2_3_3_team4.backend.domain.car.service

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse.GetCar
import com.nbe2_3_3_team4.backend.domain.car.entity.Car
import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.global.exception.BadRequestException
import com.nbe2_3_3_team4.backend.global.exception.DuplicateException
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CarService(private val carRepository : CarRepository ,private val memberRepository: MemberRepository) {
    @Transactional
    fun registerCar(dto: CarRequest.RegCar, email: String?): CarResponse.RegCar {
        val member = memberRepository.findByEmail(email).orElse(null) ?: throw  NotFoundException(ErrorCode.USER_NOT_FOUND)
        if(carRepository.existsByNumber(dto.carNumber)) {throw DuplicateException(ErrorCode.CAR_ALREADY_EXISTS)}

        // 회원의 현재 등록된 차량 수 확인
        if (member.cars.size >= 3) {
            throw BadRequestException(ErrorCode.CAR_LIMIT_OVER)
        }
        return CarResponse.RegCar.from(member.addCar(Car.to(dto)))
    }

    @Transactional
    fun updatePrimary(carId: Long) {
        val car = carRepository.findById(carId).orElse(null) ?: throw NotFoundException(ErrorCode.CAR_NOT_FOUND)
        car.updateIsPrimary(car.isPrimary)
    }

    @Transactional(readOnly = true)
    fun getCars(email: String?): List<GetCar> {
        val member = memberRepository.findByEmail(email).orElse(null) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        val cars: MutableList<GetCar> = ArrayList()
        for (car in member.cars) {
            cars.add(GetCar.from(car))
        }
        return cars
    }

    @Transactional
    fun modify(carId: Long, dto: CarRequest.Modify){
        val car = carRepository.findById(carId).orElse(null) ?: throw NotFoundException(ErrorCode.CAR_NOT_FOUND)
        car.modify(dto)
    }

    @Transactional
    fun delete(carId: Long): Void? {
        val car = carRepository.findById(carId).orElse(null) ?: throw NotFoundException(ErrorCode.CAR_NOT_FOUND)
        carRepository.delete(car)
        return null
    }
}
