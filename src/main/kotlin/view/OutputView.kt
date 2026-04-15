package view

import java.time.format.DateTimeFormatter

object OutputView {
    fun showErrorMessage(errorMessage: String) {
        println(errorMessage)
    }

    fun showMovieReservationResult(
        initialMessage: String,
        reservationDtoGroup: List<MovieReservationResultDto>,
    ) {
        println(initialMessage)
        reservationDtoGroup
            .groupBy { it.movieName to it.startTime }
            .forEach { (key, results) ->
                val (movieName, startTime) = key
                val seats = results.joinToString(", ") { it.seatName.split(":").first() }
                println("- [$movieName] ${startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))} 좌석: $seats")
            }
    }

    fun printTotalPrice(finalPrice: Int) {
        println("가격 계산")
        print("최종 결제 금액: ")
        println("${"%,d".format(finalPrice)}원")
    }

    fun end() {
        println("감사합니다.")
    }
}
