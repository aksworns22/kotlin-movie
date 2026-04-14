package model.time

data class CinemaTimeRange(
    val start: CinemaTime,
    val end: CinemaTime,
) : Comparable<CinemaTimeRange> {
    init {
        require(start.isBefore(end)) { "시작 시간이 종료 시간보다 늦을 수 없습니다" }
    }

    val durationMinute = start.minuteUntil(end)

    fun contains(time: CinemaTime): Boolean {
        if (time.isEqual(start) || time.isEqual(end)) return true
        return time.isAfter(start) && time.isBefore(end)
    }

    fun overlaps(timeRange: CinemaTimeRange): Boolean = !start.isAfter(timeRange.end) && !timeRange.start.isAfter(end)

    override fun compareTo(other: CinemaTimeRange): Int {
        val startTimeCompareResult = start.compareTo(other.start)
        if (startTimeCompareResult == 0) {
            return end.compareTo(other.end)
        }
        return startTimeCompareResult
    }
}
