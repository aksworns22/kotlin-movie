package server

import MovieRepository
import model.CinemaConstants
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.payment.DefaultMoviePayment
import model.payment.PayType
import model.payment.Point
import model.reservation.MovieReservationGroup
import model.schedule.MovieScreening
import model.seat.SeatColumn
import model.seat.SeatPosition
import model.seat.SeatRow
import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
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
    val movieRepository: MovieRepository = MovieRepository(path = "~/movieeee")

    init {
        movieRepository.insertMovieScreenings(
            MovieScreening(
                screenId = 1,
                movie = Movie(MovieName("혼자사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 8, 10, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 8, 11, 0)),
                    ),
                seatGroup = CinemaConstants.fixedSeatGroup,
            ),
            MovieScreening(
                screenId = 2,
                movie = Movie(MovieName("아이언맨"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 9, 7, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 9, 8, 0)),
                    ),
                seatGroup = CinemaConstants.fixedSeatGroup,
            ),
            MovieScreening(
                screenId = 3,
                movie = Movie(MovieName("혼자사는남자"), RunningTime(60)),
                screenTime =
                    CinemaTimeRange(
                        start = CinemaTime(LocalDateTime.of(2026, 4, 10, 20, 0)),
                        end = CinemaTime(LocalDateTime.of(2026, 4, 10, 21, 0)),
                    ),
                seatGroup = CinemaConstants.fixedSeatGroup,
            ),
        )
    }

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
                            endAt =
                                it.getMovieStartTime().plusMinutes(
                                    it.movie.runningTime
                                        .getMinutes()
                                        .toLong(),
                                ),
                        )
                    },
            )
        }
    }

    @PostMapping("/api/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun createReservation(
        @RequestBody request: ReservationRequest,
    ): ReservationResponse {
        var movieReservationGroup = MovieReservationGroup(emptySet())
        val screeningSeatMap = mutableMapOf<Int, List<String>>()

        try {
            for (res in request.reservations) {
                val movieScreening =
                    movieRepository.getMovieScreeningById(res.screeningId)
                        ?: run {
                            throw ResponseStatusException(HttpStatus.NOT_FOUND, "상영 정보를 찾을 수 없습니다: ${res.screeningId}")
                        }

                for (seatName in res.seats) {
                    if (movieRepository.isReservedSeatById(res.screeningId, seatName)) {
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 예약된 좌석입니다: $seatName")
                    }

                    val seatPosition =
                        try {
                            SeatPosition(
                                row = SeatRow(seatName.substring(0, 1)),
                                column = SeatColumn(seatName.substring(1).toInt()),
                            )
                        } catch (e: Exception) {
                            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 좌석 형식입니다: $seatName")
                        }

                    movieReservationGroup = movieReservationGroup.reserveSeat(movieScreening, seatPosition)
                }
                screeningSeatMap[res.screeningId] = res.seats
            }

            val payment =
                DefaultMoviePayment(
                    reservations = movieReservationGroup,
                    payType =
                        try {
                            PayType.valueOf(request.paymentMethod)
                        } catch (e: Exception) {
                            throw ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "잘못된 결제 수단입니다: ${request.paymentMethod}",
                            )
                        },
                    point = Point(request.usedPoints),
                )
            val paymentResult = payment.calculate()

            val reservationId = movieRepository.insertMovieReservationBatch(screeningSeatMap)

            return ReservationResponse(
                reservationId = reservationId,
                reservations = request.reservations,
                usedPoints = request.usedPoints,
                paymentMethod = request.paymentMethod,
                totalPrice = paymentResult.finalPrice.toInt(),
            )
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
