package com.deblock.flights.infra.integration.toughjet

import com.deblock.flights.application.FlightSearchRequest
import com.deblock.flights.domain.Flight
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

object ToughJetSearchConverter {

    private val logger = LoggerFactory.getLogger(ToughJetFlightSearchResponse::class.java)

    fun convertRequest(flightSearchRequest: FlightSearchRequest) =
        with(flightSearchRequest) {
            ToughJetFlightSearchRequest(origin, destination, departureDate, returnDate, numberOfPassengers)
        }

    fun convertResponse(toughJetFlightSearchResponse: ToughJetFlightSearchResponse): Flight {
        return with(toughJetFlightSearchResponse) {
            Flight(
                carrier,
                "ToughJet",
                calculateFare(basePrice, tax, discount),
                departureAirportName,
                arrivalAirportName,
                LocalDateTime.ofInstant(outboundDateTime, ZoneOffset.UTC),
                LocalDateTime.ofInstant(inboundDateTime, ZoneOffset.UTC)
            )
        }
    }

    private fun calculateFare(basePrice: BigDecimal, tax: BigDecimal, discountPercentage: String): BigDecimal {
        logger.info("Base price: $basePrice, tax: $tax")
        val discount = discountPercentage.removeSuffix("%").toDouble()
        val discountMultiplier = if (discount <= 0) 1.00 else discount / 100.00
        val basePriceInPence = basePrice.movePointRight(2).toLong()
        val taxInPence = tax.movePointRight(2).toLong()
        val discountValueInPence = basePriceInPence * discountMultiplier
        logger.info("Discount value: $discountValueInPence")
        return BigDecimal(basePriceInPence - discountValueInPence + taxInPence).movePointLeft(2)
    }
}
