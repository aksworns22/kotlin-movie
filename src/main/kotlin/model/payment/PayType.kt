package model.payment

enum class PayType {
    CREDIT_CARD,
    CASH,
    ;

    companion object {
        val size = entries.size
    }
}
