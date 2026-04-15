package seat

import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeLessThan
import model.seat.SeatColumn
import model.seat.SeatPosition
import model.seat.SeatRow
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class SeatPositionTest {
    @ParameterizedTest
    @ValueSource(strings = ["F", "AB", "-1", " ", "0", "a", "f", "B "])
    fun `좌석의 행 문자가 F 이상이거나, 소문자로 입력되거나, 2글자 이상일 경우 예외를 던진다`(row: String) {
        assertThatThrownBy {
            SeatRow(row = row)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(strings = ["A", "B", "C", "D", "E"])
    fun `좌석의 행 문자가 A ~ E 범위이면 예외를 던지지 않는다`(row: String) {
        assertThatCode {
            SeatRow(row = row)
        }.doesNotThrowAnyException()
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 6, -1, 33])
    fun `좌석의 열 번호가 1 미만 4 초과일 경우 예외를 던진다`(column: Int) {
        assertThatThrownBy {
            SeatColumn(column = column)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4])
    fun `좌석의 열 번호가 1 이상 4 이하일 경우 예외를 던지지 않는다`(column: Int) {
        assertThatCode {
            SeatColumn(column = column)
        }.doesNotThrowAnyException()
    }

    @ParameterizedTest
    @CsvSource("A,1,B,1", "A,1,B,2", "A,2,B,1")
    fun `좌석의 열 번호가 알파벳순으로 앞서면 더 앞선 좌석으로 판단한다`(
        seatRow1: String,
        seatColumn1: Int,
        seatRow2: String,
        seatColumn2: Int,
    ) {
        SeatPosition(SeatRow(seatRow1), SeatColumn(seatColumn1)).compareTo(
            SeatPosition(
                SeatRow(
                    seatRow2,
                ),
                SeatColumn(seatColumn2),
            ),
        ) shouldBeLessThan 0
    }

    @Test
    fun `좌석의 행 번호가 같다면 열 번호가 작은 경우 더 앞선 좌석으로 판단한다`() {
        SeatPosition(SeatRow("A"), SeatColumn(1)).compareTo(
            SeatPosition(
                SeatRow(
                    "A",
                ),
                SeatColumn(2),
            ),
        ) shouldBeLessThan 0
    }
}
