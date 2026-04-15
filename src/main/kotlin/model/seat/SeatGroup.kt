package model.seat

class SeatGroup(
    seats: List<Seat>,
) : Iterable<Seat> {
    private val seats = seats.toList()
    val size: Int = seats.size

    init {
        require(seats.distinct().size == seats.size) { "중복된 좌석은 존재할 수 없습니다." }
    }

    override fun iterator(): Iterator<Seat> = seats.iterator()

    operator fun get(seatPosition: SeatPosition): Seat {
        val seat = seats.first { it.isEqual(seatPosition) }
        return seat
    }

    fun getAllSeatNames(): List<String> =
        seats.map { seat ->
            seat.getName()
        }
}
