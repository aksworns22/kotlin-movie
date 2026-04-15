package schedule

import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieSchedule
import model.schedule.MovieScreening
import model.seat.SeatGroup
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MovieScheduleTest {
    @Test
    fun `특정 영화의 상영 일정 목록에 포함된 모든 영화들이 같은 영화가 아니라면 예외를 발생시킨다`() {
        assertThatThrownBy {
            MovieSchedule(
                MovieName("혼자사는남자"),
                movieScreenings =
                    listOf(
                        createOneHourMovieScreening(
                            Movie(
                                name = MovieName("혼자사는남자"),
                                runningTime = RunningTime(60),
                            ),
                        ),
                        createOneHourMovieScreening(
                            Movie(
                                name = MovieName("아이언맨"),
                                runningTime = RunningTime(60),
                            ),
                        ),
                    ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    private fun createOneHourMovieScreening(movie: Movie): MovieScreening =
        MovieScreening(
            movie = movie,
            screenTime =
                CinemaTimeRange(
                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 20)),
                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 13, 20)),
                ),
            seatGroup = SeatGroup(emptyList()),
        )
}
