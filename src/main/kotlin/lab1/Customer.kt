package lab1

class Customer(
    private val name: String,
    private val operators: Array<Operator>,
    private val bills: Array<Bill>
) {
    private fun performActionForOperator(index: Int, cost: Double, operator: Operator, actionName: String) {
        if (bills[index].check(cost)) {
            bills[index].add(cost)
            println("$name $actionName через Оператор ${operator.ID} за $cost$")
        } else {
            println("$name: Недостатньо коштів для $actionName через Оператор ${operator.ID}")
            handleInsufficientBalance(index)
        }
    }

    private fun handleInsufficientBalance(index: Int) {
        println("Бажаєте поповнити баланс? -> 1")
        println("Бажаєте змінити ліміт? -> 2")
        println("Пропустити -> 3")
        print("Дія: ")
        when (readlnOrNull()?.toInt()) {
            1 -> topUpBalance(index)
            2 -> changeLimit(index)
            else -> println("Вихід!")
        }
    }

    private fun topUpBalance(index: Int) {
        print("Введіть суму для поповнення: ")
        val sum = readlnOrNull()?.toDouble() ?: 0.0
        bills[index].pay(sum)
    }

    private fun changeLimit(index: Int) {
        print("Введіть інший ліміт: ")
        val limit = readlnOrNull()?.toDouble() ?: 0.0
        bills[index].changeTheLimit(limit)
    }

    fun talk(minute: Int, other: Customer) {
        operators.forEachIndexed { index, operator ->
            val talkingCost = operator.calculateTalkingCost(minute = minute)
            performActionForOperator(index, talkingCost, operator, "розмови")
        }
    }

    fun message(quantity: Int, other: Customer) {
        operators.forEachIndexed { index, operator ->
            val messageCost = operator.calculateMessageCost(quantity = quantity)
            performActionForOperator(index, messageCost, operator, "відправки sms")
        }
    }

    fun connection(amount: Double) {
        operators.forEachIndexed { index, operator ->
            val connectionCost = operator.calculateNetworkCost(amount = amount)
            performActionForOperator(index, connectionCost, operator, "використання інтернету")
        }
    }
}
