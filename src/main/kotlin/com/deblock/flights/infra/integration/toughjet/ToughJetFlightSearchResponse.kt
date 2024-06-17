package com.deblock.flights.infra.integration.toughjet

import java.math.BigDecimal
import java.time.Instant

data class ToughJetFlightSearchResponse(
    val carrier: String,
    val basePrice: BigDecimal,
    val tax: BigDecimal,
    val discount: String,
    val departureAirportName: String,
    val arrivalAirportName: String,
    val outboundDateTime: Instant,
    val inboundDateTime: Instant
)
