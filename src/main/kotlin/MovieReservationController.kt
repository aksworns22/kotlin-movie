import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.reservation.MovieReservationGroup
import model.schedule.CinemaSchedule
import model.schedule.MovieSchedule
import model.schedule.MovieScreening
import model.schedule.ScreenSchedule
import model.seat.Seat
import model.seat.SeatColumn
import model.seat.SeatGrade
import model.seat.SeatGroup
import model.seat.SeatPosition
import model.seat.SeatRow
import model.time.CinemaTime
import model.time.CinemaTimeRange
import view.InputView
import view.MovieReservationResultDto
import view.OutputView
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import kotlin.collections.component1
import kotlin.collections.component2

class MovieReservationController(
    private val movieRepository: MovieRepository,
    serviceTime: CinemaTimeRange,
) {
    private val cinemaSchedule =

        CinemaSchedule(
            movieRepository.getAllMovieScreenings().groupBy { it.screenId }.map { (screenId, screenDtoGroup) ->
                ScreenSchedule(
                    screenId = screenId.toString(),
                    servicePeriod = serviceTime,
                    movieScreenings =
                        screenDtoGroup.map { dto ->
                            MovieScreening(
                                movie =
                                    Movie(
                                        MovieName(dto.title),
                                        RunningTime(dto.runningTime),
                                    ),
                                screenTime =
                                    CinemaTimeRange(
                                        start = CinemaTime(dto.startTime),
                                        end = CinemaTime(dto.startTime).plusMinutes(dto.runningTime),
                                    ),
                                seatGroup = fixedSeatGroup,
                            )
                        },
                )
            },
        )

    fun handleMovieReservations(
        initialMovieReservationGroup: MovieReservationGroup = MovieReservationGroup(emptySet()),
    ): MovieReservationGroup {
        var movieReservationGroup: MovieReservationGroup = initialMovieReservationGroup
        do {
            val movieScreening = getReservableMovieScreening(movieReservationGroup)
            movieReservationGroup =
                getMovieScreeningReservations(
                    movieScreening = movieScreening,
                    movieReservationGroup = movieReservationGroup,
                )
        } while (getAdditionalMovieReservation())
        return movieReservationGroup
    }

    private fun getReservableMovieScreening(movieReservationGroup: MovieReservationGroup): MovieScreening {
        while (true) {
            try {
                val allScreensMovieSchedule = getMovieScheduleByName()
                val movieSchedule = getMovieScheduleByDate(allScreensMovieSchedule)
                val movieScreening = getMovieScreeningByTime(movieReservationGroup, movieSchedule)
                return movieScreening
            } catch (err: IllegalStateException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getMovieScheduleByName(): MovieSchedule {
        while (true) {
            try {
                val movieScreening = cinemaSchedule[MovieName(InputView.getMovieName())]
                if (movieScreening.isEmpty()) throw IllegalArgumentException("해당하는 영화가 없습니다.")
                return movieScreening
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getMovieScheduleByDate(movieSchedule: MovieSchedule): MovieSchedule {
        while (true) {
            try {
                val screeningDate = CinemaTime(InputView.getScreeningDate())
                val movieSchedule = movieSchedule.getSameDayMovieSchedule(screeningDate)
                if (movieSchedule.isEmpty()) throw IllegalArgumentException("해당 요일에 영화가 없습니다.")
                return movieSchedule
            } catch (_: DateTimeParseException) {
                throw IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)")
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getMovieScreeningByTime(
        movieReservationGroup: MovieReservationGroup,
        movieSchedule: MovieSchedule,
    ): MovieScreening {
        while (true) {
            try {
                val sortedMovieTimeTable = movieSchedule.getAllMovieStartTime().sorted()
                val movieScreeningNumber = InputView.selectMovieScreening(sortedMovieTimeTable) - 1
                require(movieScreeningNumber in sortedMovieTimeTable.indices) { "잘못된 입력입니다" }
                val reserveTime = CinemaTime(sortedMovieTimeTable[movieScreeningNumber])
                val movieScreening = movieSchedule[reserveTime]
                if (!movieReservationGroup.isReservable(movieScreening)) {
                    OutputView.showErrorMessage("선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요.")
                    continue
                }
                if (!movieReservationGroup.hasAvailableSeat(movieScreening)) {
                    OutputView.showErrorMessage("모든 좌석이 매진입니다. 다른 시간을 선택해 주세요.")
                    continue
                }
                return movieScreening
            } catch (error: IllegalArgumentException) {
                OutputView.showErrorMessage(error.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getMovieScreeningReservations(
        movieScreening: MovieScreening,
        movieReservationGroup: MovieReservationGroup,
    ): MovieReservationGroup {
        while (true) {
            try {
                val seatNames = movieScreening.getAllSeatNames().groupBy { it.first() }
                val rawPositions = InputView.selectSeats(seatNames)
                val seatPositions =
                    rawPositions.map { (rawRow, rawColumn) ->
                        SeatPosition(
                            row = SeatRow(rawRow),
                            column = SeatColumn(rawColumn),
                        )
                    }
                val finalMovieReservationGroup =
                    seatPositions.fold(movieReservationGroup) { group, seatPosition ->
                        require(
                            !movieRepository.isReservedSeat(
                                movieScreening.info.split(":").first(),
                                LocalDateTime.parse(
                                    movieScreening.info.substringAfter(":"),
                                ),
                                seatPosition.getName().split(":").first(),
                            ),
                        ) {
                            "이미 예약된 좌석입니다. 다른 좌석을 선택해 주세요"
                        }
                        group.reserveSeat(
                            movieScreening = movieScreening,
                            seatPosition = seatPosition,
                        )
                    }
                val movieReservationResultDtoGroup =
                    (finalMovieReservationGroup - movieReservationGroup).map { movieSeatSelection ->
                        MovieReservationResultDto(
                            movieSeatSelection.movieName,
                            startTime = movieSeatSelection.startTime,
                            seatName = movieSeatSelection.seatName,
                        )
                    }
                OutputView.showMovieReservationResult(
                    initialMessage = "장바구니에 추가됨",
                    reservationDtoGroup = movieReservationResultDtoGroup,
                )
                return finalMovieReservationGroup
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getAdditionalMovieReservation(): Boolean {
        while (true) {
            try {
                return InputView.selectAdditionalMovieReservation()
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    companion object {
        private val fixedSeatGroup =
            SeatGroup(
                seats =
                    listOf(
                        Seat(
                            position = SeatPosition(SeatRow("A"), SeatColumn(1)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("A"), SeatColumn(2)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("A"), SeatColumn(3)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("A"), SeatColumn(4)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("B"), SeatColumn(1)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("B"), SeatColumn(2)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("B"), SeatColumn(3)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("B"), SeatColumn(4)),
                            grade = SeatGrade.B,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("C"), SeatColumn(1)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("C"), SeatColumn(2)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("C"), SeatColumn(3)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("C"), SeatColumn(4)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("D"), SeatColumn(1)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("D"), SeatColumn(2)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("D"), SeatColumn(3)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("D"), SeatColumn(4)),
                            grade = SeatGrade.S,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("E"), SeatColumn(1)),
                            grade = SeatGrade.A,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("E"), SeatColumn(2)),
                            grade = SeatGrade.A,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("E"), SeatColumn(3)),
                            grade = SeatGrade.A,
                        ),
                        Seat(
                            position = SeatPosition(SeatRow("E"), SeatColumn(4)),
                            grade = SeatGrade.A,
                        ),
                    ),
            )
    }
}
