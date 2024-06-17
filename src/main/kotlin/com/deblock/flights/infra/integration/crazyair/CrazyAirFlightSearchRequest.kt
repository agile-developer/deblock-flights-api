package com.deblock.flights.infra.integration.crazyair

import java.time.LocalDate

data class CrazyAirFlightSearchRequest(
    val origin: String,
    val destination: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val passengerCount: Int,
)