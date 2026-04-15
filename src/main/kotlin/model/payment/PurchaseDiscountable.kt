package model.payment

interface PurchaseDiscountable {
    fun applyDiscount(originalMoney: Money): Money
}

class SequentialPurchaseDiscount(
    discountableGroup: List<PurchaseDiscountable>,
) {
    private val discountableGroup: List<PurchaseDiscountable> = discountableGroup.toList()

    fun getDiscountedPrice(originalMoney: Money): Money =
        discountableGroup.fold(originalMoney) { discountedMoney, discountable ->
            discountable.applyDiscount(discountedMoney)
        }
}

class PayTypeDiscount(
    private val payType: PayType,
) : PurchaseDiscountable {
    override fun applyDiscount(originalMoney: Money): Money =
        when (payType) {
            PayType.CREDIT_CARD -> originalMoney applyRate (1 - CREDIT_CARD_DISCOUNT_RATIO)
            PayType.CASH -> originalMoney applyRate (1 - CASH_DISCOUNT_RATIO)
        }

    companion object {
        const val CREDIT_CARD_DISCOUNT_RATIO = 0.05
        const val CASH_DISCOUNT_RATIO = 0.02
    }
}

class PointDiscount(
    private val point: Point,
) : PurchaseDiscountable {
    override fun applyDiscount(originalMoney: Money): Money =
        originalMoney.minusWithMinimum(
            money = point.toMoney(),
            minimum = Money(0),
        )
}
