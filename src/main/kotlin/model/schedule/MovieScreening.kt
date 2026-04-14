package model.schedule

import model.movie.Movie
import model.seat.Seat
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.util.Objects

class MovieScreening(
    val movie: Movie,
    val screenTime: CinemaTimeRange,
    val seatGroup: SeatGroup,
) {
    val seatCount: Int get() = seatGroup.size

    init {
        require(movie.isSameDuration(screenTime)) { "영화의 러닝타임과 상영관의 상영 시간이 일치하지 않습니다." }
    }

    override fun equals(other: Any?): Boolean {
        if (other is MovieScreening) {
            return movie == other.movie && screenTime == other.screenTime
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(movie.hashCode(), screenTime.hashCode())

    fun getSeat(seatPosition: SeatPosition): Seat = seatGroup[seatPosition]

    fun isSameScreenTime(otherTime: CinemaTimeRange): Boolean = otherTime.start == screenTime.start && otherTime.end == screenTime.end

    fun isSameStartDate(time: CinemaTime): Boolean = time.isEqualDate(screenTime.start)

    fun overlaps(other: CinemaTimeRange): Boolean = screenTime.overlaps(other)

    fun isSameStartDateTime(time: CinemaTime): Boolean = time.isEqual(screenTime.start)
}
