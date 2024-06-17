package com.deblock.flights.domain

import com.deblock.flights.application.FlightSearchRequest

interface FlightsSupplier {
    suspend fun searchFlights(searchRequest: FlightSearchRequest): SearchResult

    val isEnabled: Boolean
    val name: String
}
