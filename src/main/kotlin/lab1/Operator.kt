package lab1

class Operator(
    val name: String,
    private val talkingCharge: Double,
    private val messageCost: Double,
    private val networkCharge: Double,
    customerAge: Int
) {
    private val discountRate = customerAge
    private fun applyDiscount(charge: Double): Double {
        val discountedRate = if (discountRate < 18 || discountRate > 65) discountRate else 0
        return charge * (1 - discountedRate / 100.0)
    }
    fun calculateTalkingCost(minute: Int) = minute * applyDiscount(charge = talkingCharge)

    fun calculateMessageCost(quantity: Int) = quantity * applyDiscount(charge = messageCost)

    fun calculateNetworkCost(amount: Double) = amount * applyDiscount(charge = networkCharge)
}
