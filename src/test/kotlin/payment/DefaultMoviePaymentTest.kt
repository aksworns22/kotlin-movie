package payment

import io.kotest.matchers.shouldBe
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.DefaultMoviePayment
import model.payment.Money
import model.payment.PayType
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
}
