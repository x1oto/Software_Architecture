package lab3

import java.io.File
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName

data class PortData(
    @SerializedName("id") val id: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("current") val current: List<String>,
    @SerializedName("history") val history: List<String>,
    @SerializedName("container") val container: List<String>
)

data class ActionData(
    @SerializedName("action") val action: String,
    @SerializedName("containers") val containers: List<Int>,
    @SerializedName("newFuel") val newFuel: Double,
    @SerializedName("port") val port: Int,
    @SerializedName("ship") val ship: Int
)

data class ContainerData(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
    @SerializedName("items") val items: List<ItemsData>
)

data class ItemsData(
    @SerializedName("id") val id: Int,
    @SerializedName("weight") val weight: Double,
    @SerializedName("count") val count: Int,
    @SerializedName("type") val type: String
)

data class ShipData(
    @SerializedName("id") val id: Int,
    @SerializedName("fuel") val fuel: Double,
    @SerializedName("portId") val portId: Int,
    @SerializedName("totalWeightCapacity") val totalWeightCapacity: Int,
    @SerializedName("maxNumberOfAllContainers") val maxNumberOfAllContainers: Int,
    @SerializedName("maxNumberOfBasicContainers") val maxNumberOfBasicContainers: Int,
    @SerializedName("maxNumberOfHeavyContainers") val maxNumberOfHeavyContainers: Int,
    @SerializedName("maxNumberOfRefrigeratedContainers") val maxNumberOfRefrigeratedContainers: Int,
    @SerializedName("maxNumberOfLiquidContainers") val maxNumberOfLiquidContainers: Int,
    @SerializedName("fuelConsumptionPerKM") val fuelConsumptionPerKM: Double,
    @SerializedName("containers") val containers: List<String>
)

private const val INPUT_FILE_PATH = "src/main/kotlin/lab3/Input.json"
private const val OUTPUT_FILE_PATH = "src/main/kotlin/lab3/Output.json"

fun main() {
    val (gson, jsonObject) = loadJsonAndCreateGsonMap()

    val containers = getContainers(jsonObject, gson)
    val ports = containers?.let { getPorts(jsonObject, gson, it) }
    val ship = ports?.let { getShips(jsonObject, gson, it) }

    val outputPorts = mutableListOf<PortData>()
    val outputShips = mutableListOf<ShipData>()

    if (ship != null) {
        loadAndExecuteActionsFromJson(jsonObject, gson, ship, containers, ports, outputPorts, outputShips)
    }
    createJsonBasedOnActions(gson, outputPorts, outputShips)
}

private fun loadJsonAndCreateGsonMap(): Pair<Gson, Map<*, *>> {
    val jsonString = File(INPUT_FILE_PATH).readText()
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonString, Map::class.java)
    return Pair(gson, jsonObject)
}

private fun createJsonBasedOnActions(
    gson: Gson,
    outputPortData: List<PortData>,
    outputShipsData: List<ShipData>
) {
    val portsAndShipsMap = mapOf(
        "ports" to outputPortData,
        "ships" to outputShipsData
    )
    val portsAndShipsResultsJSON = gson.toJson(portsAndShipsMap)
    File(OUTPUT_FILE_PATH).writeText(portsAndShipsResultsJSON)
}

private fun loadAndExecuteActionsFromJson(
    jsonObject: Map<*, *>,
    gson: Gson,
    ships: List<Ship>,
    containers: List<Container>,
    ports: List<Port>,
    outputPortData: MutableList<PortData>,
    outputShipData: MutableList<ShipData>
) {
    try {
        (jsonObject["actions"] as List<*>).map { action ->
            val actionData = gson.fromJson(gson.toJson(action), ActionData::class.java)
            val ship = actionData.ship

            when (actionData.action) {
                "load" -> executeLoadAction(actionData, ships[ship], containers)
                "unload" -> executeUnloadAction(actionData, ships[ship], containers)
                "sail" -> executeSailAction(ships[ship], ports, actionData)
                "refuel" -> executeRefuelAction(ships[ship], actionData)
                else -> println("[Error] Invalid action in Input.json/\"actions\". " +
                        "Must be one of the following: load, unload, sail, refuel. Please check your json file and try again.")
            }
        }
        convertPortToPortData(ports, outputPortData)
        convertShipToShipData(ships, outputShipData)
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to perform the action. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
    }
}

private fun convertPortToPortData(
    ports: List<Port>,
    outputPortData: MutableList<PortData>
) {
    ports.forEach { port ->
        val currentShipsAsString = port.current.map { it.toString() }
        val historyShipsAsString = port.history.map { it.toString() }
        val containersAsString = port.containersList.map { it.toString() }

        outputPortData.add(
            PortData(
                id = port.id,
                latitude = port.latitude,
                longitude = port.longitude,
                current = currentShipsAsString,
                history = historyShipsAsString,
                container = containersAsString
            )
        )
    }
}

private fun convertShipToShipData(
    ships: List<Ship>,
    outputShipData: MutableList<ShipData>
) {
    ships.forEach { ship ->
        val containersAsString = ship.getCurrentContainers().map { it.toString() }

        when (ship) {
            is LightWeightShip -> {
                outputShipData.add(
                    ShipData(
                        id = ship.id,
                        fuel = ship.fuel,
                        portId = ship.currentPort.id,
                        totalWeightCapacity = ship.totalWeightCapacity,
                        maxNumberOfAllContainers = ship.maxNumberOfAllContainers,
                        maxNumberOfBasicContainers = ship.maxNumberOfBasicContainers,
                        maxNumberOfHeavyContainers = ship.maxNumberOfHeavyContainers,
                        maxNumberOfRefrigeratedContainers = ship.maxNumberOfRefrigeratedContainers,
                        maxNumberOfLiquidContainers = ship.maxNumberOfLiquidContainers,
                        fuelConsumptionPerKM = ship.fuelConsumptionPerKM,
                        containers = containersAsString
                    )
                )
            }
            is MediumShip -> {
                outputShipData.add(
                    ShipData(
                        id = ship.id,
                        fuel = ship.fuel,
                        portId = ship.currentPort.id,
                        totalWeightCapacity = ship.totalWeightCapacity,
                        maxNumberOfAllContainers = ship.maxNumberOfAllContainers,
                        maxNumberOfBasicContainers = ship.maxNumberOfBasicContainers,
                        maxNumberOfHeavyContainers = ship.maxNumberOfHeavyContainers,
                        maxNumberOfRefrigeratedContainers = ship.maxNumberOfRefrigeratedContainers,
                        maxNumberOfLiquidContainers = ship.maxNumberOfLiquidContainers,
                        fuelConsumptionPerKM = ship.fuelConsumptionPerKM,
                        containers = containersAsString
                    )
                )
            }
            is HeavyShip -> {
                outputShipData.add(
                    ShipData(
                        id = ship.id,
                        fuel = ship.fuel,
                        portId = ship.currentPort.id,
                        totalWeightCapacity = ship.totalWeightCapacity,
                        maxNumberOfAllContainers = ship.maxNumberOfAllContainers,
                        maxNumberOfBasicContainers = ship.maxNumberOfBasicContainers,
                        maxNumberOfHeavyContainers = ship.maxNumberOfHeavyContainers,
                        maxNumberOfRefrigeratedContainers = ship.maxNumberOfRefrigeratedContainers,
                        maxNumberOfLiquidContainers = ship.maxNumberOfLiquidContainers,
                        fuelConsumptionPerKM = ship.fuelConsumptionPerKM,
                        containers = containersAsString
                    )
                )
            }
            else -> {
                // Ця гілка викликається, якщо ship не є ані LightWeightShip, ані MediumShip, ані HeavyShip
            }
        }
    }
}

private fun executeLoadAction(
    actionData: ActionData,
    ship: Ship,
    containers: List<Container>
) {
    for (index in actionData.containers) {
        if (index in containers.indices) {
            val isSuccess = ship.load(containers[index])
            println("Trying to load ${containers[index]} on ship... Result: $isSuccess")
        } else {
            println("[Error] Incorrect container index or missing container. Please fill in your JSON correctly!")
        }
    }
}

private fun executeUnloadAction(
    actionData: ActionData,
    ship: Ship,
    containers: List<Container>
) {
    for (index in actionData.containers) {
        if (index in containers.indices) {
            val isSuccess = ship.unLoad(containers[index])
            println("Trying to unload ${containers[index]} from ship... Result: $isSuccess")
        } else {
            println("[Error] Incorrect container index or missing container. Please fill in your JSON correctly!")
        }
    }
}

private fun executeSailAction(
    ship: Ship,
    ports: List<Port>,
    actionData: ActionData,
) {
    val isSuccess = ship.sailTo(ports[actionData.port])
    if (isSuccess) ship.changeCurrentPort(ports[actionData.port])
    println("Sail to port ${ports[actionData.port].id}... Result: $isSuccess")
}

private fun executeRefuelAction(
    ship: Ship,
    actionData: ActionData
) {
    ship.reFuel(actionData.newFuel)
    println("Ship refueled successfully.")
}

private fun getShips(
    jsonObject: Map<*, *>,
    gson: Gson,
    ports: List<Port>
): List<Ship>? {
    return try {
        val ships = (jsonObject["ships"] as List<*>).map {
            val shipsData = gson.fromJson(gson.toJson(it), ShipData::class.java)
            shipsFactory(ports = ports, shipsData = shipsData)
        }
        ships
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a ship. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
        null
    }
}

private fun shipsFactory(ports: List<Port>, shipsData: ShipData): Ship {
    return when (shipsData.totalWeightCapacity) {
        in 0..6000 -> createLightWeightShip(ports = ports, shipsData = shipsData)
        in 6000 .. 24000 -> createMediumShip(ports = ports, shipsData = shipsData)
        else -> createHeavyShip(ports = ports, shipsData = shipsData)
    }
}

private fun createLightWeightShip(ports: List<Port>, shipsData: ShipData): LightWeightShip {
    return with(shipsData) {
        LightWeightShip.Builder(id, totalWeightCapacity, fuelConsumptionPerKM)
            .fuel(fuel)
            .currentPort(ports[portId])
            .maxNumberOfAllContainers(maxNumberOfAllContainers)
            .maxNumberOfBasicContainers(maxNumberOfBasicContainers)
            .maxNumberOfHeavyContainers(maxNumberOfHeavyContainers)
            .maxNumberOfRefrigeratedContainers(maxNumberOfRefrigeratedContainers)
            .maxNumberOfLiquidContainers(maxNumberOfLiquidContainers)
            .build()
    }
}

private fun createMediumShip(ports: List<Port>, shipsData: ShipData): MediumShip {
    return with(shipsData) {
        MediumShip.Builder(id, totalWeightCapacity, fuelConsumptionPerKM)
            .fuel(fuel)
            .currentPort(ports[portId])
            .maxNumberOfAllContainers(maxNumberOfAllContainers)
            .maxNumberOfBasicContainers(maxNumberOfBasicContainers)
            .maxNumberOfHeavyContainers(maxNumberOfHeavyContainers)
            .maxNumberOfRefrigeratedContainers(maxNumberOfRefrigeratedContainers)
            .maxNumberOfLiquidContainers(maxNumberOfLiquidContainers)
            .build()
    }
}

private fun createHeavyShip(ports: List<Port>, shipsData: ShipData): HeavyShip {
    return with(shipsData) {
        HeavyShip.Builder(id, totalWeightCapacity, fuelConsumptionPerKM)
            .fuel(fuel)
            .currentPort(ports[portId])
            .maxNumberOfAllContainers(maxNumberOfAllContainers)
            .maxNumberOfBasicContainers(maxNumberOfBasicContainers)
            .maxNumberOfHeavyContainers(maxNumberOfHeavyContainers)
            .maxNumberOfRefrigeratedContainers(maxNumberOfRefrigeratedContainers)
            .maxNumberOfLiquidContainers(maxNumberOfLiquidContainers)
            .build()
    }
}

private fun getContainers(
    jsonObject: Map<*, *>,
    gson: Gson
): List<Container>? {
    return try {
            val containers = (jsonObject["containers"] as List<*>).map {
                val containersData = gson.fromJson(gson.toJson(it), ContainerData::class.java)
                containersFactory(containersData, itemsFactory(containersData.items))
        }
        containers
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a container. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
        null
    }
}

private fun containersFactory(containersData: ContainerData, items: List<Item>) =
    when (containersData.type) {
        "basic" -> BasicContainer(id = containersData.id, items = items)
        "heavy" -> HeavyContainer(id = containersData.id, items = items)
        "refrigerated" -> RefrigeratedContainer(id = containersData.id, items = items)
        "liquid" -> LiquidContainer(id = containersData.id, items = items)
        else -> throw IllegalArgumentException("No such container type. Please check your JSON file.")
    }

private fun itemsFactory(items: List<ItemsData>): List<Item> {
    return items.map { item ->
        when (item.type) {
            "small" -> Small(id = item.id, weight = item.weight, count = item.count)
            "heavy" -> Heavy(id = item.id, weight = item.weight, count = item.count)
            "refrigerated" -> Refrigerated(id = item.id, weight = item.weight, count = item.count)
            "liquid" -> Liquid(id = item.id, weight = item.weight, count = item.count)
            else -> throw IllegalArgumentException("No such item type. Please check your JSON file.")
        }
    }
}

private fun getPorts(jsonObject: Map<*, *>, gson: Gson, containers: List<Container>): List<Port>? {
    return try {
        (jsonObject["ports"] as List<*>).map {
            val portsData = gson.fromJson(gson.toJson(it), PortData::class.java)
            val containersToAdd = portsData.container.map { index ->
                containers[index.toDouble().toInt()]
            }
            Port(
                id = portsData.id,
                latitude = portsData.latitude,
                longitude = portsData.longitude,
                containersList = containersToAdd.toMutableList()
            )
        }
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a port. Json structure not followed. " +
                "Please make sure the names are correct and there are no mistakes with the data types.")
        null
    }
}