package lab2

import kotlin.math.*

class Port(
    val id: Int,
    private val latitude: Double,
    private val longitude: Double
): IPort {
    var containersList = mutableListOf<Container>() //containers in port
    private var currentShipSet = mutableSetOf<Ship>() //keeps track of the ships currently here;
    private var historyShipSet = mutableSetOf<Ship>() //keeps track of every ship that has visited;

    override fun incomingShip(ship: Ship) {
        currentShipSet.add(ship)
    }

    override fun outgoingShip(ship: Ship) {
        historyShipSet.add(ship)
    }

    fun getDistance(other: Port): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}