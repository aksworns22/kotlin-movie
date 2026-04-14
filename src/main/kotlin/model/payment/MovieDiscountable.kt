package model.payment

import model.reservation.MovieReservationResult
import java.time.LocalTime

interface MovieDiscountable {
    fun getDiscountAmount(movieReservationResult: MovieReservationResult): Money
}

class SequentialMovieDiscount(
    discountableGroup: List<MovieDiscountable>,
) {
    private val discountableGroup: List<MovieDiscountable> = discountableGroup.toList()

    fun getDiscountedPrice(movieReservationResult: MovieReservationResult): Money {
        val originalPrice = movieReservationResult.price
        return discountableGroup.fold(originalPrice) { nextPrice, movieDiscountable ->
            nextPrice.minusWithMinimum(
                money = movieDiscountable.getDiscountAmount(movieReservationResult),
                minimum = Money(0),
            )
        }
    }
}

class EarlyMorningDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieReservationResult: MovieReservationResult): Money {
        val time = movieReservationResult.screenTime.start.toLocalTime()
        if (time.isAfter(LocalTime.of(11, 0))) {
            return Money(0)
        }
        return Money(2000)
    }
}

class LateNightDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieReservationResult: MovieReservationResult): Money {
        val time = movieReservationResult.screenTime.end.toLocalTime()
        if (time.isBefore(LocalTime.of(20, 0))) {
            return Money(0)
        }
        return Money(2000)
    }
}

class MovieDayDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieReservationResult: MovieReservationResult): Money {
        val originalPrice = movieReservationResult.price
        val discountDays = setOf(10, 20, 30)
        if (discountDays.contains(movieReservationResult.screenTime.start.dayOfMonth)) {
            return originalPrice applyRate 0.1
        }
        return Money(0)
    }
}
