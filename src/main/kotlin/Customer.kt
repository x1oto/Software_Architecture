class Customer(
    private val ID: Int,
    private val name: String,
//    val age: Int,
    private val operators: Array<Operator>,
    private val bills: Array<Bill>
) {
    fun talk(minute: Int, other: Customer) {
        for (operator in operators) {
            val talkingCost = operator.calculateTalkingCost(minute = minute)
            if (bills[ID].check(talkingCost)) {
                bills[ID].add(talkingCost)
                println("$name розмовляє з ${other.name} протягом $minute хвилин через Оператор ${operator.ID} за $talkingCost$")
            } else {
                println("$name: Недостатньо коштів для розмови")
            }
        }
    }

    fun message(quantity: Int, other: Customer){
        for (operator in operators) {
            val messageCost = operator.calculateMessageCost(quantity = quantity)
            if (bills[ID].check(messageCost)) {
                bills[ID].add(messageCost)
                println("$name надіслав ${other.name} $quantity sms через Оператор ${operator.ID} за $messageCost$")
            } else {
                println("$name: Недостатньо коштів для відправки sms")
            }
        }
    }

    fun connection(amount: Double){
        for (operator in operators) {
            val connectionCost = operator.calculateNetworkCost(amount = amount)
            if (bills[ID].check(connectionCost)) {
                bills[ID].add(connectionCost)
                println("$name використав $amount MBs через Оператор ${operator.ID} і витратив на це $connectionCost$")
            } else {
                println("$name: Недостатньо коштів для використання інтернету")
            }
        }
    }
}


//        val talkingCost = operator.calculateTalkingCost(minute = minute)
//        if (bills[ID].check(talkingCost)) {
//            bills[ID].add(talkingCost)
//            println("$name розмовляє з ${other.name} протягом $minute хвилин за $talkingCost$")
//        } else {
//            println("$name: Недостатньо коштів для розмови")
//        }