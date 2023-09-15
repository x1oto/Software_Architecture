fun main() {
    // Ініціалізація масивів customers, operators, та bills
    val operators = arrayOf(
        Operator(0,2.0, 0.2, 0.05, 5),
        Operator(1,1.5, 0.15, 0.04, 10),
    )

    val bills = arrayOf(
        Bill(1000.0),
        Bill(1500.0),
        Bill(1.0)
    )

    val customers = arrayOf(
        Customer(0, "Client 1", operators, bills),
        Customer(1, "Client 2", operators, bills)
    )

    // Приклад використання методу talk
    customers[0].connection(10.1 /* operators[1] */)
}