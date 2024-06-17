package com.deblock.flights.application

class InvalidRequestException(private val validationErrors: List<String>) : RuntimeException() {

    override val message: String
        get() = validationErrors.joinToString("\n")

    override fun toString(): String {
        return validationErrors.joinToString("\n")
    }
}
