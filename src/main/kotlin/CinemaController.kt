@file:Suppress("ktlint")

// import view.InputView
// import view.OutputView
//
// class CinemaController(
//    private val movieReservationController: MovieReservationController,
//    private val moviePaymentController: MoviePaymentController,
// ) {
//    fun run() {
//        if (!getStartRequest()) return
//        val movieReservationGroup =
//            movieReservationController.handleMovieReservations()
//        OutputView.showMovieReservationResult("장바구니", movieReservationGroup)
//        val moviePaymentResult =
//            moviePaymentController.handleMoviePayment(
//                movieReservationGroup,
//            )
//        OutputView.printTotalPrice(moviePaymentResult)
//        if (getPaymentConfirm()) {
//            OutputView.showMovieReservationResult("예매 완료\n내역:", movieReservationGroup)
//        }
//        OutputView.end()
//    }
//
//    private fun getStartRequest(): Boolean {
//        while (true) {
//            try {
//                return InputView.startMovieReservation()
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
//            }
//        }
//    }
//
//    private fun getPaymentConfirm(): Boolean {
//        while (true) {
//            try {
//                return InputView.getPaymentConfirm()
//            } catch (err: IllegalArgumentException) {
//                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
//            }
//        }
//    }
// }
