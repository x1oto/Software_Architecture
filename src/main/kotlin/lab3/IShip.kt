package lab3

interface IShip {
    fun sailTo(p: Port): Boolean
    fun reFuel(newFuel: Double)
    fun load(cont: Container): Boolean
    fun unLoad(cont: Container): Boolean
}