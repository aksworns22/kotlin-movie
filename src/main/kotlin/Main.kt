import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime

fun main() {
    val repository = MovieRepository("~/test")

    repository.insertMovieScreenings(
        MovieScreeningDto("혼자사는남자", 60, 1, LocalDateTime.of(2026, 4, 8, 10, 0)),
        MovieScreeningDto("아이언맨", 60, 2, LocalDateTime.of(2026, 4, 9, 7, 0)),
        MovieScreeningDto("혼자사는남자", 60, 3, LocalDateTime.of(2026, 4, 10, 20, 0)),
    )

    CinemaController(
        moviePaymentController = MoviePaymentController(),
        movieReservationController =
            MovieReservationController(
                movieRepository = repository,
                serviceTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(1, 1, 1, 0, 0)),
                        end = CinemaTime(LocalDateTime.of(99999, 1, 1, 0, 0)),
                    ),
            ),
    ).run()
}
