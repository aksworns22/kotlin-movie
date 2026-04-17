import dto.MovieScreeningDto
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime

fun main() {
    val movieRepository = MovieRepository("~/test")

    movieRepository.insertMovieScreenings(
        MovieScreeningDto("혼자사는남자", 1, 60, LocalDateTime.of(2026, 4, 8, 10, 0)),
        MovieScreeningDto("아이언맨", 2, 60, LocalDateTime.of(2026, 4, 9, 7, 0)),
        MovieScreeningDto("혼자사는남자", 3, 60, LocalDateTime.of(2026, 4, 10, 20, 0)),
    )

    CinemaController(
        movieRepository = movieRepository,
        moviePaymentController = MoviePaymentController(),
        movieReservationController =
            MovieReservationController(
                movieRepository = movieRepository,
                serviceTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(1, 1, 1, 0, 0)),
                        end = CinemaTime(LocalDateTime.of(99999, 1, 1, 0, 0)),
                    ),
            ),
    ).run()
}
