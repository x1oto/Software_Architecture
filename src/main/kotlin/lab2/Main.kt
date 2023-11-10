package lab2

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.File

data class PortData(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val current: List<String>,
    val history: List<String>,
    val container: List<String>
)

data class ActionData(
    val action: String,
    val containers: List<Int>,
    val newFuel: Double,
    val port: Int,
    val ship: Int
)

data class ContainerData(
    val id: Int,
    val weight: Int,
    val type: String
)

data class ShipData(
    val id: Int,
    val fuel: Double,
    val portId: Int,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfBasicContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double,
    val containers: List<String>
)

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
    val jsonString = File("src/main/kotlin/lab2/Input.json").readText()
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
    File("src/main/kotlin/lab2/Output.json").writeText(portsAndShipsResultsJSON)
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

        outputShipData.add(ShipData(
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
        ))
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
            println("Trying to load ${containers[index]} on ship ${ship.id}... Result: $isSuccess")
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
            println("Trying to unload ${containers[index]} from ship ${ship.id}... Result: $isSuccess")
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
    println("Sail to next port... Result: $isSuccess")
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

            with(shipsData) {
                Ship(
                    id = id,
                    fuel = fuel,
                    currentPort = ports[portId],
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
        ships
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a ship. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
        null
    }
}

private fun getContainers(
    jsonObject: Map<*, *>,
    gson: Gson
): List<Container>? {
    return try {
        val containers = (jsonObject["containers"] as List<*>).map {
            val containersData = gson.fromJson(gson.toJson(it), ContainerData::class.java)
            when (containersData.type) {
                "basicContainer" -> BasicContainer(id = containersData.id, weight = containersData.weight)
                "heavyContainer" -> HeavyContainer(id = containersData.id, weight = containersData.weight)
                "refrigeratedContainer" -> RefrigeratedContainer(id = containersData.id, weight = containersData.weight)
                "liquidContainer" -> LiquidContainer(id = containersData.id, weight = containersData.weight)
                else -> throw IllegalArgumentException("No such container type. Please check your JSON file.")
            }
        }
        containers
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a container. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
        null
    }
}

private fun getPorts(
    jsonObject: Map<*, *>,
    gson: Gson,
    containers: List<Container>
): List<Port>? {
    return try {
        val ports = (jsonObject["ports"] as List<*>).map {
            val portsData = gson.fromJson(gson.toJson(it), PortData::class.java)
            val containersToAdd = mutableListOf<Container>()

            // TODO: видалення контейнера з ліста порту
            // TODO: require
            // TODO: розкинути по пекеджах
            for (index in portsData.container) {
                containersToAdd.add(containers[index.toDouble().toInt()])
            }

            Port(
                id = portsData.id,
                latitude = portsData.latitude,
                longitude = portsData.longitude,
                containersList = containersToAdd
            )
        }
        ports
    } catch (e: JsonSyntaxException) {
        println("[Error] Unable to create a port. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types.")
        null
    }
}
