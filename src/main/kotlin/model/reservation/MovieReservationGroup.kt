package model.reservation

import model.schedule.MovieScreening
import model.seat.SeatPosition

class MovieReservationGroup(
    movieReservations: Set<MovieSeatSelection>,
) : Iterable<MovieSeatSelection> by movieReservations {
    private val movieReservationGroup = movieReservations.toSet()

    override fun equals(other: Any?): Boolean {
        if (other is MovieReservationGroup) {
            return movieReservationGroup == other.movieReservationGroup
        }
        return false
    }

    override fun hashCode(): Int = movieReservationGroup.hashCode()

    operator fun minus(other: MovieReservationGroup): MovieReservationGroup =
        MovieReservationGroup(movieReservationGroup - other.movieReservationGroup)

    fun reserveSeat(
        movieScreening: MovieScreening,
        seatPosition: SeatPosition,
    ): MovieReservationGroup {
        val movieSeatSelection = movieScreening.selectSeat(seatPosition)

        if (!isReservable(movieScreening)) {
            throw IllegalArgumentException("서로 시간이 겹치는 상영은 함께 예매할 수 없습니다.")
        }

        if (!hasAvailableSeat(movieScreening)) {
            throw IllegalArgumentException("예매 가능한 좌석이 없습니다.")
        }

        if (movieReservationGroup.any { movieSeatSelection == it }) {
            throw IllegalArgumentException("이미 예약된 좌석입니다.")
        }

        return MovieReservationGroup(movieReservationGroup + movieSeatSelection)
    }

    fun isReservable(movieScreening: MovieScreening): Boolean =
        !movieReservationGroup.any {
            !movieScreening.isEqual(it) && movieScreening.overlaps(it)
        }

    fun hasAvailableSeat(movieScreening: MovieScreening): Boolean {
        val reservedSeatCount =
            movieReservationGroup.count { movieSeatSelection ->
                movieScreening.isEqual(movieSeatSelection)
            }

        return reservedSeatCount < movieScreening.seatCount
    }
}
