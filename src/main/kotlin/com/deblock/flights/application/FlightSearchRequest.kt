package com.deblock.flights.application

import com.deblock.flights.domain.SortBy
import java.time.LocalDate

data class FlightSearchRequest(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val numberOfPassengers: Int,
    val sortBy: String = "FARE"
) {
    fun validate() {
        val validationErrors = mutableListOf<String>()
        if (origin.isBlank() || origin.length != 3) {
            validationErrors.add("Origin is invalid")
        }
        if (destination.isBlank() || destination.length != 3) {
            validationErrors.add("Destination is invalid")
        }
        if (origin == destination) {
            validationErrors.add("Origin and destination cannot be the same")
        }
        if (departureDate.isBefore(LocalDate.now())) {
            validationErrors.add("Departure date cannot be in the past")
        }
        if (returnDate.isBefore(LocalDate.now())) {
            validationErrors.add("Return date cannot be in the past")
        }
        if (returnDate.isBefore(departureDate)) {
            validationErrors.add("Return date must be later than departure date")
        }
        if (numberOfPassengers < 1 || numberOfPassengers > 4) {
            validationErrors.add("Passenger count must be greater than zero and maximum 4")
        }
        runCatching {
            SortBy.valueOf(sortBy.uppercase())
        }.getOrElse {
            validationErrors.add("Sort by field: $sortBy is invalid")
        }

        if (validationErrors.isNotEmpty()) throw InvalidRequestException(validationErrors)
    }
}
