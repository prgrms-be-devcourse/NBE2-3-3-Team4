package com.nbe2_3_3_team4.backend.domain.order.entity.enums

enum class PaymentStatus(val message : String) {
    CANCELED("결제 취소"),
    WAITING("결제 대기"),
    COMPLETE("결제 완료");

    override fun toString(): String = message
}