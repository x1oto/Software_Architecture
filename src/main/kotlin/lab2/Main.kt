package lab2

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import java.io.File

data class PortData(
    val id: Int,
    val latitude: Double,
    val longitude: Double
)

data class ActionData(
    val action: String,
    val containers: List<Int>,
    val newFuel: Double,
    val port: Int
)

data class ContainerData(
    val id: Int,
    val weight: Int,
    val type: String
)

data class ShipSpecificationsData(
    val id: Int,
    val fuel: Double,
    val port: Int,
    val totalWeightCapacity: Int,
    val maxNumberOfAllContainers: Int,
    val maxNumberOfBasicContainers: Int,
    val maxNumberOfHeavyContainers: Int,
    val maxNumberOfRefrigeratedContainers: Int,
    val maxNumberOfLiquidContainers: Int,
    val fuelConsumptionPerKM: Double
)

data class ShipActionResult(
    val massage: String
)

fun main() {
    val actionsResults = mutableListOf<ShipActionResult>()

    val (jsonString, gson, jsonObject) = loadJsonAndCreateGsonMap()

    val ports = getPorts(jsonObject, gson, actionsResults)
    val containers = getContainers(jsonObject, gson, actionsResults)
    val ship = ports?.let { getShip(gson, jsonString, it, actionsResults) }


    if (ship != null && containers != null) {
        loadAndExecuteActionsFromJson(jsonObject, gson, ship, containers, ports, actionsResults)
    }
    createJsonBasedOnActions(gson, actionsResults)
}

private fun createJsonBasedOnActions(
    gson: Gson,
    actionsResults: MutableList<ShipActionResult>
) {
    val actionsResultsJSON = gson.toJson(actionsResults)
    File("src/main/kotlin/lab2/Output.json").writeText(actionsResultsJSON)
}

private fun loadJsonAndCreateGsonMap(): Triple<String, Gson, Map<*, *>> {
    val jsonString = File("src/main/kotlin/lab2/Input.json").readText()
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonString, Map::class.java)
    return Triple(jsonString, gson, jsonObject)
}

private fun loadAndExecuteActionsFromJson(
    jsonObject: Map<*, *>,
    gson: Gson,
    ship: Ship,
    containers: List<Container>,
    ports: List<Port>,
    actionsResults: MutableList<ShipActionResult>
) {
    try {
        (jsonObject["actions"] as List<*>).map {
            val actionData = gson.fromJson(gson.toJson(it), ActionData::class.java)

            when (actionData.action) {
                "load" -> executeLoadAction(actionData, ship, containers, actionsResults)
                "unload" -> executeUnloadAction(actionData, ship, containers, actionsResults)
                "sail" -> executeSailAction(ship, ports, actionData, actionsResults)
                "refuel" -> executeRefuelAction(ship, actionData, actionsResults)
                else -> actionsResults.add(ShipActionResult("[Error] Invalid action in Input.json/\"actions\". " +
                        "Must be one of the following: load, unload, sail, refuel. Please check your json file and try again."))
            }
            actionsResults.add(ShipActionResult("Current containers on ship: ${ship.getCurrentContainers()}"))
        }
    } catch (e: JsonSyntaxException) {
        actionsResults.add(ShipActionResult("[Error] Unable to perform the action. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types."))
    }
}

private fun executeLoadAction(
    actionData: ActionData,
    ship: Ship,
    containers: List<Container>,
    actionsResults: MutableList<ShipActionResult>
) {
    for (index in actionData.containers) {
        if (index >= 0 && index < containers.size) {
            val isSuccess = ship.load(containers[index])
            actionsResults.add(ShipActionResult("Trying to load ${containers[index]} on ship ${ship.id}... " +
                    "Result: $isSuccess"))
        } else {
            actionsResults.add(ShipActionResult("[Error] Incorrect container index or missing container. Please fill in your JSON correctly!"))
        }
    }
}

private fun executeUnloadAction(
    actionData: ActionData,
    ship: Ship,
    containers: List<Container>,
    actionsResults: MutableList<ShipActionResult>
) {
    for (index in actionData.containers) {
        if (index >= 0 && index < containers.size) {
            val isSuccess = ship.unLoad(containers[index])
            actionsResults.add(ShipActionResult("Trying to unload ${containers[index]} from ship ${ship.id}... " +
                    "Result: $isSuccess"))
        } else {
            actionsResults.add(ShipActionResult("[Error] Incorrect container index or missing container. Please fill in your JSON correctly!"))
        }
    }
}

private fun executeSailAction(
    ship: Ship,
    ports: List<Port>,
    actionData: ActionData,
    actionsResults: MutableList<ShipActionResult>
) {
    val isSuccess = ship.sailTo(ports[actionData.port])
    if(isSuccess) ship.changeCurrentPort(ports[actionData.port])

    actionsResults.add(ShipActionResult("Sail to next port... Result: $isSuccess"))
}

private fun executeRefuelAction(
    ship: Ship,
    actionData: ActionData,
    actionsResults: MutableList<ShipActionResult>
) {
    ship.reFuel(actionData.newFuel)
    actionsResults.add(ShipActionResult("Ship refueled successfully."))
}

private fun getShip(
    gson: Gson,
    jsonString: String,
    ports: List<Port>,
    actionsResults: MutableList<ShipActionResult>
): Ship? {
    return try {
        val shipJsonObject = gson.fromJson(jsonString, JsonObject::class.java)
        val shipSpecifications =
            gson.fromJson(shipJsonObject.getAsJsonObject("shipSpecs"), ShipSpecificationsData::class.java)

        val shipSpecificationsData = with(shipSpecifications) {
            ShipSpecifications(
                id = id,
                fuel = fuel,
                currentPort = ports[port],
                totalWeightCapacity = totalWeightCapacity,
                maxNumberOfAllContainers = maxNumberOfAllContainers,
                maxNumberOfBasicContainers = maxNumberOfBasicContainers,
                maxNumberOfHeavyContainers = maxNumberOfHeavyContainers,
                maxNumberOfRefrigeratedContainers = maxNumberOfRefrigeratedContainers,
                maxNumberOfLiquidContainers = maxNumberOfLiquidContainers,
                fuelConsumptionPerKM = fuelConsumptionPerKM
            )
        }
        Ship(shipSpecificationsData)
    } catch (e: JsonSyntaxException) {
        actionsResults.add(ShipActionResult("[Error] Unable to create a ship. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types."))
        null
    }
}

private fun getContainers(
    jsonObject: Map<*, *>,
    gson: Gson,
    actionsResults: MutableList<ShipActionResult>
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
        actionsResults.add(ShipActionResult("[Error] Unable to create a container. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types."))
        null
    }
}

private fun getPorts(
    jsonObject: Map<*, *>,
    gson: Gson,
    actionsResults: MutableList<ShipActionResult>
): List<Port>? {
    return try {
        val ports = (jsonObject["ports"] as List<*>).map {
            val portsData = gson.fromJson(gson.toJson(it), PortData::class.java)
            Port(
                id = portsData.id,
                latitude = portsData.latitude,
                longitude = portsData.longitude
            )
        }
        ports
    } catch (e: JsonSyntaxException) {
        actionsResults.add(ShipActionResult("[Error] Unable to create a port. Json structure not followed" +
                " Please make sure the names are correct and there is no mistake with the data types."))
        null
    }
}