package com.deblock.flights.application

import com.deblock.flights.domain.Flight
import java.math.BigDecimal
import java.time.LocalDateTime

data class FlightSearchResponse(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
) {
    companion object {
        fun fromFlight(flight: Flight): FlightSearchResponse =
            with(flight) {
                FlightSearchResponse(
                    airline,
                    supplier,
                    fare,
                    departureAirportCode,
                    destinationAirportCode,
                    departureDate,
                    arrivalDate
                )
            }
    }
}
