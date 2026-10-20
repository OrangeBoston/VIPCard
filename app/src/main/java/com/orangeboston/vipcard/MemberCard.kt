package com.orangeboston.vipcard

data class MemberCard(
    val id: Long = System.currentTimeMillis(),
    val storeName: String,
    val phoneNumber: String
)
