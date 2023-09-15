class Operator(
    val ID: Int,
    private val talkingCharge: Double,
    private val messageCost: Double,
    private val networkCharge: Double,
    private val discountRate: Int
) {
    fun calculateTalkingCost(minute: Int, /* customer: Customer */): Double {
        return minute * applyDiscount(charge = talkingCharge)
    }

    fun calculateMessageCost(quantity: Int /*, customer: Customer, other: Customer*/ ): Double {
        return quantity * applyDiscount(charge = messageCost)
    }

    fun calculateNetworkCost(amount: Double): Double {
        return amount * applyDiscount(charge = networkCharge)
    }

    private fun applyDiscount(charge: Double): Double {
        val discountedRate = if (discountRate < 18 || discountRate > 65) discountRate else 0
        return charge * (1 - discountedRate / 100.0)
    }
}
