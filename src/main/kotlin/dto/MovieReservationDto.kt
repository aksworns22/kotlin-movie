package dto

import java.time.LocalDateTime

data class MovieReservationDto(
    val movieName: String,
    val startTime: LocalDateTime,
    val seatName: String,
)
