package model

import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.seat.SeatRow

object CinemaConstants {
    val fixedSeatGroup =
        SeatGroup(
            seats =
                listOf(
                    Seat(position = SeatPosition(SeatRow("A"), SeatColumn(1)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("A"), SeatColumn(2)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("A"), SeatColumn(3)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("A"), SeatColumn(4)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("B"), SeatColumn(1)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("B"), SeatColumn(2)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("B"), SeatColumn(3)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("B"), SeatColumn(4)), grade = SeatGrade.B),
                    Seat(position = SeatPosition(SeatRow("C"), SeatColumn(1)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("C"), SeatColumn(2)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("C"), SeatColumn(3)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("C"), SeatColumn(4)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("D"), SeatColumn(1)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("D"), SeatColumn(2)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("D"), SeatColumn(3)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("D"), SeatColumn(4)), grade = SeatGrade.S),
                    Seat(position = SeatPosition(SeatRow("E"), SeatColumn(1)), grade = SeatGrade.A),
                    Seat(position = SeatPosition(SeatRow("E"), SeatColumn(2)), grade = SeatGrade.A),
                    Seat(position = SeatPosition(SeatRow("E"), SeatColumn(3)), grade = SeatGrade.A),
                    Seat(position = SeatPosition(SeatRow("E"), SeatColumn(4)), grade = SeatGrade.A),
                ),
        )
}
