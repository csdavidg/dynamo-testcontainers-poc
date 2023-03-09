package com.demo.dynamotestcontainers.exceptions.handlers

import com.demo.dynamotestcontainers.exceptions.CarNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlerAdvice {

    @ExceptionHandler
    fun handleIllegalStateException(ex: CarNotFoundException): ResponseEntity<String> {
        return ResponseEntity(ex.message, HttpStatus.BAD_REQUEST)
    }
}