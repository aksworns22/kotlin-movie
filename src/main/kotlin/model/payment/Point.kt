package model.payment

@JvmInline
value class Point(
    private val value: Int,
) {
    init {
        require(value >= 0) { "포인트는 음수일 수 없습니다." }
    }

    fun toMoney(): Money = Money(value)
}
