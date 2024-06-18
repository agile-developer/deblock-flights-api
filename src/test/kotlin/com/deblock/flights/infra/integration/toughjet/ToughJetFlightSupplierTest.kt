package com.deblock.flights.infra.integration.toughjet

import com.deblock.flights.application.FlightSearchRequest
import com.deblock.flights.domain.SearchResult
import com.deblock.flights.infra.integration.toughjet.ToughJetSearchConverter.convertRequest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClient
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class ToughJetFlightSupplierTest {

    private val restClient = mockk<RestClient>()
    private val toughJetFlightSupplier = ToughJetFlightSupplier("http://localhost/toughjet", true, restClient)

    @Test
    fun `should return 'Found' with flights when REST client returns results, applying expected conversion`(): Unit = runBlocking {
        // arrange
        val origin = "LHR"
        val destination = "DXB"
        val departureDate = LocalDate.of(2024, 9, 1)
        val returnDate = LocalDate.of(2024, 9, 10)
        val numberOfPassengers = 4
        val searchRequest = FlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        val flightSearchResponseListTypeRef =
            object : ParameterizedTypeReference<List<ToughJetFlightSearchResponse>>() {}
        val responseEntity = mockk<ResponseEntity<List<ToughJetFlightSearchResponse>>>()
        val toughJetFlight = ToughJetFlightSearchResponse(
            "Emirates",
            BigDecimal("850.00"),
            BigDecimal("127.50"),
            "5%",
            "LHR",
            "DXB",
            LocalDateTime.of(departureDate, LocalTime.of(8, 0)).atOffset(ZoneOffset.UTC).toInstant(),
            LocalDateTime.of(returnDate, LocalTime.of(8, 0)).atOffset(ZoneOffset.UTC).toInstant()
        )
        every { restClient.post()
            .uri("http://localhost/toughjet/flights")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(convertRequest(searchRequest))
            .retrieve()
            .toEntity(flightSearchResponseListTypeRef)
        } returns responseEntity
        every { responseEntity.statusCode } returns HttpStatus.OK
        every { responseEntity.body } returns listOf(toughJetFlight)

        // act
        val response = toughJetFlightSupplier.searchFlights(searchRequest)

        // assert
        assertThat(response).isInstanceOf(SearchResult.Found::class.java)
        val flight = (response as SearchResult.Found).flights[0]
        val expectedFare = BigDecimal("935.00")
        assertThat(flight.fare).isEqualTo(expectedFare)
        assertThat(flight.airline).isEqualTo(toughJetFlight.carrier)
        assertThat(flight.supplier).isEqualTo(toughJetFlightSupplier.name)
        assertThat(flight.departureDate).isEqualTo(LocalDateTime.of(departureDate, LocalTime.of(8, 0)))
        assertThat(flight.arrivalDate).isEqualTo(LocalDateTime.of(returnDate, LocalTime.of(8, 0)))
        assertThat(flight.departureAirportCode.name).isEqualTo(toughJetFlight.departureAirportName)
        assertThat(flight.destinationAirportCode.name).isEqualTo(toughJetFlight.arrivalAirportName)
    }

    @Test
    fun `should return 'Empty' when REST client returns no result`(): Unit = runBlocking {
        // arrange
        val origin = "LHR"
        val destination = "DXB"
        val departureDate = LocalDate.of(2024, 9, 1)
        val returnDate = LocalDate.of(2024, 9, 10)
        val numberOfPassengers = 4
        val searchRequest = FlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        val flightSearchResponseListTypeRef =
            object : ParameterizedTypeReference<List<ToughJetFlightSearchResponse>>() {}
        val responseEntity = mockk<ResponseEntity<List<ToughJetFlightSearchResponse>>>()
        every { restClient.post()
            .uri("http://localhost/toughjet/flights")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(convertRequest(searchRequest))
            .retrieve()
            .toEntity(flightSearchResponseListTypeRef)
        } returns responseEntity
        every { responseEntity.statusCode } returns HttpStatus.OK
        every { responseEntity.body } returns emptyList()
        // act
        val response = toughJetFlightSupplier.searchFlights(searchRequest)

        // assert
        assertThat(response).isInstanceOf(SearchResult.Empty::class.java)
    }

    @Test
    fun `should return 'Error' when REST client returns non-200 OK result`(): Unit = runBlocking {
        // arrange
        val origin = "LHR"
        val destination = "DXB"
        val departureDate = LocalDate.of(2024, 9, 1)
        val returnDate = LocalDate.of(2024, 9, 10)
        val numberOfPassengers = 4
        val searchRequest = FlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        val flightSearchResponseListTypeRef =
            object : ParameterizedTypeReference<List<ToughJetFlightSearchResponse>>() {}
        val responseEntity = mockk<ResponseEntity<List<ToughJetFlightSearchResponse>>>()
        every { restClient.post()
            .uri("http://localhost/toughjet/flights")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(convertRequest(searchRequest))
            .retrieve()
            .toEntity(flightSearchResponseListTypeRef)
        } returns responseEntity
        every { responseEntity.statusCode } returns HttpStatus.INTERNAL_SERVER_ERROR

        // act
        val response = toughJetFlightSupplier.searchFlights(searchRequest)

        // assert
        assertThat(response).isInstanceOf(SearchResult.Error::class.java)
    }
}