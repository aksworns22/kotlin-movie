package seat

import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatPosition
import model.seat.SeatRow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SeatTest {
    @Test
    fun `좌석 번호가 다르다면 다른 좌석으로 판단한다`() {
        assertThat(
            creatSeat(row = "A", column = 4, grade = SeatGrade.A),
        ).isNotEqualTo(
            creatSeat(row = "B", column = 4, grade = SeatGrade.A),
        )
    }

    @Test
    fun `좌석 등급이 다르다면 다른 좌석으로 판단한다`() {
        assertThat(
            creatSeat(row = "A", column = 3, grade = SeatGrade.A),
        ).isNotEqualTo(
            creatSeat(row = "A", column = 3, grade = SeatGrade.S),
        )
    }

    @Test
    fun `좌석 등급과 좌석번호가 같다면 동일한 좌석으로 판단한다`() {
        assertThat(
            creatSeat(row = "A", column = 3, grade = SeatGrade.S),
        ).isEqualTo(
            creatSeat(row = "A", column = 3, grade = SeatGrade.S),
        )
    }

    private fun creatSeat(
        row: String,
        column: Int,
        grade: SeatGrade,
    ): Seat =
        Seat(
            position = SeatPosition(SeatRow(row = row), SeatColumn(column = column)),
            grade = grade,
        )
}
