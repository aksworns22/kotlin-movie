package model.payment

import model.reservation.MovieReservationGroup

class DefaultMoviePayment(
    private val reservations: MovieReservationGroup,
    payType: PayType,
    point: Point,
) {
    private val sequentialMovieDiscount =
        SequentialMovieDiscount(
            listOf(
                MovieDayDiscount(),
                EarlyMorningDiscount(),
                LateNightDiscount(),
            ),
        )

    private val sequentialPurchaseDiscount =
        SequentialPurchaseDiscount(
            listOf(
                PointDiscount(point),
                PayTypeDiscount(payType),
            ),
        )

    fun calculate(): MoviePaymentResult {
        val totalPrice =
            reservations.fold(Money(0)) { nextPrice, reservation ->
                nextPrice + reservation.price
            }
        val movieDiscountedPrice =
            reservations.fold(Money(0)) { nextPrice, reservation ->
                nextPrice + sequentialMovieDiscount.getDiscountedPrice(reservation)
            }
        return MoviePaymentResult(
            totalPrice = totalPrice,
            finalPrice = sequentialPurchaseDiscount.getDiscountedPrice(movieDiscountedPrice),
        )
    }
}
