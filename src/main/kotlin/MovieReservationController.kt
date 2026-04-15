import model.movie.MovieName
import model.reservation.MovieReservationGroup
import model.schedule.CinemaSchedule
import model.schedule.MovieSchedule
import model.schedule.MovieScreening
import model.seat.SeatColumn
import model.seat.SeatPosition
import model.seat.SeatRow
import model.time.CinemaTime
import view.InputView
import view.MovieReservationResultDto
import view.OutputView
import java.time.format.DateTimeParseException

class MovieReservationController(
    private val cinemaSchedule: CinemaSchedule,
    private var movieReservationGroup: MovieReservationGroup =
        MovieReservationGroup(
            emptySet(),
        ),
) {
    fun handleMovieReservations(): MovieReservationGroup {
        do {
            val movieScreening = getReservableMovieScreening()
            movieReservationGroup =
                getMovieScreeningReservations(
                    movieScreening = movieScreening,
                    movieReservationGroup = movieReservationGroup,
                )
        } while (getAdditionalMovieReservation())
        return movieReservationGroup
    }

    private fun getReservableMovieScreening(): MovieScreening {
        while (true) {
            try {
                val allScreensMovieSchedule = getMovieScheduleByName()
                val movieSchedule = getMovieScheduleByDate(allScreensMovieSchedule)
                val movieScreening = getMovieScreeningByTime(movieSchedule)
                return movieScreening
            } catch (err: IllegalStateException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }

    private fun getMovieScheduleByName(): MovieSchedule {
        while (true) {
            try {
                val movieName = MovieName(InputView.getMovieName())
                val movieSchedule = cinemaSchedule[movieName]
                if (movieSchedule.isEmpty()) throw IllegalArgumentException("해당하는 영화가 없습니다.")
                return movieSchedule
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

    private fun getMovieScreeningByTime(movieSchedule: MovieSchedule): MovieScreening {
        while (true) {
            try {
                val sortedMovieTimeTable = movieSchedule.getAllMovieStartTime().sorted()
                val movieScreeningNumber = InputView.selectMovieScreening(sortedMovieTimeTable)
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
}
