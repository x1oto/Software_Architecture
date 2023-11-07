package lab1

class Bill(private var limitingAmount: Double) {
    var currentDebt: Double = 0.0

    fun check(amount: Double): Boolean = currentDebt + amount <= limitingAmount

    fun add(amount: Double) {
        if (check(amount)) {
            currentDebt += amount
        } else {
            println("Account limit exceeded!")
        }
    }

    fun pay(amount: Double) {
        if (amount <= currentDebt) {
            println("The balance was successfully replenished by $amount$, thank you!")
            currentDebt -= amount
        } else {
            println("Attempt to pay a larger amount than the debt on the account!")
        }
    }

    fun changeTheLimit(amount: Double) {
        println("Changed!")
        limitingAmount = amount
    }
}