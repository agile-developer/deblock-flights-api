package com.deblock.flights.domain

interface SearchResult {
    data class Found(val flights: List<Flight>): SearchResult
    data object Empty: SearchResult {
        const val NO_RESULTS = "Search returned no results"
    }
    data class UnsupportedAirport(val message: String): SearchResult
    data class Error(private val carrier: String): SearchResult {
        val message: String get() = "Encountered error calling carrier: $carrier"
    }
}
