package model.schedule

import model.movie.MovieName
import model.time.CinemaTime
import java.time.LocalDateTime

class MovieSchedule(
    private val movieName: MovieName,
    movieScreenings: List<MovieScreening>,
) : Iterable<MovieScreening> by movieScreenings {
    private val movieScreenings = movieScreenings.toList()
    val size: Int = movieScreenings.size

    init {
        require(
            movieScreenings.all { movieScreening ->
                movieScreening.isScreeningMovie(movieName)
            },
        ) {
            "일정에 포함된 영화들은 모두 동일한 영화 이름만 가능합니다."
        }
    }

    operator fun get(startTime: CinemaTime): MovieScreening =
        movieScreenings.firstOrNull { it.isSameStartDateTime(startTime) }
            ?: throw IllegalArgumentException("해당 시간에 존재하는 영화가 없습니다.")

    override fun equals(other: Any?): Boolean {
        if (other is MovieSchedule) {
            return movieScreenings == other.movieScreenings
        }
        return false
    }

    override fun hashCode(): Int = movieScreenings.hashCode()

    fun getSameDayMovieSchedule(time: CinemaTime): MovieSchedule =
        MovieSchedule(
            movieName = movieName,
            movieScreenings.filter { screen ->
                screen.isSameStartDate(time)
            },
        )

    fun isEmpty(): Boolean = movieScreenings.isEmpty()

    fun getAllMovieStartTime(): List<LocalDateTime> =
        movieScreenings.map {
            it.getMovieStartTime()
        }
}
