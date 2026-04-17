import dto.MovieReservationDto
import view.InputView
import view.MovieReservationResultDto
import view.OutputView

class CinemaController(
    private val movieRepository: MovieRepository,
    private val movieReservationController: MovieReservationController,
    private val moviePaymentController: MoviePaymentController,
) {
    fun run() {
        if (!getStartRequest()) return
        val movieReservationGroup =
            movieReservationController.handleMovieReservations()
        val movieReservationResultDtoGroup =
            movieReservationGroup.map { movieSeatSelection ->
                MovieReservationResultDto(
                    movieSeatSelection.movieName,
                    startTime = movieSeatSelection.startTime,
                    seatName = movieSeatSelection.seatName,
                )
            }
        OutputView.showMovieReservationResult("장바구니", movieReservationResultDtoGroup)
        val moviePaymentResult =
            moviePaymentController.handleMoviePayment(
                movieReservationGroup,
            )
        OutputView.printTotalPrice(moviePaymentResult.finalPrice.toInt())
        if (getPaymentConfirm()) {
            movieRepository.insertMovieReservation(
                *movieReservationGroup
                    .map { movieSeatSelection ->
                        MovieReservationDto(
                            movieName = movieSeatSelection.movieName,
                            startTime = movieSeatSelection.startTime,
                            seatName = movieSeatSelection.seatName.split(":").first(),
                        )
                    }.toTypedArray(),
            )
            OutputView.showMovieReservationResult("예매 완료\n내역:", movieReservationResultDtoGroup)
        }
        OutputView.end()
    }

    private fun getStartRequest(): Boolean {
        while (true) {
            try {
                return InputView.startMovieReservation()
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }

    private fun getPaymentConfirm(): Boolean {
        while (true) {
            try {
                return InputView.getPaymentConfirm()
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }
}
