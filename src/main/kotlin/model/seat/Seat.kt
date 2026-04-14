package model.seat

import model.payment.Money

data class Seat(
    val position: SeatPosition,
    val grade: SeatGrade,
) {
    val price: Money get() = grade.price

    fun isEqual(position: SeatPosition): Boolean = this.position == position
}
