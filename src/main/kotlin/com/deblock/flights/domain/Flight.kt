package com.deblock.flights.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: Airport,
    val destinationAirportCode: Airport,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
)
