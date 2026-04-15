package model.time

import java.util.Objects

class CinemaTimeRange(
    private val start: CinemaTime,
    private val end: CinemaTime,
) : Comparable<CinemaTimeRange> {
    val durationMinute = start.minuteUntil(end)

    init {
        require(start.isBefore(end)) { "시작 시간이 종료 시간보다 늦을 수 없습니다" }
    }

    override fun equals(other: Any?): Boolean {
        if (other is CinemaTimeRange) {
            return start.isEqual(other.start) && end.isEqual(other.end)
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(start, end)

    override fun compareTo(other: CinemaTimeRange): Int {
        val startTimeCompareResult = start.compareTo(other.start)
        if (startTimeCompareResult == 0) {
            return end.compareTo(other.end)
        }
        return startTimeCompareResult
    }

    fun contains(time: CinemaTime): Boolean {
        if (time.isEqual(start) || time.isEqual(end)) return true
        return time.isAfter(start) && time.isBefore(end)
    }

    fun contains(other: CinemaTimeRange): Boolean = !start.isAfter(other.start) && !end.isBefore(other.end)

    fun isEqual(other: CinemaTimeRange): Boolean = start.isEqual(other.start) && end.isEqual(other.end)

    fun isStartEqual(cinemaTime: CinemaTime): Boolean = start.isEqual(cinemaTime)

    fun overlaps(timeRange: CinemaTimeRange): Boolean = !start.isAfter(timeRange.end) && !timeRange.start.isAfter(end)

    fun isSameStartDate(cinemaTime: CinemaTime): Boolean = start.isEqualDate(cinemaTime)

    fun isBeforeStartHour(hour: Int): Boolean = start.isBeforeHour(hour)

    fun isSameStartDayOfMonth(dayOfMonth: Int): Boolean = start.isSameDay(dayOfMonth)
}
