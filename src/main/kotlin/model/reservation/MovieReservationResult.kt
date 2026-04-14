package model.reservation

import model.movie.Movie
import model.schedule.MovieScreening
import model.seat.Seat
import model.time.CinemaTimeRange

data class MovieReservationResult(
    val movie: Movie,
    val screenTime: CinemaTimeRange,
    val seat: Seat,
) {
    fun isEqual(movieScreening: MovieScreening): Boolean = movieScreening.movie == movie && movieScreening.screenTime == screenTime
}
