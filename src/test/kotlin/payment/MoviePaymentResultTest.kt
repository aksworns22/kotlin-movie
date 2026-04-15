package payment

import io.kotest.matchers.shouldBe
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.DefaultMoviePayment
import model.payment.EarlyMorningDiscount
import model.payment.LateNightDiscount
import model.payment.Money
import model.payment.MovieDayDiscount
import model.payment.PayType
import model.payment.PayTypeDiscount
import model.payment.Point
import model.payment.PointDiscount
import model.reservation.MovieReservationGroup
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
import org.junit.jupiter.params.provider.CsvSource
import java.time.LocalDateTime

class MoviePaymentResultTest {
    @Test
    fun `좌석 등급과 예약한 좌석 수에 따라서 가격이 계산된다`() {
        val reservations =
            MovieReservationGroup(
                setOf(
                    MovieSeatSelection(
                        movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 10, 10, 30)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 10, 11, 30)),
                            ),
                        seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
                    ),
                    MovieSeatSelection(
                        movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                        screenTime =
                            CinemaTimeRange(
                                start = CinemaTime(LocalDateTime.of(2026, 4, 11, 10, 30)),
                                end = CinemaTime(LocalDateTime.of(2026, 4, 11, 11, 30)),
                            ),
                        seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
                    ),
                ),
            )
        DefaultMoviePayment(
            reservations = reservations,
            point = Point(0),
            payType = PayType.CREDIT_CARD,
        ).calculate().totalPrice shouldBe Money(30_000)
    }

    @Test
    fun `무비데이(매월 10일, 20일, 30일)에 상영되는 영화는 10% 할인된다`() {
        MovieDayDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 10, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 11, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe (SeatGrade.A.price applyRate 0.1)
    }

    @Test
    fun `오전 11시까지 시작하는 상영은 2,000원이 할인된다`() {
        EarlyMorningDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 10, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 11, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(2_000)
    }

    @Test
    fun `오후 8시부터 시작하는 상영은 2,000원이 할인된다`() {
        LateNightDiscount().getDiscountAmount(
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 12, 20, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 21, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            ),
        ) shouldBe Money(2_000)
    }

    @Test
    fun `포인트를 사용하면 예매 금액에서 차감된다`() {
        PointDiscount(
            point = Point(1000),
        ).applyDiscount(Money(5000)) shouldBe Money(4000)
    }

    @ParameterizedTest
    @CsvSource(
        "CREDIT_CARD, 14_250",
        "CASH, 14_700",
    )
    fun `결제 방식에 따라 결제 금액에서 일정 비율 할인된다`(
        payType: PayType,
        finalPrice: Int,
    ) {
        PayTypeDiscount(
            payType = payType,
        ).applyDiscount(Money(15_000)) shouldBe Money(finalPrice)
    }
}
