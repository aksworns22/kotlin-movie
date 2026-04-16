package model.seat

import model.payment.Money

data class Seat(
    private val position: SeatPosition,
    val grade: SeatGrade,
) {
    val price: Money get() = grade.price

    fun isEqual(position: SeatPosition): Boolean = this.position == position

    fun getName(): String = "${position.getName()}:${grade.name}"
}
