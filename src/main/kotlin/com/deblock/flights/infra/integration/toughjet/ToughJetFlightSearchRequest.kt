package com.deblock.flights.infra.integration.toughjet

import java.time.LocalDate

data class ToughJetFlightSearchRequest(
    val from: String,
    val to: String,
    val outboundDate: LocalDate,
    val inboundDate: LocalDate,
    val numberOfAdults: Int,
)
