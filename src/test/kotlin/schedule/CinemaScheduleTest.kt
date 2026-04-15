package schedule

import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.CinemaSchedule
import model.schedule.MovieSchedule
import model.schedule.MovieScreening
import model.schedule.ScreenSchedule
import model.seat.SeatGroup
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CinemaScheduleTest {
    @Test
    fun `상영관들의 모든 영화 상영 일정에 대해 제목에 해당하는 영화 목록을 반환한다`() {
        val cinemaTimeRange =
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2026, 4, 7, 21, 50)),
                CinemaTime(LocalDateTime.of(2026, 4, 10, 22, 50)),
            )

        val movieScreening =
            MovieScreening(
                movie =
                    Movie(
                        name = MovieName("혼자사는남자"),
                        runningTime = RunningTime(60),
                    ),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 16, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 17, 0)),
                    ),
                seatGroup = SeatGroup(emptyList()),
            )

        assertThat(
            CinemaSchedule(
                screenSchedules =
                    listOf(
                        ScreenSchedule(
                            screenId = "1",
                            servicePeriod = cinemaTimeRange,
                            movieScreenings = listOf(movieScreening),
                        ),
                        ScreenSchedule(
                            screenId = "2",
                            servicePeriod = cinemaTimeRange,
                            movieScreenings = listOf(movieScreening),
                        ),
                    ),
            )[MovieName("혼자사는남자")],
        ).isEqualTo(MovieSchedule(MovieName("혼자사는남자"), listOf(movieScreening, movieScreening)))
    }

    @Test
    fun `동일한 ScreenSchedule이 들어오면 예외를 반환한다`() {
        val cinemaTimeRange =
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2026, 4, 7, 21, 50)),
                CinemaTime(LocalDateTime.of(2026, 4, 10, 22, 50)),
            )

        assertThatThrownBy {
            CinemaSchedule(
                screenSchedules =
                    listOf(
                        ScreenSchedule(
                            screenId = "1",
                            servicePeriod = cinemaTimeRange,
                            movieScreenings = emptyList(),
                        ),
                        ScreenSchedule(
                            screenId = "1",
                            servicePeriod = cinemaTimeRange,
                            movieScreenings = emptyList(),
                        ),
                    ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
