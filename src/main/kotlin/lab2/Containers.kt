package lab2

abstract class Container(val id: Int, val weight: Int) {
    abstract fun consumption(): Double
    fun compare(other: Container) = this == other
}

class BasicContainer(id: Int, weight: Int) : Container(id, weight) {

//    init {
//        require(weight <= 3000) { "Weight of BasicContainer should be less than or equal to 3000" }
//    }

    override fun consumption() = 2.50 * weight
}

open class HeavyContainer(id: Int, weight: Int) : Container(id, weight) {

//    init {
//        require(weight > 3000) { "Weight of HeavyContainer should be more than 3000" }
//    }

    override fun consumption(): Double = 3.0 * weight
}

class RefrigeratedContainer(id: Int, weight: Int) : HeavyContainer(id, weight) {
    override fun consumption(): Double = 5.0 * weight
}

class LiquidContainer(id: Int, weight: Int) : HeavyContainer(id, weight) {
    override fun consumption(): Double = 4.0 * weight
}