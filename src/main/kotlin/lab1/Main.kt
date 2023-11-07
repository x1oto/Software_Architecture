package lab1

import com.google.gson.Gson
import java.io.File

data class OperatorData(
    val name: String,
    val talkingCost: Double,
    val messageCost: Double,
    val networkCost: Double,
    val maxLimit: Int
)

data class BillData(val currentDebt: Double)

data class CustomerData(
    val name: String,
    val operators: List<Int>,
    val bills: List<Int>
)

data class ActionData(
    val action: String,
    val customerIndex: Int,
    val amount: Double? = null,
    val quantity: Int? = null,
    val otherCustomerIndex: Int? = null,
    val minutes: Int? = null
)

fun main() {
    val jsonString = File("src/main/kotlin/lab1/resources/Input.json").readText()
    val gson = Gson()

    val jsonObject = gson.fromJson(jsonString, Map::class.java)

    val operators = (jsonObject["operators"] as List<*>).map {
        val operatorData = gson.fromJson(gson.toJson(it), OperatorData::class.java)
        Operator(
            operatorData.name,
            operatorData.talkingCost,
            operatorData.messageCost,
            operatorData.networkCost,
            operatorData.maxLimit
        )
    }

    val bills = (jsonObject["bills"] as List<*>).map {
        val billsData = gson.fromJson(gson.toJson(it), BillData::class.java)
        Bill(billsData.currentDebt)
    }

    val customers = (jsonObject["customers"] as List<*>).map {
        val customerData = gson.fromJson(gson.toJson(it), CustomerData::class.java)
        val customerOperators = customerData.operators.map { operators[it] }
        val customerBills = customerData.bills.map { bills[it] }
        Customer(customerData.name, customerOperators.toTypedArray(), customerBills.toTypedArray())
    }

    val actions = (jsonObject["actions"] as List<*>).map {
        val actionData = gson.fromJson(gson.toJson(it), ActionData::class.java)
        when (actionData.action) {
            "connection" -> customers[actionData.customerIndex].connection(actionData.amount!!)
            "talk" -> customers[actionData.customerIndex].talk(
                actionData.minutes!!,
                customers[actionData.otherCustomerIndex!!]
            )
            "sms" -> customers[actionData.customerIndex].message(
                actionData.quantity!!,
                customers[actionData.otherCustomerIndex!!]
            )
        }
    }
}
