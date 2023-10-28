package lab2

interface IPort {
    fun incomingShip(ship: Ship)
    fun outgoingShip(ship: Ship)
}