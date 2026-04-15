package model.seat

import model.payment.Money

data class Seat(
    private val position: SeatPosition,
    private val grade: SeatGrade,
) {
    val price: Money get() = grade.price

    fun isEqual(position: SeatPosition): Boolean = this.position == position
}
