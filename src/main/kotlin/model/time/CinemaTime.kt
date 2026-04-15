package model.time

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JvmInline
value class CinemaTime(
    private val time: LocalDateTime,
) : Comparable<CinemaTime> {
    fun isBefore(other: CinemaTime): Boolean = time.isBefore(other.time)

    fun isBeforeHour(hour: Int): Boolean = time.hour < hour

    fun isAfter(other: CinemaTime): Boolean = time.isAfter(other.time)

    fun isEqual(other: CinemaTime): Boolean = time.isEqual(other.time)

    fun isEqualDate(other: CinemaTime): Boolean = time.toLocalDate().isEqual(other.time.toLocalDate())

    fun format(pattern: String): String = time.format(DateTimeFormatter.ofPattern(pattern))

    fun minuteUntil(other: CinemaTime): Int = Duration.between(time, other.time).toMinutes().toInt()

    override fun compareTo(other: CinemaTime): Int = time.compareTo(other.time)

    fun isSameDay(dayOfMonth: Int): Boolean = dayOfMonth == time.dayOfMonth

    fun toLocalDateTime(): LocalDateTime = time
}
