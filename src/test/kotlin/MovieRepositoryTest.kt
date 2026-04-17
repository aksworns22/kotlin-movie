import dto.MovieScreeningDto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
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

                then("ID로 영화 상영 정보를 가져오면 저장된 정보와 일치한다") {
                    val movieId = movieRepository.getMovieId("고양이랑사는남자")!!
                    val screeningId = movieRepository.getMovieScreeningId(movieId, LocalDateTime.of(2026, 4, 16, 19, 0))!!
                    val screening = movieRepository.getMovieScreeningById(screeningId)
                    screening?.title shouldBe "고양이랑사는남자"
                    screening?.screenId shouldBe 1
                    screening?.runningTime shouldBe 60
                }

                then("ID로 영화 정보를 가져오면 저장된 정보와 일치한다") {
                    val movieId = movieRepository.getMovieId("고양이랑사는남자")!!
                    val movie = movieRepository.getMovieById(movieId)
                    movie?.title shouldBe "고양이랑사는남자"
                    movie?.runningTime shouldBe 60
                }
            }

            `when`("여러 좌석 예약을 한 번에 저장한다") {
                val movieId = movieRepository.getMovieId("고양이랑사는남자")!!
                val screeningId = movieRepository.getMovieScreeningId(movieId, LocalDateTime.of(2026, 4, 16, 19, 0))!!
                val reservationId = movieRepository.insertMovieReservationBatch(
                    mapOf(screeningId to listOf("C2", "C3"))
                )

                then("예약된 좌석은 isReservedSeatById가 true를 반환한다") {
                    movieRepository.isReservedSeatById(screeningId, "C2") shouldBe true
                    movieRepository.isReservedSeatById(screeningId, "C3") shouldBe true
                    movieRepository.isReservedSeatById(screeningId, "C4") shouldBe false
                }
            }
        }
    })
