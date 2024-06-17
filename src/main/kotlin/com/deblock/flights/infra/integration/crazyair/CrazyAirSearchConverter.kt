package com.deblock.flights.infra.integration.crazyair

import com.deblock.flights.application.FlightSearchRequest
import com.deblock.flights.domain.Flight

object CrazyAirSearchConverter {

    fun convertRequest(flightSearchRequest: FlightSearchRequest) =
        with(flightSearchRequest) {
            CrazyAirFlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        }

    fun convertResponse(crazyAirFlightSearchResponse: CrazyAirFlightSearchResponse): Flight {
        return with(crazyAirFlightSearchResponse) {
            Flight(
                airline,
                "CrazyAir",
                price,
                departureAirportCode,
                destinationAirportCode,
                departureDate,
                arrivalDate
            )
        }
    }
}
