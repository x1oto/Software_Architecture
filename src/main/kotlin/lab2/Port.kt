package lab2

import kotlin.math.*

class Port(
    val id: Int,
    private val latitude: Double,
    private val longitude: Double
): IPort {
    var containersList = mutableListOf<Container>() //containers in port
    private var current = mutableListOf<Ship>() //keeps track of the ships currently here;
    private var history = mutableListOf<Ship>() //keeps track of every ship that has visited;

    override fun incomingShip(s: Ship) {
        if (s !in history) current.add(s)
    }

    override fun outgoingShip(s: Ship) {
        if (s !in history) history.add(s)
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