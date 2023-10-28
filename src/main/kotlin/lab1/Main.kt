package lab1

fun main() {

    val operators = arrayOf(
        Operator(0, 2.0, 0.2, 0.05, 5),
        Operator(1, 1.5, 0.15, 0.04, 10),
    )

    val customers = arrayOf(
        Customer("Client 0", operators, arrayOf(Bill(100.0), Bill(1000.0))),
//        lab1.Customer("Client 1", operators, arrayOf(lab1.Bill(100.0),  lab1.Bill(1000.0)))
    )

    customers[0].connection(210.1)
//    customers[0].talk(30, customers[1])
//    customers[0].message(200, customers[1])
//    customers[0].talk(20, customers[1])

}