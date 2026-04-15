import model.payment.DefaultMoviePayment
import model.payment.MoviePaymentResult
import model.payment.PayType
import model.payment.Point
import model.reservation.MovieReservationGroup
import view.InputView
import view.OutputView

class MoviePaymentController {
    fun handleMoviePayment(movieReservationGroup: MovieReservationGroup): MoviePaymentResult {
        val point = getPoint()
        val payType = getPayType()
        // DefaultMoviePayment의 인터페이스를 만들어서 파라미터로 주입받아 확장성을 가져가 볼 수도 있을 것 같습니다!
        return DefaultMoviePayment(
            reservations = movieReservationGroup,
            point = point,
            payType = payType,
        ).calculate()
    }

    fun getPoint(): Point {
        while (true) {
            try {
                return Point(InputView.getPointNumber())
            } catch (_: NumberFormatException) {
                OutputView.showErrorMessage("숫자만 입력 가능합니다.")
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }

    fun getPayType(): PayType {
        while (true) {
            try {
                return when (InputView.getPayTypeNumber()) {
                    1 -> PayType.CREDIT_CARD
                    2 -> PayType.CASH
                    else -> throw IllegalArgumentException("존재하지 않는 선택번호입니다")
                }
            } catch (_: NumberFormatException) {
                OutputView.showErrorMessage("숫자만 입력 가능합니다.")
            } catch (err: IllegalArgumentException) {
                OutputView.showErrorMessage(err.message ?: "알 수 없는 에러가 발생했습니다.")
            }
        }
    }
}
