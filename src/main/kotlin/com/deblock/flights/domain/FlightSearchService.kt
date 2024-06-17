package com.deblock.flights.domain

import com.deblock.flights.application.FlightSearchRequest

interface FlightSearchService {
    suspend fun searchFlights(searchRequest: FlightSearchRequest): SearchResult
}
