package model.movie

import model.time.CinemaTimeRange

data class Movie(
    private val name: MovieName,
    val runningTime: RunningTime,
) {
    fun isSameRunningTime(cinemaTimeRange: CinemaTimeRange): Boolean = runningTime.isSameDuration(cinemaTimeRange)

    fun isSameName(movieName: MovieName): Boolean = name == movieName

    fun getName(): String = name.value
}
