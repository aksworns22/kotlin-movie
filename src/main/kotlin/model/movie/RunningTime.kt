package model.movie

import model.time.CinemaTimeRange

@JvmInline
value class RunningTime(
    private val minute: Int,
) {
    init {
        require(minute > 0) { "상영 시간은 0분 초과이어야 합니다." }
    }

    fun isSameDuration(cinemaTimeRange: CinemaTimeRange): Boolean = cinemaTimeRange.durationMinute == minute
}
