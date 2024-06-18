package com.deblock.flights.application

import com.deblock.flights.domain.FlightSearchService
import com.deblock.flights.domain.SearchResult
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import java.time.LocalDate

@WebMvcTest(com.deblock.flights.application.FlightSearchController::class)
class FlightSearchControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var flightSearchService: FlightSearchService

    @Test
    fun `should return '200 OK' and empty result when service returns no results`(): Unit = runBlocking {
        // arrange
        val origin = "LHR"
        val destination = "DXB"
        val departureDate = LocalDate.of(2024, 9, 1)
        val returnDate = LocalDate.of(2024, 9, 10)
        val numberOfPassengers = 4
        val searchRequest = FlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        `when`(flightSearchService.searchFlights(searchRequest)).thenReturn(SearchResult.Empty)

        // act
        val searchRequestJson = """
			{
				"origin": "LHR",
				"destination": "DXB",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 4
			}
		""".trimIndent()
        val result = mockMvc.perform(
            post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequestJson)
        ).andReturn()

        // assert
        assertThat(result.response.status).isEqualTo(200)
        val responseEntity = result.asyncResult as ResponseEntity<String>
        assertThat(responseEntity.body!!).isEqualTo(SearchResult.Empty.NO_RESULTS)
    }

    @Test
    fun `should return an error when request has more than 4 passengers`(): Unit = runBlocking {
        // arrange

        // act
        val searchRequestJson = """
			{
				"origin": "LHR",
				"destination": "DXB",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 5
			}
		""".trimIndent()
        val result = mockMvc.perform(
            post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequestJson)
        ).andReturn()

        // assert
        //assertThat(result.response.status).isEqualTo(400)
        val invalidRequestException = result.asyncResult as InvalidRequestException
        assertThat(invalidRequestException.message).isEqualTo("Passenger count must be greater than zero and maximum 4")
    }

    @Test
    fun `should return an error when request has the same origin and destination`(): Unit = runBlocking {
        // arrange

        // act
        val searchRequestJson = """
			{
				"origin": "LHR",
				"destination": "LHR",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 4
			}
		""".trimIndent()
        val result = mockMvc.perform(
            post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequestJson)
        ).andReturn()

        // assert
        //assertThat(result.response.status).isEqualTo(400)
        val invalidRequestException = result.asyncResult as InvalidRequestException
        assertThat(invalidRequestException.message).isEqualTo("Origin and destination cannot be the same")
    }
}
