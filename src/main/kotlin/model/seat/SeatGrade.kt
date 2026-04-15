package model.seat

import model.payment.Money

enum class SeatGrade(
    val price: Money,
) {
    S(price = Money(18_000)),
    A(price = Money(15_000)),
    B(price = Money(12_000)),
}
