package lab2

data class ShipSpecifications (
    val id: Int,
    var fuel: Double,
    val currentPort: Port,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double
)

class Ship(
    private val specifications: ShipSpecifications
) : IShip {

    private val containers = mutableListOf<Container>()

    fun getCurrentContainers(): List<Container> {
        return containers.sortedBy { it.id }
    }

    override fun sailTo(p: Port): Boolean {
        TODO("Not yet implemented")
    }

    override fun reFuel(newFuel: Double) {
        specifications.fuel += newFuel
    }

    override fun load(cont: Container): Boolean {
        TODO("Not yet implemented")
    }

    override fun unLoad(cont: Container): Boolean {
        TODO("Not yet implemented")
    }
}
