package movie

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MovieTest {
    @ParameterizedTest
    @CsvSource("movie1,10,movie1,20", "movie1,10,movie2,10")
    fun `이름과 러닝 타임 중 하나라도 다르다면 다른 영화로 판단한다`(
        movieName1: String,
        runningTime1: Int,
        movieName2: String,
        runningTime2: Int,
    ) {
        Movie(MovieName(movieName1), RunningTime(runningTime1)) shouldNotBe
            Movie(MovieName(movieName2), RunningTime(runningTime2))
    }

    @Test
    fun `러닝 타임과 이름이 같으면 동일한 영화로 판단한다`() {
        Movie(MovieName("자취하는남자"), RunningTime(60)) shouldBe
            Movie(MovieName("자취하는남자"), RunningTime(60))
    }
}
