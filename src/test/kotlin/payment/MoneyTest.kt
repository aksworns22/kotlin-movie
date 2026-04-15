package payment

import io.kotest.matchers.shouldBe
import model.payment.Money
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class MoneyTest {
    @Test
    fun `1000원에 1000원을 더하면 2000원이 된다`() {
        Money(1000) + Money(1000) shouldBe Money(2000)
    }

    @Test
    fun `1000원에 1000원을 빼면 0원이 된다`() {
        Money(1000) - Money(1000) shouldBe Money(0)
    }

    @Test
    fun `돈이 음수인 경우 예외가 발생한다`() {
        assertThatThrownBy {
            Money(-1000)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `1000원을 3배하면 3000원이된다`() {
        Money(1000) * 3 shouldBe Money(3000)
    }

    @Test
    fun `1000원에 10% 비율은 100원이 된다`() {
        Money(1000) applyRate 0.1 shouldBe Money(100)
    }
}
