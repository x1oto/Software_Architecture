package lab2

fun main() {
    val shipSpec = ShipSpecifications(1, 1000.0, Port(1, 50.45466, 30.5238), 1000000, 10, 1, 2, 2, 1, 1.5)
    val ship = Ship(shipSpec)

//    ship.containers.add(HeavyContainer(1, 1500))
//    ship.containers.add(HeavyContainer(1, 1500))
//    ship.containers.add(HeavyContainer(1, 1500))

    println(ship.load(HeavyContainer(1, 1500)))
}