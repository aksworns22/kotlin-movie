package payment

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import model.payment.Money
import model.payment.Point
import org.junit.jupiter.api.Test

class PointTest {
    @Test
    fun `포인트가 음수면 예외가 발생한다`() {
        shouldThrow<IllegalArgumentException> {
            Point(-1)
        }
    }

    @Test
    fun `포인트는 현금과 1대1 비율로 교환된다`() {
        Point(1_000).toMoney() shouldBe Money(1_000)
    }
}
