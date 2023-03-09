package com.demo.dynamotestcontainers.controllers

import com.demo.dynamotestcontainers.data.Car
import com.demo.dynamotestcontainers.services.ICarService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/car")
class CarController(override val carService: ICarService): ICarController  {

    @PostMapping
    override suspend fun createCar(@RequestBody car: Car) {
        carService.createCar(car)
    }

    @GetMapping("/{name}")
    override suspend fun getCar(@PathVariable("name") name: String): Car {
        return carService.getCarByName(name)
    }

    @DeleteMapping("/name")
    override suspend fun deleteCar(@PathVariable("name") name: String) {
        carService.deleteCar(name)
    }


}