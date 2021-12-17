package com.example.lab2.models

import kotlinx.serialization.Serializable
@Serializable
data class Reservation(var barberId: Int, var serviceId: Int, var date: String, var userName: String)