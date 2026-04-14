package model.reservation

import model.movie.Movie
import model.payment.Money
import model.schedule.MovieScreening
import model.seat.Seat
import model.time.CinemaTimeRange

data class MovieReservationResult(
    val movie: Movie,
    val screenTime: CinemaTimeRange,
    val seat: Seat,
) {
    val price: Money get() = seat.price

    fun isEqual(movieScreening: MovieScreening): Boolean = movieScreening.movie == movie && movieScreening.isSameScreenTime(screenTime)
}
