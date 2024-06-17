package com.deblock.flights

import com.deblock.flights.application.FlightSearchResponse
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
class AggregatorApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

//	@Test
//	fun contextLoads() {
//	}

    companion object {
        @RegisterExtension
        @JvmStatic
        val wireMock: WireMockExtension = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build()

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("crazyair_api.base_uri") { wireMock.baseUrl() + "/crazyair" }
            registry.add("toughjet_api.base_uri") { wireMock.baseUrl() + "/toughjet"}
        }
    }

    @Test
    fun `should return flights sorted by fare when both suppliers return flights`(): Unit = runBlocking {
        // arrange
        val crazyAirResponse = """
			[
			  {
				"airline": "Emirates",
				"price": 550.00,
				"cabinClass": "E",
				"departureAirportCode": "LHR",
				"destinationAirportCode": "LHR",
				"departureDate": "2024-07-01T08:00:00",
				"arrivalDate": "2024-07-10T08:00:00"
			  },
			  {
				"airline": "British Airways",
				"price": 650.00,
				"cabinClass": "B",
				"departureAirportCode": "LHR",
				"destinationAirportCode": "LHR",
				"departureDate": "2024-07-01T08:00:00",
				"arrivalDate": "2024-07-10T08:00:00"
			  }
			]			
		""".trimIndent()
        wireMock.stubFor(
            post(urlMatching("/crazyair/flights"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(crazyAirResponse)
                )
        )
        val toughJetResponse = """
            [
              {
                "carrier": "Turkish Airlines",
                "basePrice": 650.00,
                "tax": 97.50,
                "discount": "5%",
                "departureAirportName": "LHR",
                "arrivalAirportName": "DXB",
                "outboundDateTime": "2024-07-01T08:00:00Z",
                "inboundDateTime": "2024-07-10T08:00:00Z"
              },
              {
                "carrier": "Lufthansa",
                "basePrice": 450.00,
                "tax": 67.50,
                "discount": "5%",
                "departureAirportName": "LHR",
                "arrivalAirportName": "DXB",
                "outboundDateTime": "2024-07-01T08:00:00Z",
                "inboundDateTime": "2024-07-10T08:00:00Z"
              }
            ]            
        """.trimIndent()
        wireMock.stubFor(
            post(urlMatching("/toughjet/flights"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(toughJetResponse)
                )
        )

        val searchRequest = """
			{
				"origin": "LHR",
				"destination": "DXB",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 4
			}
		""".trimIndent()

        // act
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequest)
        ).andReturn()

        // assert
        assertThat(result.response.status).isEqualTo(200)
        assertThat(result.asyncResult).isInstanceOf(ResponseEntity::class.java)
        val flightSearchResponses = (result.asyncResult as ResponseEntity<List<FlightSearchResponse>>).body
        assertThat(flightSearchResponses).isInstanceOf(List::class.java)
        assertThat(flightSearchResponses?.size).isEqualTo(4)
        assertThat(flightSearchResponses?.get(0)?.fare).isEqualTo(BigDecimal("495.00"))
        assertThat(flightSearchResponses?.get(1)?.fare).isEqualTo(BigDecimal("550.00"))
        assertThat(flightSearchResponses?.get(2)?.fare).isEqualTo(BigDecimal("650.00"))
        assertThat(flightSearchResponses?.get(3)?.fare).isEqualTo(BigDecimal("715.00"))
    }

    @Test
    fun `should return flights from one supplier, if the other returns non-200 response`(): Unit = runBlocking {
        // arrange
        val crazyAirResponse = """
			[
			  {
				"airline": "Emirates",
				"price": 550.00,
				"cabinClass": "E",
				"departureAirportCode": "LHR",
				"destinationAirportCode": "LHR",
				"departureDate": "2024-07-01T08:00:00",
				"arrivalDate": "2024-07-10T08:00:00"
			  },
			  {
				"airline": "British Airways",
				"price": 650.00,
				"cabinClass": "B",
				"departureAirportCode": "LHR",
				"destinationAirportCode": "LHR",
				"departureDate": "2024-07-01T08:00:00",
				"arrivalDate": "2024-07-10T08:00:00"
			  }
			]			
		""".trimIndent()
        wireMock.stubFor(
            post(urlMatching("/crazyair/flights"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", APPLICATION_JSON_VALUE)
                        .withBody(crazyAirResponse)
                )
        )
        wireMock.stubFor(
            post(urlMatching("/toughjet/flights"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withStatusMessage("Internal Server Error")
                )
        )

        val searchRequest = """
			{
				"origin": "LHR",
				"destination": "DXB",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 4
			}
		""".trimIndent()

        // act
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequest)
        ).andReturn()

        // assert
        assertThat(result.response.status).isEqualTo(200)
        assertThat(result.asyncResult).isInstanceOf(ResponseEntity::class.java)
        val flightSearchResponses = (result.asyncResult as ResponseEntity<List<FlightSearchResponse>>).body
        assertThat(flightSearchResponses).isInstanceOf(List::class.java)
        assertThat(flightSearchResponses?.size).isEqualTo(2)
        assertThat(flightSearchResponses?.get(0)?.supplier).isEqualTo("CrazyAir")
        assertThat(flightSearchResponses?.get(1)?.supplier).isEqualTo("CrazyAir")
    }
    @Test
    fun `should return '400 Bad Request' when searching for unsupported airport code`(): Unit = runBlocking {
        val searchRequest = """
			{
				"origin": "LGW",
				"destination": "DXB",
				"departureDate": "2024-09-01",
				"returnDate": "2024-09-10",
				"numberOfPassengers": 4
			}
		""".trimIndent()

        // act
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/deblock/flights")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(searchRequest)
        ).andReturn()

        // assert
        val responseEntity = (result.asyncResult as ResponseEntity<*>)
        assertThat(responseEntity.statusCode.value()).isEqualTo(400)
        assertThat(responseEntity.body).isEqualTo("Origin: LGW is not a supported airport code")
    }
}
