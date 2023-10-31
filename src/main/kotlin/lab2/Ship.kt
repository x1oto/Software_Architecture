package lab2

data class ShipSpecifications (
    val id: Int,
    var fuel: Double,
    val currentPort: Port,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfBasicContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double
)

class Ship(private val specifications: ShipSpecifications) : IShip {

    private val containers = mutableListOf<Container>()
    fun getCurrentContainers(): List<Container> = containers.sortedBy { it.id }

    private fun totalContainersWeight() = containers.sumOf { it.weight }
    private fun containersFuelConsumption() = containers.sumOf { it.consumption() }

    private fun canLoadContainerByType(cont: Container): Boolean {
        return when (cont) {
            is BasicContainer -> containers.count { it is BasicContainer } <= specifications.maxNumberOfBasicContainers
            is LiquidContainer -> containers.count { it is LiquidContainer } <= specifications.maxNumberOfLiquidContainers
            is RefrigeratedContainer -> containers.count { it is RefrigeratedContainer } <= specifications.maxNumberOfRefrigeratedContainers
            is HeavyContainer -> containers.count { it is HeavyContainer } <= specifications.maxNumberOfHeavyContainers
            else -> false
        }
    }

    override fun sailTo(p: Port): Boolean {
//        TODO: notify ports about ship movements
//        specifications.currentPort.outgoingShip(this)
//        p.incomingShip(this)

        val distance = specifications.currentPort.getDistance(p)
        val distanceCanBeCovered = specifications.fuel / specifications.fuelConsumptionPerKM + containersFuelConsumption()
        return distanceCanBeCovered >= distance
    }

    override fun load(cont: Container): Boolean {
        if (canLoadContainerByType(cont) &&
            (totalContainersWeight() <= specifications.totalWeightCapacity) &&
            (containers.size <= specifications.maxNumberOfAllContainers)
        ) {
            if (specifications.currentPort.containersList.contains(cont)) {
                return containers.add(cont)
            }
        }
        return false
    }

    override fun unLoad(cont: Container): Boolean {
        if (containers.contains(cont)) {
            return containers.remove(cont)
        }
        return false
    }

    override fun reFuel(newFuel: Double) {
        specifications.fuel += newFuel
    }
}
