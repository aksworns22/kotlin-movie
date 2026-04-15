package payment

import io.kotest.matchers.shouldBe
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.DefaultMoviePayment
import model.payment.Money
import model.payment.PayType
import model.payment.PayTypeDiscount
import model.payment.Point
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
import java.time.LocalDateTime

class DefaultMoviePaymentTest {
    @Test
    fun `좌석 등급과 예약한 좌석 수에 따라서 총 가격이 계산된다`() {
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
    fun `4월 10일 13시 영화를 0포인트와 현금으로 구매하면 무비데이 할인과 현금 할인 적용된다`() {
        // given
        val movieSeatSelection =
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 13, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 14, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            )
        val reservations =
            MovieReservationGroup(
                setOf(movieSeatSelection),
            )
        val finalPayAmount =
            (movieSeatSelection.price applyRate 0.9) applyRate (1 - PayTypeDiscount.CASH_DISCOUNT_RATIO)

        // when
        val moviePaymentResult =
            DefaultMoviePayment(
                reservations = reservations,
                point = Point(0),
                payType = PayType.CASH,
            ).calculate()

        // then
        moviePaymentResult.finalPrice shouldBe finalPayAmount
    }

    @Test
    fun `4월 10일 7시 영화를 5000포인트와 현금으로 구매하면 무비데이 할인, 조조할인, 현금 할인 적용된다`() {
        // given
        val movieSeatSelection =
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 7, 30)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 8, 30)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            )
        val reservations =
            MovieReservationGroup(
                setOf(movieSeatSelection),
            )
        val usedPoint = Point(5_000)
        val appliedMovieDayDiscount = movieSeatSelection.price applyRate 0.9
        val appliedEarlyMorningDiscount = appliedMovieDayDiscount - Money(2_000)
        val finalPayAmount =
            (appliedEarlyMorningDiscount - usedPoint.toMoney()) applyRate (1 - PayTypeDiscount.CASH_DISCOUNT_RATIO)

        // when
        val moviePaymentResult =
            DefaultMoviePayment(
                reservations = reservations,
                point = usedPoint,
                payType = PayType.CASH,
            ).calculate()

        // then
        moviePaymentResult.finalPrice shouldBe finalPayAmount
    }

    @Test
    fun `4월 10일 23시 영화를 0포인트와 신용카드로 구매하면 무비데이 할인, 심야할인, 카드 할인 적용된다`() {
        // given
        val movieSeatSelection =
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 23, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 11, 0, 0)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            )
        val reservations =
            MovieReservationGroup(
                setOf(movieSeatSelection),
            )
        val appliedMovieDayDiscount = movieSeatSelection.price applyRate 0.9
        val appliedLateNightDiscount = appliedMovieDayDiscount - Money(2_000)
        val finalPayAmount =
            (appliedLateNightDiscount - Money(0)) applyRate (1 - PayTypeDiscount.CREDIT_CARD_DISCOUNT_RATIO)

        // when
        val moviePaymentResult =
            DefaultMoviePayment(
                reservations = reservations,
                point = Point(0),
                payType = PayType.CREDIT_CARD,
            ).calculate()

        // then
        moviePaymentResult.finalPrice shouldBe finalPayAmount
    }

    @Test
    fun `4월 11일 23시 영화를 500포인트와 신용카드로 구매하면 심야할인과 카드 할인 적용된다`() {
        // given
        val movieSeatSelection =
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 11, 23, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 12, 0, 0)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            )
        val reservations =
            MovieReservationGroup(
                setOf(movieSeatSelection),
            )
        val usedPoint = Point(500)
        val appliedLateNightDiscount = movieSeatSelection.price - Money(2_000)
        val finalPayAmount =
            (appliedLateNightDiscount - usedPoint.toMoney()) applyRate (1 - PayTypeDiscount.CREDIT_CARD_DISCOUNT_RATIO)

        // when
        val moviePaymentResult =
            DefaultMoviePayment(
                reservations = reservations,
                point = usedPoint,
                payType = PayType.CREDIT_CARD,
            ).calculate()

        // then
        moviePaymentResult.finalPrice shouldBe finalPayAmount
    }

    @Test
    fun `무비데이(10일)에 조조(10시)로 A석을 예매하고, 1_000포인트를 사용한 뒤, 신용카드로 결제하면 9_975원이된다`() {
        // given
        val movieSeatSelection =
            MovieSeatSelection(
                movie = Movie(MovieName("옥탑방에사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 10, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 11, 0)),
                    ),
                seat = Seat(SeatPosition(SeatRow("A"), SeatColumn(1)), SeatGrade.A),
            )
        val reservations =
            MovieReservationGroup(
                setOf(movieSeatSelection),
            )
        val usedPoint = Point(1_000)
        // when
        val moviePaymentResult =
            DefaultMoviePayment(
                reservations = reservations,
                point = usedPoint,
                payType = PayType.CREDIT_CARD,
            ).calculate()

        // then
        moviePaymentResult.finalPrice shouldBe Money(9_975)
    }
}
