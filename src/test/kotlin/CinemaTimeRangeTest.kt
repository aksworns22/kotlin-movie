import model.time.CinemaTime
import model.time.CinemaTimeRange
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime

class CinemaTimeRangeTest {
    @ParameterizedTest
    @MethodSource("invalidDuration")
    fun `시작 시간이 종료 시간보다 느린 시간이라면 예외가 발생한다`(
        start: LocalDateTime,
        end: LocalDateTime,
    ) {
        assertThatThrownBy {
            CinemaTimeRange(CinemaTime(start), CinemaTime(end))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `시작 시간이 종료 시간과 같은 시간이라면 예외가 발생한다`() {
        assertThatThrownBy {
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2026, 5, 7, 9, 30)),
                CinemaTime(LocalDateTime.of(2026, 5, 7, 9, 30)),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `2025년 12월 25일 12시 30분은 2025년 12월 25일 12시 ~ 2025년 12월 25일 13시의 기간에 포함된다`() {
        assertThat(
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2025, 12, 25, 12, 0)),
                CinemaTime(LocalDateTime.of(2025, 12, 25, 13, 0)),
            ).contains(CinemaTime(LocalDateTime.of(2025, 12, 25, 12, 30))),
        ).isTrue
    }

    @Test
    fun `2025년 12월 25일 11시 30분은 2025년 12월 25일 12시 ~ 2025년 12월 25일 13시의 기간에 포함되지 않는다`() {
        assertThat(
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2025, 12, 25, 12, 0)),
                CinemaTime(LocalDateTime.of(2025, 12, 25, 13, 0)),
            ).contains(CinemaTime(LocalDateTime.of(2025, 12, 25, 11, 30))),
        ).isFalse
    }

    @Test
    fun `2025년 12월 25일 12시는 2025년 12월 25일 12시 ~ 13시의 기간에 포함된다`() {
        val startTime = CinemaTime(LocalDateTime.of(2025, 12, 25, 12, 0))
        assertThat(
            CinemaTimeRange(
                startTime,
                CinemaTime(LocalDateTime.of(2025, 12, 25, 13, 0)),
            ).contains(startTime),
        ).isTrue
    }

    @Test
    fun `2025년 12월 25일 13시는 2025년 12월 25일 12시 ~ 13시의 기간에 포함된다`() {
        val endTime = CinemaTime(LocalDateTime.of(2025, 12, 25, 13, 0))
        assertThat(
            CinemaTimeRange(
                CinemaTime(LocalDateTime.of(2025, 12, 25, 12, 0)),
                endTime,
            ).contains(endTime),
        ).isTrue
    }

    companion object {
        @JvmStatic
        fun invalidDuration(): List<Arguments> =
            listOf(
                Arguments.of(
                    LocalDateTime.of(2026, 5, 7, 9, 30),
                    LocalDateTime.of(2026, 4, 7, 9, 30),
                ),
                Arguments.of(
                    LocalDateTime.of(2026, 5, 7, 9, 30),
                    LocalDateTime.of(2026, 5, 6, 9, 30),
                ),
                Arguments.of(
                    LocalDateTime.of(2026, 5, 7, 9, 30),
                    LocalDateTime.of(2026, 5, 7, 8, 30),
                ),
                Arguments.of(
                    LocalDateTime.of(2026, 5, 7, 9, 30),
                    LocalDateTime.of(2026, 5, 7, 9, 0),
                ),
            )
    }
}
