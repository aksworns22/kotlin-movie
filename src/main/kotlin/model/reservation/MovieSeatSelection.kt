package model.reservation

import model.movie.Movie
import model.payment.Money
import model.seat.Seat
import model.time.CinemaTimeRange
import java.util.Objects

class MovieSeatSelection(
    private val movie: Movie,
    private val screenTime: CinemaTimeRange,
    private val seat: Seat,
) {
    val price: Money get() = seat.price

    override fun equals(other: Any?): Boolean {
        if (other is MovieSeatSelection) {
            return movie == other.movie && screenTime.isEqual(other.screenTime) && seat == other.seat
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(movie, screenTime, seat)

    fun isBeforeScreeningStartHour(hour: Int): Boolean = screenTime.isBeforeStartHour(hour)

    fun isSameScreeningDay(dayOfMonth: Int): Boolean = screenTime.isSameStartDayOfMonth(dayOfMonth)

    fun isEqual(
        movie: Movie,
        screenTime: CinemaTimeRange,
    ): Boolean = this.movie == movie && this.screenTime.isEqual(screenTime)

    fun overlaps(screenTime: CinemaTimeRange): Boolean = this.screenTime.overlaps(screenTime)
}
