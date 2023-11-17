package lab3

abstract class Item(
    val id: Int,
    val weight: Double,
    val count: Int
) {
    abstract fun getItemWeight(): Double
}

class Small(
    id: Int,
    weight: Double,
    count: Int
): Item(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Heavy(
    id: Int,
    weight: Double,
    count: Int
): Item(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Refrigerated(
    id: Int,
    weight: Double,
    count: Int
): Item(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Liquid(
    id: Int,
    weight: Double,
    count: Int
): Item(id, weight, count) {
    override fun getItemWeight() = weight * count
}

