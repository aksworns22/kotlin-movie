package schedule

import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieScreening
import model.seat.SeatGroup
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments
import java.time.LocalDateTime

class MovieScreeningTest {
    @Test
    fun `영화, 상영관, 기간이 같은 일정은 동일한 일정으로 판단한다`() {
        assertThat(
            MovieScreening(
                movie = oneHourMovie,
                screenTime = oneHourScreenTime,
                seatGroup = SeatGroup(emptyList()),
            ),
        ).isEqualTo(
            MovieScreening(
                movie = oneHourMovie,
                screenTime = oneHourScreenTime,
                seatGroup = SeatGroup(emptyList()),
            ),
        )
    }

    @Test
    fun `배정된 시간의 길이가 영화의 러닝타임과 일치하지 않으면 에외를 발생시킨다`() {
        assertThatThrownBy {
            MovieScreening(
                movie = oneHourMovie,
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 16, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 18, 0)),
                    ),
                seatGroup = SeatGroup(emptyList()),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `배정된 시간의 길이가 영화의 러닝타임과 일치하면 예외를 발생시키지 않는다`() {
        assertThatCode {
            MovieScreening(
                movie = oneHourMovie,
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 16, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 17, 0)),
                    ),
                seatGroup = SeatGroup(emptyList()),
            )
        }.doesNotThrowAnyException()
    }

    companion object {
        private val oneHourMovie =
            Movie(
                name = MovieName("혼자사는남자1"),
                runningTime = RunningTime(minute = 60),
            )
        private val twoHourMovie =
            Movie(
                name = MovieName("혼자사는남자2"),
                runningTime = RunningTime(minute = 120),
            )

        private val twoHourScreenTime =
            CinemaTimeRange(
                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 0)),
                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 0)),
            )
        private val oneHourScreenTime =
            CinemaTimeRange(
                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 0)),
            )

        @JvmStatic
        fun differentScheduledScreens(): List<Arguments> =
            listOf(
                Arguments.of(
                    listOf(oneHourMovie, twoHourMovie),
                    listOf(oneHourScreenTime, twoHourScreenTime),
                ),
                Arguments.of(
                    listOf(oneHourMovie, oneHourMovie),
                    listOf(oneHourScreenTime, oneHourScreenTime),
                ),
            )
    }
}
