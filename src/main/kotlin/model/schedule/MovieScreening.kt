package model.schedule

import model.movie.Movie
import model.movie.MovieName
import model.reservation.MovieSeatSelection
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime
import java.util.Objects

class MovieScreening(
    private val movie: Movie,
    private val screenTime: CinemaTimeRange,
    private val seatGroup: SeatGroup,
) {
    val seatCount: Int get() = seatGroup.size

    init {
        require(movie.isSameRunningTime(screenTime)) { "영화의 러닝타임과 상영관의 상영 시간이 일치하지 않습니다." }
    }

    override fun equals(other: Any?): Boolean {
        if (other is MovieScreening) {
            return movie == other.movie && screenTime == other.screenTime
        }
        return false
    }

    fun isEqual(movieSeatSelection: MovieSeatSelection): Boolean = movieSeatSelection.isEqual(movie, screenTime)

    override fun hashCode(): Int = Objects.hash(movie.hashCode(), screenTime.hashCode())

    fun isSameStartDate(time: CinemaTime): Boolean = screenTime.isSameStartDate(time)

    fun overlaps(other: MovieScreening): Boolean = screenTime.overlaps(other.screenTime)

    fun overlaps(movieSeatSelection: MovieSeatSelection): Boolean = movieSeatSelection.overlaps(screenTime)

    fun isSameStartDateTime(time: CinemaTime): Boolean = screenTime.isStartEqual(time)

    fun isScreeningMovie(movieName: MovieName): Boolean = movie.isSameName(movieName)

    fun isBetween(servicePeriod: CinemaTimeRange): Boolean = servicePeriod.contains(screenTime)

    fun selectSeat(seatPosition: SeatPosition): MovieSeatSelection =
        MovieSeatSelection(
            movie = movie,
            screenTime = screenTime,
            seat = seatGroup[seatPosition],
        )

    fun getMovieStartTime(): LocalDateTime = screenTime.getStartTime()

    fun getAllSeatNames(): List<String> = seatGroup.getAllSeatNames()
}
