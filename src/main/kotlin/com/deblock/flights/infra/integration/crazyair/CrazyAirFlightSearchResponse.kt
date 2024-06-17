package com.deblock.flights.infra.integration.crazyair

import java.math.BigDecimal
import java.time.LocalDateTime

data class CrazyAirFlightSearchResponse(
    val airline: String,
    val price: BigDecimal,
    val cabinClass: String,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime
)
