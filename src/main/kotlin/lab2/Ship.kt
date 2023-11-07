package lab2

data class ShipSpecifications (
    val id: Int,
    var fuel: Double,
    var currentPort: Port,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfBasicContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double
)

class Ship(private val specifications: ShipSpecifications) : IShip {

    var id = specifications.id
    var fuel = specifications.fuel

    private val containers = mutableListOf<Container>()
    fun getCurrentContainers(): List<Container> = containers.sortedBy { container -> container.id }
    fun changeCurrentPort(p: Port) { specifications.currentPort = p }

    private fun totalContainersWeight() = containers.sumOf { container -> container.weight }
    private fun containersFuelConsumption() = containers.sumOf { container -> container.consumption() }

    private fun canLoadContainerByType(cont: Container): Boolean {
        return when (cont) {
            is BasicContainer -> containers.count { container -> container is BasicContainer } <= specifications.maxNumberOfBasicContainers
            is LiquidContainer -> containers.count { container -> container is LiquidContainer } <= specifications.maxNumberOfLiquidContainers
            is RefrigeratedContainer -> containers.count { container -> container is RefrigeratedContainer } <= specifications.maxNumberOfRefrigeratedContainers
            is HeavyContainer -> containers.count { container -> container is HeavyContainer } <= specifications.maxNumberOfHeavyContainers
            else -> false
        }
    }

    override fun sailTo(p: Port): Boolean {
        val distance = specifications.currentPort.getDistance(p)
        val totalFuelConsumptionPerKM = (specifications.fuelConsumptionPerKM + containersFuelConsumption() / distance)
            .coerceAtLeast(0.0)

        val requiredFuel = totalFuelConsumptionPerKM * distance

        val canSail = specifications.fuel >= requiredFuel

        if (canSail) specifications.fuel -= requiredFuel

        return canSail
    }


    override fun load(cont: Container): Boolean {
        return if (canLoadContainerByType(cont) &&
            (totalContainersWeight() <= specifications.totalWeightCapacity) &&
            (containers.size <= specifications.maxNumberOfAllContainers)
        ) {
            containers.add(cont)
        } else false
    }

    override fun unLoad(cont: Container): Boolean {
        return if (containers.contains(cont)) {
            containers.remove(cont)
        } else false
    }

    override fun reFuel(newFuel: Double) {
        specifications.fuel += newFuel
    }
}
