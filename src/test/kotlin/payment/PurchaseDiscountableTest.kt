package payment

import io.kotest.matchers.shouldBe
import model.payment.Money
import model.payment.PayType
import model.payment.PayTypeDiscount
import model.payment.Point
import model.payment.PointDiscount
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PurchaseDiscountableTest {
    @Test
    fun `포인트를 사용하면 예매 금액에서 차감된다`() {
        PointDiscount(
            point = Point(1000),
        ).applyDiscount(Money(5000)) shouldBe Money(4000)
    }

    @ParameterizedTest
    @CsvSource(
        "CREDIT_CARD, 14_250",
        "CASH, 14_700",
    )
    fun `결제 방식에 따라 결제 금액에서 일정 비율 할인된다`(
        payType: PayType,
        finalPrice: Int,
    ) {
        PayTypeDiscount(
            payType = payType,
        ).applyDiscount(Money(15_000)) shouldBe Money(finalPrice)
    }
}
