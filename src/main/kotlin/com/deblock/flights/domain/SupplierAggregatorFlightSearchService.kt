package com.deblock.flights.domain

import com.deblock.flights.application.FlightSearchRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SupplierAggregatorFlightSearchService(
    private val suppliers: List<FlightsSupplier>
) : FlightSearchService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun searchFlights(searchRequest: FlightSearchRequest): SearchResult {
        logger.info("Available suppliers: ${suppliers.map { it.name }}")

        runCatching {
            checkAirport(searchRequest.origin, "Origin")
            checkAirport(searchRequest.destination, "Destination")
        }.onFailure {
            return SearchResult.UnsupportedAirport(it.message!!)
        }

        return coroutineScope {
            suppliers
                .filter {
                    logger.info("Supplier: ${it.name + ", isEnabled: " + it.isEnabled}")
                    it.isEnabled
                }
                .map {
                    async {
                        it.searchFlights(searchRequest)
                    }
                }
                .awaitAll()
                .filterIsInstance<SearchResult.Found>()
                .map { it.flights }
                .flatten()
                .sortedBy { it.fare }
                .let { SearchResult.Found(it) }
        }
    }

    private fun checkAirport(airportCode: String, journeyLeg: String): Airport {
        return runCatching {
            Airport.valueOf(airportCode)
        }.getOrElse {
            throw IllegalArgumentException("$journeyLeg: $airportCode is not a supported airport code")
        }
    }
}
