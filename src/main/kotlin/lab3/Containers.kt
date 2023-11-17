package lab3

abstract class Container(val id: Int, val items: List<Item>) {
    abstract fun consumption(): Double
    abstract fun getContWeight(): Double
    fun compare(other: Container) = this == other
}

class BasicContainer(id: Int, items: List<Item>) : Container(id, items) {
    override fun getContWeight(): Double = items.sumOf { item -> item.getItemWeight() }
    override fun consumption() = getContWeight() * 2.5
}

open class HeavyContainer(id: Int, items: List<Item>) : Container(id, items) {
    override fun getContWeight(): Double = items.sumOf { item -> item.getItemWeight() }
    override fun consumption() = getContWeight() * 3
}

class RefrigeratedContainer(id: Int, items: List<Item>) : HeavyContainer(id, items) {
    override fun getContWeight(): Double = items.sumOf { item -> item.getItemWeight() }
    override fun consumption() = getContWeight() * 5
}

class LiquidContainer(id: Int, items: List<Item>) : HeavyContainer(id, items) {
    override fun getContWeight(): Double = items.sumOf { item -> item.getItemWeight() }
    override fun consumption() = getContWeight() * 4
}