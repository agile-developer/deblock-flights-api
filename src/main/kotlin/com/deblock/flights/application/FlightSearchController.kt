package com.deblock.flights.application

import com.deblock.flights.application.FlightSearchResponse.Companion.fromFlight
import com.deblock.flights.domain.FlightSearchService
import com.deblock.flights.domain.SearchResult
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/deblock/flights")
class FlightSearchController(
    private val flightSearchService: FlightSearchService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    suspend fun searchFlights(
        @RequestBody flightSearchRequest: com.deblock.flights.application.FlightSearchRequest
    ): ResponseEntity<*> {

        flightSearchRequest.validate()
        return when(val searchResult = flightSearchService.searchFlights(flightSearchRequest)) {
            is SearchResult.Found -> ResponseEntity.ok(searchResult.flights.map { fromFlight(it) })
            is SearchResult.Empty -> ResponseEntity.ok(searchResult.NO_RESULTS)
            is SearchResult.UnsupportedAirport -> ResponseEntity.badRequest().body(searchResult.message)
            else -> ResponseEntity.internalServerError().body("Something went wrong")
        }
    }
}
