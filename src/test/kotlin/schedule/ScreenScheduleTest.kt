package schedule

import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieSchedule
import model.schedule.MovieScreening
import model.schedule.ScreenSchedule
import model.seat.SeatGroup
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime

class ScreenScheduleTest {
    private val idOne = "1"
    private val idTwo = "2"

    @Test
    fun `운영 기간이 같더라도 id가 다르면 다른 상영관 일정으로 판단한다`() {
        val cinemaTimeRange =
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2026, 4, 7, 21, 50)),
                CinemaTime(LocalDateTime.of(2026, 4, 7, 22, 50)),
            )
        assertThat(
            ScreenSchedule(
                screenId = idOne,
                servicePeriod = cinemaTimeRange,
                movieScreenings = emptyList(),
            ),
        ).isNotEqualTo(
            ScreenSchedule(
                screenId = idTwo,
                servicePeriod = cinemaTimeRange,
                movieScreenings = emptyList(),
            ),
        )
    }

    @Test
    fun `운영 기간이 다르더라도 id가 같으면 같은 상영관 일정으로 판단한다`() {
        assertThat(
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        CinemaTime(LocalDateTime.of(2026, 4, 7, 21, 50)),
                        CinemaTime(LocalDateTime.of(2026, 4, 7, 22, 50)),
                    ),
                movieScreenings = emptyList(),
            ),
        ).isEqualTo(
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        CinemaTime(LocalDateTime.of(1999, 4, 7, 21, 50)),
                        CinemaTime(LocalDateTime.of(2026, 4, 7, 22, 50)),
                    ),
                movieScreenings = emptyList(),
            ),
        )
    }

    @Test
    fun `상영관의 운영시간보다 영화 상영 일정이 일찍 배정되면 예외를 발생시킨다`() {
        assertThatThrownBy {
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 7, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 8, 0)),
                    ),
                movieScreenings =
                    listOf(
                        MovieScreening(
                            screenId = 1,
                            movie =
                                Movie(
                                    name = MovieName("혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 6, 0)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 7, 0)),
                                ),
                            seatGroup = SeatGroup(emptyList()),
                        ),
                    ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `상영관의 운영시간보다 영화 상영 일정이 늦게 배정되면 예외를 발생시킨다`() {
        assertThatThrownBy {
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 7, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 8, 0)),
                    ),
                movieScreenings =
                    listOf(
                        MovieScreening(
                            screenId = 1,
                            movie =
                                Movie(
                                    name = MovieName("혼자사는남자"),
                                    runningTime = RunningTime(minute = 60),
                                ),
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 7, 30)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 8, 30)),
                                ),
                            seatGroup = SeatGroup(emptyList()),
                        ),
                    ),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `특정 영화를 요청받으면 해당하는 상영관의 영화 전체 일정을 반환한다`() {
        val movieOne =
            Movie(
                name = MovieName("혼자사는남자"),
                runningTime = RunningTime(10),
            )

        assertThat(
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        CinemaTime(LocalDateTime.of(1999, 4, 7, 21, 50)),
                        CinemaTime(LocalDateTime.of(2026, 4, 10, 22, 50)),
                    ),
                movieScreenings =
                    listOf(
                        MovieScreening(
                            screenId = 1,
                            movie = movieOne,
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 10)),
                                ),
                            seatGroup = SeatGroup(emptyList()),
                        ),
                    ),
            )[MovieName("혼자사는남자")],
        ).isEqualTo(
            MovieSchedule(
                MovieName("혼자사는남자"),
                listOf(
                    MovieScreening(
                        screenId = 1,
                        movie = movieOne,
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 10)),
                            ),
                        seatGroup = SeatGroup(emptyList()),
                    ),
                ),
            ),
        )
    }

    @ParameterizedTest
    @MethodSource("invalidMovieScreening")
    fun `동일한 상영관에서 상영 시간이 겹칠 경우 예외를 발생시킨다`(movieScreening: MovieScreening) {
        assertThatThrownBy {
            ScreenSchedule(
                screenId = idOne,
                servicePeriod =
                    CinemaTimeRange(
                        CinemaTime(LocalDateTime.of(1999, 4, 7, 21, 50)),
                        CinemaTime(LocalDateTime.of(2026, 4, 10, 22, 50)),
                    ),
                movieScreenings =
                    listOf(
                        movieScreening,
                        MovieScreening(
                            screenId = 1,
                            movie = movieOne,
                            screenTime =
                                CinemaTimeRange(
                                    start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                                    end = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 0)),
                                ),
                            seatGroup = SeatGroup(emptyList()),
                        ),
                    ),
            )
        }
    }

    companion object {
        private val movieOne =
            Movie(
                name = MovieName("혼자사는남자"),
                runningTime = RunningTime(60),
            )

        @JvmStatic
        fun invalidMovieScreening(): List<Arguments> =
            listOf(
                Arguments.of(
                    MovieScreening(
                        screenId = 1,
                        movie = movieOne,
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 0)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                            ),
                        seatGroup = SeatGroup(emptyList()),
                    ),
                ),
                Arguments.of(
                    MovieScreening(
                        screenId = 1,
                        movie = movieOne,
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 0)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 13, 0)),
                            ),
                        seatGroup = SeatGroup(emptyList()),
                    ),
                ),
                Arguments.of(
                    MovieScreening(
                        screenId = 1,
                        movie = movieOne,
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 30)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 12, 30)),
                            ),
                        seatGroup = SeatGroup(emptyList()),
                    ),
                ),
                Arguments.of(
                    MovieScreening(
                        screenId = 1,
                        movie = movieOne,
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 30)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 30)),
                            ),
                        seatGroup = SeatGroup(emptyList()),
                    ),
                ),
            )
    }
}
