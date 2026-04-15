package view

import java.time.LocalDateTime

data class MovieReservationResultDto(
    val movieName: String,
    val startTime: LocalDateTime,
    val seatName: String,
)
