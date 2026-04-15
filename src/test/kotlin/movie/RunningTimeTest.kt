package movie

import model.movie.RunningTime
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class RunningTimeTest {
    @Test
    fun `러닝 타임이 0 이하일 경우 예외를 던진다`() {
        assertThatThrownBy {
            RunningTime(-1)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `러닝 타임이 양수라면 예외를 던지지 않는다`() {
        assertThatCode {
            RunningTime(100)
        }.doesNotThrowAnyException()
    }

    @Test
    fun `러닝 타임의 값이 서로 같다면 동등하다`() {
        assertThat(RunningTime(100)).isEqualTo(RunningTime(100))
    }
}
