package lab1
class Customer(
    private val name: String,
    private val operators: Array<Operator>,
    private val bills: Array<Bill>
) {
    private fun performActionForOperator(index: Int, cost: Double, operator: Operator, actionName: String) {
        var hasSufficientFunds = bills[index].check(cost)

        while (!hasSufficientFunds) {
            println("{Houston, We've Got a Problem!} $name - Insufficient funds for $actionName through the Operator ${operator.name}")
            handleInsufficientBalance(index)

            hasSufficientFunds = bills[index].check(cost)
        }

        if (hasSufficientFunds) {
            println("$name $actionName through the Operator ${operator.name} for $cost$")
            bills[index].add(cost)
        }
    }

    private fun handleInsufficientBalance(index: Int) {
        var isValidChoice = false
        var userInputToInt = 0

        while (!isValidChoice) {
            print("Choose variant: 1. Top up balance / 2. Change limit: ")
            val userInput = readlnOrNull()

            try {
                userInputToInt = userInput!!.toInt()
                isValidChoice = true
            } catch (e: Exception) {
                println("[i] Enter just a number to choose a variant. 1 / 2")
            }
        }

        when (userInputToInt) {
            1 -> topUpBalance(index)
            2 -> changeLimit(index)
        }
    }

    private fun topUpBalance(index: Int) {
        print("Enter the amount to top up (max: ${bills[index].currentDebt}$): ")
        val sum = readln().toDoubleOrNull() ?: 0.0
        bills[index].pay(sum)
    }

    private fun changeLimit(index: Int) {
        print("Enter a different limit: ")
        val limit = readln().toDoubleOrNull()

        if (limit != null) {
            bills[index].changeTheLimit(limit)
        } else {
            println("Enter a valid integer value!")
        }
    }

    fun talk(minute: Int, other: Customer) {
        operators.forEachIndexed { index, operator ->
            val talkingCost = operator.calculateTalkingCost(minute = minute)
            performActionForOperator(index, talkingCost, operator, "conversations with ${other.name}")
        }
    }

    fun message(quantity: Int, other: Customer) {
        operators.forEachIndexed { index, operator ->
            val messageCost = operator.calculateMessageCost(quantity = quantity)
            performActionForOperator(index, messageCost, operator, "sending sms to ${other.name}")
        }
    }

    fun connection(amount: Double) {
        operators.forEachIndexed { index, operator ->
            val connectionCost = operator.calculateNetworkCost(amount = amount)
            performActionForOperator(index, connectionCost, operator, "using the internet")
        }
    }
}
