package com.deblock.flights.infra.integration.crazyair

import com.deblock.flights.application.FlightSearchRequest
import com.deblock.flights.domain.FlightsSupplier
import com.deblock.flights.domain.SearchResult
import com.deblock.flights.infra.integration.crazyair.CrazyAirSearchConverter.convertRequest
import com.deblock.flights.infra.integration.crazyair.CrazyAirSearchConverter.convertResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CrazyAirFlightSupplier(
    @Value("\${crazyair_api.base_uri}")
    private val baseUri: String,
    @Value("\${crazyair_api.enabled}")
    private val enabled: Boolean,
    private val restClient: RestClient
) : FlightsSupplier {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun searchFlights(searchRequest: FlightSearchRequest): SearchResult {
        logger.info("Calling CrazyAir API to search for flights")

        val flightSearchResponseListTypeRef =
            object : ParameterizedTypeReference<List<CrazyAirFlightSearchResponse>>() {}

        val httpResponse = runCatching {
            val crazyAirFlightSearchResponse: ResponseEntity<List<CrazyAirFlightSearchResponse>> = restClient.post()
                .uri("$baseUri/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(convertRequest(searchRequest))
                .retrieve()
                .toEntity(flightSearchResponseListTypeRef)
            crazyAirFlightSearchResponse
        }.getOrElse { exception ->
            logger.error("Encountered exception calling supplier $name: ${exception.message}")
            return SearchResult.Error(name)
        }

        logger.info("Received HTTP status code: ${httpResponse.statusCode}, from supplier: $name")
        return when (httpResponse.statusCode) {
            HttpStatus.OK -> {
                val flights = httpResponse.body?.map { convertResponse(it) } ?: emptyList()
                if (flights.isNotEmpty()) SearchResult.Found(flights) else SearchResult.Empty
            }

            else -> SearchResult.Error(name)
        }
    }

    override val isEnabled: Boolean
        get() {
            return enabled
        }

    override val name: String
        get() {
            return "CrazyAir"
        }
}
