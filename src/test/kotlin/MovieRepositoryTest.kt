import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import model.CinemaConstants
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieScreening
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.time.LocalDateTime

class MovieRepositoryTest :
    BehaviorSpec({
        given("아무것도 저장된 정보가 없는 MovieRepository가 주어진다") {
            val movieRepository = MovieRepository("mem:testMovieRepository")
            `when`("4월 16일 19시에 상영하는 고양이랑사는남자를 MovieRepository에 저장한다") {
                val movie = Movie(MovieName("고양이랑사는남자"), RunningTime(60))
                val startTime = LocalDateTime.of(2026, 4, 16, 19, 0)
                val movieScreening = MovieScreening(
                    screenId = 1,
                    movie = movie,
                    screenTime = CinemaTimeRange(
                        start = CinemaTime(startTime),
                        end = CinemaTime(startTime).plusMinutes(60)
                    ),
                    seatGroup = CinemaConstants.fixedSeatGroup
                )

                movieRepository.insertMovieScreenings(movieScreening)

                then("모든 영화를 가져오면 4월 16일 19시에 상영하는 고양이랑사는남자가 포함되어있다.") {
                    movieRepository.getAllMovieScreenings() shouldContain movieScreening
                }

                then("ID로 영화 상영 정보를 가져오면 저장된 정보와 일치한다") {
                    val movieId = movieRepository.getMovieId("고양이랑사는남자")!!
                    val screeningId = movieRepository.getMovieScreeningId(movieId, startTime)!!
                    val screening = movieRepository.getMovieScreeningById(screeningId)!!
                    screening.movie.getName() shouldBe "고양이랑사는남자"
                    screening.screenId shouldBe 1
                    screening.movie.runningTime.getMinutes() shouldBe 60
                }

                then("ID로 영화 정보를 가져오면 저장된 정보와 일치한다") {
                    val movieId = movieRepository.getMovieId("고양이랑사는남자")!!
                    val movieEntity = movieRepository.getMovieById(movieId)
                    movieEntity?.title shouldBe "고양이랑사는남자"
                    movieEntity?.runningTime shouldBe 60
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
