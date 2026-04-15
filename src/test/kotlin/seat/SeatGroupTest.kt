package seat

import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.seat.SeatRow
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class SeatGroupTest {
    private val seatPositionA1 = SeatPosition(row = SeatRow("A"), column = SeatColumn(1))

    @Test
    fun `존재하는 좌석을 요청하면 해당 좌석을 반환한다`() {
        val seats =
            listOf(
                Seat(
                    position = seatPositionA1,
                    grade = SeatGrade.A,
                ),
            )
        assertThat(SeatGroup(seats = seats)[seatPositionA1])
            .isEqualTo(
                Seat(
                    position = seatPositionA1,
                    grade = SeatGrade.A,
                ),
            )
    }

    @Test
    fun `중복된 좌석이 존재하면 예외를 던진다`() {
        val seats =
            listOf(
                Seat(
                    position = seatPositionA1,
                    grade = SeatGrade.A,
                ),
                Seat(
                    position = seatPositionA1,
                    grade = SeatGrade.A,
                ),
            )
        assertThatThrownBy {
            SeatGroup(seats = seats)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
