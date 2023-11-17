package lab3

class MediumShip private constructor(
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
) : Ship {
    private val containers = mutableListOf<Container>()

    private fun totalContainersWeight() = containers.sumOf { container -> container.getContWeight() }
    private fun containersFuelConsumption() = containers.sumOf { container -> container.consumption() }

    private fun canLoadContainerByType(cont: Container): Boolean {
        return when (cont) {
            is BasicContainer -> containers.count { container -> container is BasicContainer } <= maxNumberOfBasicContainers
            is LiquidContainer -> containers.count { container -> container is LiquidContainer } <= maxNumberOfLiquidContainers
            is RefrigeratedContainer -> containers.count { container -> container is RefrigeratedContainer } <= maxNumberOfRefrigeratedContainers
            is HeavyContainer -> containers.count { container -> container is HeavyContainer } <= maxNumberOfHeavyContainers
            else -> false
        }
    }

    override fun sailTo(p: Port): Boolean {
        val distance = currentPort.getDistance(p)
        val totalFuelConsumptionPerKM = (fuelConsumptionPerKM + containersFuelConsumption() / distance)
            .coerceAtLeast(0.0)

        val requiredFuel = totalFuelConsumptionPerKM * distance
        val canSail = fuel >= requiredFuel
        if (canSail) fuel -= requiredFuel
        notifyPorts(p)

        return canSail
    }

    private fun notifyPorts(p: Port) {
        currentPort.outgoingShip(s = this)
        p.incomingShip(s = this)
    }

    override fun load(cont: Container): Boolean {
        return if (canLoadContainerByType(cont) &&
            totalContainersWeight() <= totalWeightCapacity &&
            containers.size <= maxNumberOfAllContainers &&
            currentPort.containersList.remove(cont)
        ) {
            containers.add(cont)
            true
        } else false
    }

    override fun unLoad(cont: Container): Boolean {
        return if (containers.contains(cont)) {
            currentPort.containersList.add(cont)
            containers.remove(cont)
        } else false
    }

    override fun changeCurrentPort(p: Port) {
        currentPort = p
    }

    override fun getCurrentContainers(): List<Container> {
        return containers.sortedBy { container -> container.id }
    }

    override fun reFuel(newFuel: Double) {
        fuel += newFuel
    }

    class Builder(private val id: Int, private val totalWeightCapacity: Int, private val fuelConsumptionPerKM: Double) {
        private var fuel: Double = 0.0
        private lateinit var currentPort: Port
        private var maxNumberOfAllContainers: Int = 0
        private var maxNumberOfBasicContainers: Int = 0
        private var maxNumberOfHeavyContainers: Int = 0
        private var maxNumberOfRefrigeratedContainers: Int = 0
        private var maxNumberOfLiquidContainers: Int = 0

        fun fuel(fuel: Double) = apply { this.fuel = fuel }
        fun currentPort(currentPort: Port) = apply { this.currentPort = currentPort }
        fun maxNumberOfAllContainers(maxNumberOfAllContainers: Int) = apply { this.maxNumberOfAllContainers = maxNumberOfAllContainers }
        fun maxNumberOfBasicContainers(maxNumberOfBasicContainers: Int) = apply { this.maxNumberOfBasicContainers = maxNumberOfBasicContainers }
        fun maxNumberOfHeavyContainers(maxNumberOfHeavyContainers: Int) = apply { this.maxNumberOfHeavyContainers = maxNumberOfHeavyContainers }
        fun maxNumberOfRefrigeratedContainers(maxNumberOfRefrigeratedContainers: Int) = apply { this.maxNumberOfRefrigeratedContainers = maxNumberOfRefrigeratedContainers }
        fun maxNumberOfLiquidContainers(maxNumberOfLiquidContainers: Int) = apply { this.maxNumberOfLiquidContainers = maxNumberOfLiquidContainers }

        fun build(): MediumShip {
            return MediumShip(
                id = id,
                fuel = fuel,
                currentPort = currentPort,
                totalWeightCapacity = totalWeightCapacity,
                maxNumberOfAllContainers = maxNumberOfAllContainers,
                maxNumberOfBasicContainers = maxNumberOfBasicContainers,
                maxNumberOfHeavyContainers = maxNumberOfHeavyContainers,
                maxNumberOfRefrigeratedContainers = maxNumberOfRefrigeratedContainers,
                maxNumberOfLiquidContainers = maxNumberOfLiquidContainers,
                fuelConsumptionPerKM = fuelConsumptionPerKM
            )
        }
    }
}

