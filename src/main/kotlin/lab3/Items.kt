package lab3

abstract class Items(
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
): Items(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Heavy(
    id: Int,
    weight: Double,
    count: Int
): Items(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Refrigerated(
    id: Int,
    weight: Double,
    count: Int
): Items(id, weight, count) {
    override fun getItemWeight() = weight * count
}

class Liquid(
    id: Int,
    weight: Double,
    count: Int
): Items(id, weight, count) {
    override fun getItemWeight() = weight * count
}

