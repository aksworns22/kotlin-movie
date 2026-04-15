@file:Suppress("ktlint")

package view
//
// import model.movie.MovieName
// import model.payment.PayType
// import model.payment.PayTypeDiscount
// import model.payment.Point
// import model.schedule.MovieSchedule
// import model.schedule.MovieScreening
// import model.seat.SeatColumn
// import model.seat.SeatPosition
// import model.seat.SeatRow
// import model.time.CinemaTime
// import java.time.LocalDate
// import java.time.format.DateTimeFormatter
//
// object InputView {
//    fun startMovieReservation(): Boolean {
//        println("영화 예매를 시작합니다. 새 예매를 생성하시겠습니까? (Y/N)")
//        val userInput = readln()
//        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
//        return userInput == "Y"
//    }
//
//    fun getMovieName(): MovieName {
//        println("예매할 영화 제목을 입력하세요:")
//        return MovieName(readln())
//    }
//
//    fun getScreeningDate(): CinemaTime {
//        println("날짜를 입력하세요 (YYYY-MM-DD):")
//        val date =
//            LocalDate.parse(
//                readln(),
//                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
//            )
//        return CinemaTime(date.atStartOfDay())
//    }
//
//    fun selectMovieScreening(movieSchedule: MovieSchedule): MovieScreening {
//        val userInput = readln()
//        require(userInput.all { it.isDigit() }) { "숫자만 가능합니다" }
//        val index = userInput.toInt() - 1
//        require(index in (0..<movieSchedule.size)) { "올바르지 않은 번호입니다." }
//        return movieSchedule.sortedBy { it.screeningDescription }[index]
//    }
//
//    fun selectSeats(): List<SeatPosition> {
//        println("예약할 좌석을 입력하세요 (A1, B2):")
//        val rawSeats = readln().split(",").map { it.trim() }
//        require(rawSeats.all { it.isNotEmpty() }) { "올바르지 않은 입력입니다." }
//        return rawSeats.map { rawSeatPosition ->
//            SeatPosition(
//                row = SeatRow(rawSeatPosition[0].toString()),
//                column =
//                    SeatColumn(
//                        rawSeatPosition[1].digitToIntOrNull()
//                            ?: throw IllegalArgumentException("행 번호는 숫자만 가능합니다"),
//                    ),
//            )
//        }
//    }
//
//    fun selectAdditionalMovieReservation(): Boolean {
//        println("다른 영화를 추가하시겠습니까? (Y/N)")
//        val userInput = readln()
//        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
//        return userInput == "Y"
//    }
//
//    fun getPoint(): Point {
//        println("사용할 포인트를 입력하세요 (없으면 0):")
//        return Point(readln().toInt())
//    }
//
//    fun getPayType(): PayType {
//        println("결제 수단을 선택하세요:")
//        println("${PayType.CREDIT_CARD.selectNumber}) 신용카드(${PayTypeDiscount.CREDIT_CARD_DISCOUNT_RATIO * 100}% 할인)")
//        println("${PayType.CASH.selectNumber}) 현금(${PayTypeDiscount.CASH_DISCOUNT_RATIO * 100}% 할인)")
//        val userInput = readln().toInt()
//        return PayType.entries.find { it.selectNumber == userInput }
//            ?: throw IllegalArgumentException("존재하지 않는 선택번호입니다")
//    }
//
//    fun getPaymentConfirm(): Boolean {
//        println("위 금액으로 결제하시겠습니까? (Y/N)")
//        val userInput = readln()
//        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
//        return userInput == "Y"
//    }
// }
//
// private val PayType.selectNumber: Int
//    get() =
//        when (this) {
//            PayType.CREDIT_CARD -> 1
//            PayType.CASH -> 2
//        }
