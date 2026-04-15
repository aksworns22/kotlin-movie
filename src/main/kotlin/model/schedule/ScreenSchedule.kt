package model.schedule

import model.movie.MovieName
import model.time.CinemaTimeRange

class ScreenSchedule(
    private val screenId: String,
    private val servicePeriod: CinemaTimeRange,
    movieScreenings: List<MovieScreening>,
) {
    private val movieScreenings = movieScreenings.toList()

    init {
        require(movieScreenings.all { it.isBetween(servicePeriod) }) { "상영관의 운영 시간을 벗어나도록 영화를 배정할 수 없습니다." }
        movieScreenings.forEachIndexed { index, current ->
            movieScreenings.drop(index + 1).forEach { other ->
                require(!current.overlaps(other)) {
                    "상영 시간이 겹치는 경우는 배정할 수 없습니다."
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is ScreenSchedule) {
            return this.screenId == other.screenId
        }
        return false
    }

    override fun hashCode(): Int = screenId.hashCode()

    operator fun get(movieName: MovieName): MovieSchedule =
        MovieSchedule(
            movieName = movieName,
            movieScreenings = movieScreenings.filter { it.isScreeningMovie(movieName) },
        )
}
