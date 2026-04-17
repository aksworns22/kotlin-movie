package dto

import java.time.LocalDateTime

data class MovieScreeningDto(
    val title: String,
    val screenId: Int,
    val runningTime: Int,
    val startTime: LocalDateTime,
)
