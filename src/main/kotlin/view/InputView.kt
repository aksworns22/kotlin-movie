package view

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object InputView {
    fun startMovieReservation(): Boolean {
        println("영화 예매를 시작합니다. 새 예매를 생성하시겠습니까? (Y/N)")
        val userInput = readln()
        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
        return userInput == "Y"
    }

    fun getMovieName(): String {
        println("예매할 영화 제목을 입력하세요:")
        return readln()
    }

    fun getScreeningDate(): LocalDateTime {
        println("날짜를 입력하세요 (YYYY-MM-DD):")
        val date =
            LocalDate.parse(
                readln(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            )
        return date.atStartOfDay()
    }

    fun selectMovieScreening(movieTimeTable: List<LocalDateTime>): Int {
        movieTimeTable.forEachIndexed { index, time ->
            println("[${index + 1}] ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        }
        return readln().toIntOrNull() ?: throw IllegalArgumentException("숫자만 입력가능합니다")
    }

    fun selectSeats(seatNames: Map<Char, List<String>>): List<Pair<String, Int>> {
        println("좌석 배치도")
        print("   ")
        seatNames.keys.sorted().forEachIndexed { index, row ->
            val sortedSameRowSeats = seatNames.getValue(row)
            if (index == 0) {
                (1..sortedSameRowSeats.size).forEach { number ->
                    print(" $number ")
                }
                println()
            }
            print(" $row ")
            sortedSameRowSeats.forEach { seatName ->
                val grade = seatName.split(":").last()
                print("[$grade]")
            }
            println()
        }
        println("예약할 좌석을 입력하세요 (A1, B2):")
        val rawSeats = readln().split(",").map { it.trim() }
        require(rawSeats.all { it.isNotEmpty() && it.length == 2 }) { "올바르지 않은 입력입니다." }
        return rawSeats.map { rawSeatPosition ->
            rawSeatPosition[0].toString() to (
                rawSeatPosition[1].digitToIntOrNull()
                    ?: throw IllegalArgumentException("숫자만 입력가능합니다")
            )
        }
    }

    fun selectAdditionalMovieReservation(): Boolean {
        println("다른 영화를 추가하시겠습니까? (Y/N)")
        val userInput = readln()
        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
        return userInput == "Y"
    }

    fun getPointNumber(): Int {
        println("사용할 포인트를 입력하세요 (없으면 0):")
        return readln().toIntOrNull() ?: throw IllegalArgumentException("숫자만 입력가능합니다")
    }

    fun getPayTypeNumber(): Int {
        println("결제 수단을 선택하세요:")
        println("1) 신용카드(5% 할인)")
        println("2) 현금(2% 할인)")
        return readln().toIntOrNull() ?: throw IllegalArgumentException("숫자만 입력가능합니다")
    }

    fun getPaymentConfirm(): Boolean {
        println("위 금액으로 결제하시겠습니까? (Y/N)")
        val userInput = readln()
        if (userInput !in listOf("Y", "N")) throw IllegalArgumentException("올바르지 않은 입력입니다")
        return userInput == "Y"
    }
}
