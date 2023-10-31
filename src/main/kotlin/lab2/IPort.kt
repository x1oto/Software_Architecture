package lab2

interface IPort {
    fun incomingShip(s: Ship)
    fun outgoingShip(s: Ship)
}