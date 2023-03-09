package com.demo.dynamotestcontainers.controllers

import com.demo.dynamotestcontainers.data.Car
import com.demo.dynamotestcontainers.services.ICarService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
interface ICarController {

    val carService: ICarService

    @GetMapping("/{name}")
    suspend fun getCar(name: String): Car

    @PostMapping
    suspend fun createCar(car: Car)

    @DeleteMapping("/name")
    suspend fun deleteCar(name: String)
}