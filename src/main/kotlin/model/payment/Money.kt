package model.payment

@JvmInline
value class Money(
    private val value: Int,
) : Comparable<Int> by value {
    init {
        require(value >= 0) { "금액은 음수일 수 없습니다." }
    }

    operator fun plus(other: Money): Money = Money(value + other.value)

    operator fun minus(other: Money): Money = Money(value - other.value)

    operator fun times(other: Int): Money = Money(value * other)

    fun minusWithMinimum(
        money: Money,
        minimum: Money,
    ): Money = Money(maxOf(value - money.value, minimum.value))

    fun toInt(): Int = value

    infix fun applyRate(rate: Double): Money = Money((value * rate).toInt())
}
