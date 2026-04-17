package server

import MovieRepository
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.DefaultMoviePayment
import model.payment.Money
import model.payment.PayType
import model.payment.Point
import model.reservation.MovieReservationGroup
import model.schedule.MovieScreening
import model.seat.*
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

data class MovieDto(
    val id: Int,
    val title: String,
    val runningTimeMinutes: Int,
    val screenings: List<MovieScreeningDto>,
)

data class MovieScreeningDto(
    val id: Int,
    val screenId: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
)

data class ReservationRequest(
    val reservations: List<ReservationItem>,
    val usedPoints: Int,
    val paymentMethod: String,
)

data class ReservationItem(
    val screeningId: Int,
    val seats: List<String>,
)

data class ReservationResponse(
    val reservationId: Int,
    val reservations: List<ReservationItem>,
    val usedPoints: Int,
    val paymentMethod: String,
    val totalPrice: Int,
)

@SpringBootApplication
class Application

@RestController
class MovieScreeningController {
    val movieRepository: MovieRepository = MovieRepository(path = "~/test")

    @GetMapping("/api/movies")
    fun getMovieScreenings(): List<MovieDto> {
        val movieScreenings = movieRepository.getAllMovieScreenings()

        val grouped = movieScreenings.groupBy { it.movie.getName() }

        return grouped.map { (title, screeningsList) ->
            val first = screeningsList.first()
            val movieId =
                movieRepository.getMovieId(title)
                    ?: throw ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "영화를 찾을 수 없습니다: $title",
                    )
            MovieDto(
                id = movieId,
                title = title,
                runningTimeMinutes = first.movie.runningTime.getMinutes(),
                screenings =
                    screeningsList.map {
                        MovieScreeningDto(
                            id =
                                movieRepository.getMovieScreeningId(movieId, it.getMovieStartTime())
                                    ?: throw ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "상영 정보를 찾을 수 없습니다: $title",
                                    ),
                            screenId = it.screenId,
                            startAt = it.getMovieStartTime(),
                            endAt = it.getMovieStartTime().plusMinutes(it.movie.runningTime.getMinutes().toLong()),
                        )
                    },
            )
        }
    }

    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(@RequestBody request: ReservationRequest): ReservationResponse {
        println("Received reservation request: $request")
        var movieReservationGroup = MovieReservationGroup(emptySet())
        val screeningSeatMap = mutableMapOf<Int, List<String>>()

        try {
            for (res in request.reservations) {
                val movieScreening = movieRepository.getMovieScreeningById(res.screeningId)
                    ?: run {
                        println("Screening not found for ID: ${res.screeningId}")
                        throw ResponseStatusException(HttpStatus.NOT_FOUND, "상영 정보를 찾을 수 없습니다: ${res.screeningId}")
                    }

                for (seatName in res.seats) {
                    if (movieRepository.isReservedSeatById(res.screeningId, seatName)) {
                        println("Seat already reserved: $seatName for screening: ${res.screeningId}")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약된 좌석입니다: $seatName")
                    }

                    val seatPosition = try {
                        SeatPosition(
                            row = SeatRow(seatName.substring(0, 1)),
                            column = SeatColumn(seatName.substring(1).toInt())
                        )
                    } catch (e: Exception) {
                        println("Invalid seat format: $seatName")
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 좌석 형식입니다: $seatName")
                    }

                    movieReservationGroup = movieReservationGroup.reserveSeat(movieScreening, seatPosition)
                }
                screeningSeatMap[res.screeningId] = res.seats
            }

            val payment = DefaultMoviePayment(
                reservations = movieReservationGroup,
                payType = try { PayType.valueOf(request.paymentMethod) } catch(e: Exception) {
                    println("Invalid payment method: ${request.paymentMethod}")
                    throw ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 결제 수단입니다: ${request.paymentMethod}")
                },
                point = Point(request.usedPoints)
            )
            val paymentResult = payment.calculate()

            val reservationId = movieRepository.insertMovieReservationBatch(screeningSeatMap)
            println("Successfully created reservation: $reservationId")

            return ReservationResponse(
                reservationId = reservationId,
                reservations = request.reservations,
                usedPoints = request.usedPoints,
                paymentMethod = request.paymentMethod,
                totalPrice = paymentResult.finalPrice.toInt()
            )
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    companion object {
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
