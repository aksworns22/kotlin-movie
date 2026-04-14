package model.reservation

import model.schedule.MovieScreening
import model.seat.SeatPosition

class MovieReservationGroup(
    movieReservations: Set<MovieReservationResult>,
) : Iterable<MovieReservationResult> {
    private val movieReservationGroup = movieReservations.toSet()

    override fun equals(other: Any?): Boolean {
        if (other is MovieReservationGroup) return movieReservationGroup == other.movieReservationGroup
        return false
    }

    override fun hashCode(): Int = movieReservationGroup.hashCode()

    operator fun minus(other: MovieReservationGroup): MovieReservationGroup =
        MovieReservationGroup(movieReservationGroup - other.movieReservationGroup)

    override fun iterator(): Iterator<MovieReservationResult> = movieReservationGroup.iterator()

    fun isReservable(movieScreening: MovieScreening): Boolean =
        !movieReservationGroup.any {
            movieScreening.screenTime.overlaps(it.screenTime) &&
                !it.isEqual(
                    movieScreening,
                )
        }

    fun hasAvailableSeat(movieScreening: MovieScreening): Boolean {
        val reservedSeatCount =
            movieReservationGroup.count {
                it.isEqual(movieScreening)
            }

        return reservedSeatCount < movieScreening.seatGroup.size
    }

    fun reserve(
        movieScreening: MovieScreening,
        seatPosition: SeatPosition,
    ): MovieReservationGroup {
        val movieReservationResult =
            MovieReservationResult(
                movie = movieScreening.movie,
                screenTime = movieScreening.screenTime,
                seat = movieScreening.getSeat(seatPosition),
            )

        if (!isReservable(movieScreening)) {
            throw IllegalArgumentException("서로 시간이 겹치는 상영은 함께 예매할 수 없습니다.")
        }

        if (movieReservationGroup.any { movieReservationResult == it }) {
            throw IllegalArgumentException("이미 예약된 좌석입니다.")
        }
        return MovieReservationGroup(movieReservationGroup + movieReservationResult)
    }
}
