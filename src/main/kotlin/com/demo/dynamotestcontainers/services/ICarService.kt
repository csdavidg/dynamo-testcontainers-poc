package com.demo.dynamotestcontainers.services

import com.demo.dynamotestcontainers.data.Car

interface ICarService {

    suspend fun createCar(car: Car)

    suspend fun getCarByName(name: String): Car

    suspend fun deleteCar(name: String)
}