package payment

import io.kotest.matchers.shouldBe
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.EarlyMorningDiscount
import model.payment.LateNightDiscount
import model.payment.Money
import model.payment.MovieDayDiscount
import model.reservation.MovieSeatSelection
import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatPosition
import model.seat.SeatRow
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDateTime

class MovieDiscountableTest {
    @ParameterizedTest
    @ValueSource(ints = [10, 20, 30])
    fun `무비데이(매월 10일, 20일, 30일)에 상영되는 영화는 10% 할인된다`(dayOfMonth: Int) {
        MovieDayDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, dayOfMonth, 10, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, dayOfMonth, 11, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe (SeatGrade.A.price applyRate 0.1)
    }

    @Test
    fun `2026년 10월 11일은 무비데이 할인이 적용되지 않는다`() {
        MovieDayDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 11, 10, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 11, 11, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(0)
    }

    @ParameterizedTest
    @MethodSource("earlyMorningScreenTimeProvider")
    fun `오전 11시까지 시작하는 상영은 2,000원이 할인되는 조조할인이 적용된다`(screenTime: CinemaTimeRange) {
        EarlyMorningDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime = screenTime,
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(2_000)
    }

    @ParameterizedTest
    @MethodSource("lateLightScreenTimeProvider")
    fun `오전 11시에 이후의 상영은 조조할인이 적용되지 않는다`(screenTime: CinemaTimeRange) {
        EarlyMorningDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime = screenTime,
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(0)
    }

    @ParameterizedTest
    @MethodSource("lateLightScreenTimeProvider")
    fun `오후 8시부터 시작하는 상영은 2,000원이 할인된다`(screenTime: CinemaTimeRange) {
        LateNightDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime = screenTime,
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(2_000)
    }

    @ParameterizedTest
    @MethodSource("earlyMorningScreenTimeProvider")
    fun `오후 8시부 이전의 상영은 심야할인이 적용되지 않는다`(screenTime: CinemaTimeRange) {
        LateNightDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime = screenTime,
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(0)
    }

    companion object {
        @JvmStatic
        fun earlyMorningScreenTimeProvider(): List<Arguments> =
            listOf(
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 10, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 11, 30)),
                    ),
                ),
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 10, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 11, 0)),
                    ),
                ),
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 0, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 13, 1, 0)),
                    ),
                ),
            )

        @JvmStatic
        fun lateLightScreenTimeProvider(): List<Arguments> =
            listOf(
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 20, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 21, 0)),
                    ),
                ),
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 20, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 21, 30)),
                    ),
                ),
                Arguments.of(
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 23, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 13, 0, 30)),
                    ),
                ),
            )
    }
}
