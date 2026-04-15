package model.payment

import model.reservation.MovieSeatSelection

interface MovieDiscountable {
    fun getDiscountAmount(movieSeatSelection: MovieSeatSelection): Money
}

class SequentialMovieDiscount(
    discountableGroup: List<MovieDiscountable>,
) {
    private val discountableGroup: List<MovieDiscountable> = discountableGroup.toList()

    fun getDiscountedPrice(movieSeatSelection: MovieSeatSelection): Money {
        val originalPrice = movieSeatSelection.price
        return discountableGroup.fold(originalPrice) { nextPrice, movieDiscountable ->
            nextPrice.minusWithMinimum(
                money = movieDiscountable.getDiscountAmount(movieSeatSelection),
                minimum = Money(0),
            )
        }
    }
}

class EarlyMorningDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieSeatSelection: MovieSeatSelection): Money {
        if (!movieSeatSelection.isBeforeScreeningStartHour(11)) {
            return Money(0)
        }
        return Money(2000)
    }
}

class LateNightDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieSeatSelection: MovieSeatSelection): Money {
        if (movieSeatSelection.isBeforeScreeningStartHour(20)) {
            return Money(0)
        }
        return Money(2000)
    }
}

class MovieDayDiscount : MovieDiscountable {
    override fun getDiscountAmount(movieSeatSelection: MovieSeatSelection): Money {
        val originalPrice = movieSeatSelection.price
        val discountDays = setOf(10, 20, 30)
        if (discountDays.any { movieSeatSelection.isSameScreeningDay(it) }) {
            return originalPrice applyRate 0.1
        }
        return Money(0)
    }
}
