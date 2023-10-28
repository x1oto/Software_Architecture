package lab1

class Bill(private var limitingAmount: Double) {
    private var currentDebt: Double = 0.0

    fun check(amount: Double): Boolean {
        return currentDebt + amount <= limitingAmount
    }

    fun add(amount: Double) {
        if (check(amount)) {
            currentDebt += amount
        } else {
            println("Перевищено ліміт рахунку")
        }
    }

    fun pay(amount: Double) {
        if (amount <= currentDebt) {
            currentDebt -= amount
            println("Змінено!")
        } else {
            println("Спроба оплати більшої суми, ніж борг на рахунку")
        }
    }

    fun changeTheLimit(amount: Double) {
        limitingAmount = amount
        println("Змінено!")
    }
}