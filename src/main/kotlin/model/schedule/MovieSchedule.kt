package model.schedule

import model.time.CinemaTime

class MovieSchedule(
    movieScreenings: List<MovieScreening>,
) : Iterable<MovieScreening> {
    private val movieScreenings = movieScreenings.toList()
    val size: Int = movieScreenings.size

    init {
        require(movieScreenings.distinctBy { it.movie }.size <= 1) {
            "일정에 포함된 영화들은 모두 동일한 영화여야 합니다."
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

    override fun iterator(): Iterator<MovieScreening> = movieScreenings.iterator()

    fun getMovieSchedule(time: CinemaTime): MovieSchedule =
        MovieSchedule(
            movieScreenings.filter { screen ->
                screen.isSameStartDate(time)
            },
        )

    fun isEmpty(): Boolean = movieScreenings.isEmpty()
}
