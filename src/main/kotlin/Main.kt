import model.CinemaConstants
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieScreening
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime

fun main() {
    val movieRepository = MovieRepository("~/test")

    movieRepository.insertMovieScreenings(
        MovieScreening(
            screenId = 1,
            movie = Movie(MovieName("혼자사는남자"), RunningTime(60)),
            screenTime =
                CinemaTimeRange(
                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 0)),
                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                ),
            seatGroup = CinemaConstants.fixedSeatGroup,
        ),
        MovieScreening(
            screenId = 2,
            movie = Movie(MovieName("아이언맨"), RunningTime(60)),
            screenTime =
                CinemaTimeRange(
                    start = CinemaTime(LocalDateTime.of(2026, 4, 9, 7, 0)),
                    end = CinemaTime(LocalDateTime.of(2026, 4, 9, 8, 0)),
                ),
            seatGroup = CinemaConstants.fixedSeatGroup,
        ),
        MovieScreening(
            screenId = 3,
            movie = Movie(MovieName("혼자사는남자"), RunningTime(60)),
            screenTime =
                CinemaTimeRange(
                    start = CinemaTime(LocalDateTime.of(2026, 4, 10, 20, 0)),
                    end = CinemaTime(LocalDateTime.of(2026, 4, 10, 21, 0)),
                ),
            seatGroup = CinemaConstants.fixedSeatGroup,
        ),
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
