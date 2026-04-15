@file:Suppress("ktlint")

// import model.reservation.MovieReservationGroup
// import model.schedule.CinemaSchedule
// import model.schedule.MovieSchedule
// import model.schedule.MovieScreening
// import view.InputView
// import view.OutputView
// import java.time.format.DateTimeParseException
//
// class MovieReservationController(
//    private val cinemaSchedule: CinemaSchedule,
//    private var movieReservationGroup: MovieReservationGroup =
//        MovieReservationGroup(
//            emptySet(),
//        ),
// ) {
//    fun handleMovieReservations(): MovieReservationGroup {
//        do {
//            val movieScreening = getReservableMovieScreening()
//            movieReservationGroup =
//                getMovieScreeningReservations(
//                    movieScreening = movieScreening,
//                    movieReservationGroup = movieReservationGroup,
//                )
//        } while (getAdditionalMovieReservation())
//        return movieReservationGroup
//    }
//
//    private fun getReservableMovieScreening(): MovieScreening {
//        while (true) {
//            try {
//                val allScreensMovieSchedule = getMovieScheduleByName()
//                val movieSchedule = getMovieScheduleByDate(allScreensMovieSchedule)
//                val movieScreening = getMovieScreeningByTime(movieSchedule)
//                if (!movieReservationGroup.hasAvailableSeat(movieScreening)) {
//                    OutputView.showErrorMessage("모든 좌석이 매진입니다. 다른 영화를 입력해주세요.")
//                    continue
//                }
//                return movieScreening
//            } catch (err: IllegalStateException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getMovieScheduleByName(): MovieSchedule {
//        while (true) {
//            try {
//                val movieSchedule = cinemaSchedule[InputView.getMovieName()]
//                if (movieSchedule.isEmpty()) throw IllegalArgumentException("해당하는 영화가 없습니다.")
//                return movieSchedule
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getMovieScheduleByDate(movieSchedule: MovieSchedule): MovieSchedule {
//        while (true) {
//            try {
//                val screeningDate = InputView.getScreeningDate()
//                val movieSchedule = movieSchedule.getMovieSchedule(screeningDate)
//                if (movieSchedule.isEmpty()) throw IllegalArgumentException("해당 요일에 영화가 없습니다.")
//                return movieSchedule
//            } catch (_: DateTimeParseException) {
//                throw IllegalArgumentException("날짜 형식이 올바르지 않습니다. (YYYY-MM-DD)")
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getMovieScreeningByTime(movieSchedule: MovieSchedule): MovieScreening {
//        while (true) {
//            try {
//                OutputView.showMovieSchedule(movieSchedule)
//                val movieScreening = InputView.selectMovieScreening(movieSchedule)
//                if (!movieReservationGroup.isReservable(movieScreening)) {
//                    OutputView.showErrorMessage("선택하신 상영 시간이 겹칩니다. 다른 시간을 선택해 주세요.")
//                    continue
//                }
//                return movieScreening
//            } catch (error: IllegalArgumentException) {
//                OutputView.showErrorMessage(error.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getMovieScreeningReservations(
//        movieScreening: MovieScreening,
//        movieReservationGroup: MovieReservationGroup,
//    ): MovieReservationGroup {
//        while (true) {
//            try {
// //                OutputView.showSeatGroup(movieScreening.seatGroup)
//                val positions = InputView.selectSeats()
//                val finalMovieReservationGroup =
//                    positions.fold(movieReservationGroup) { group, seatPosition ->
//                        group.reserve(
//                            movieScreening = movieScreening,
//                            seatPosition = seatPosition,
//                        )
//                    }
//                OutputView.showMovieReservationResult(
//                    initialMessage = "장바구니에 추가됨",
//                    reservationGroup = finalMovieReservationGroup - movieReservationGroup,
//                )
//                return finalMovieReservationGroup
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getAdditionalMovieReservation(): Boolean {
//        while (true) {
//            try {
//                return InputView.selectAdditionalMovieReservation()
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 오류가 발생했습니다.")
//            }
//        }
//    }
// }
