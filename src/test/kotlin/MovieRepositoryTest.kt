import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import java.time.LocalDateTime

class MovieRepositoryTest :
    BehaviorSpec({
        given("아무것도 저장된 정보가 없는 MovieRepository가 주어진다") {
            val movieRepository = MovieRepository("mem:testMovieRepository")
            `when`("4월 16일 19시에 상영하는 고양이랑사는남자를 MovieRepository에 저장한다") {
                movieRepository.insertMovieScreenings(
                    MovieScreeningDto(
                        title = "고양이랑사는남자",
                        screenId = 1,
                        runningTime = 60,
                        startTime = LocalDateTime.of(2026, 4, 16, 19, 0),
                    ),
                )
                then("모든 영화를 가져오면 4월 16일 19시에 상영하는 고양이랑사는남자가 포함되어있다.") {
                    movieRepository.getAllMovieScreenings() shouldContain
                        MovieScreeningDto(
                            title = "고양이랑사는남자",
                            screenId = 1,
                            runningTime = 60,
                            startTime = LocalDateTime.of(2026, 4, 16, 19, 0),
                        )
                }
            }
        }
    })
