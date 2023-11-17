package lab3

interface Ship {
    fun sailTo(p: Port): Boolean
    fun reFuel(newFuel: Double)
    fun load(cont: Container): Boolean
    fun unLoad(cont: Container): Boolean
    fun changeCurrentPort(p: Port)
    fun getCurrentContainers(): List<Container>
}
