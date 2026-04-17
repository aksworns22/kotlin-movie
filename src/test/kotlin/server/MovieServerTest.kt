package server

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.client.RestTestClient
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MovieServerTest(
    @param:LocalServerPort private val port: Int,
) {
    private lateinit var client: RestTestClient

    @BeforeEach
    fun setUp() {
        client =
            RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:$port")
                .build()
    }

    @Test
    fun `기본 저장된 영화 목록 전체를 조회한다`() {
        client
            .get()
            .uri("/api/movies")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody(object : ParameterizedTypeReference<List<MovieDto>>() {})
            .isEqualTo(
                listOf(
                    MovieDto(
                        id = 1,
                        title = "혼자사는남자",
                        runningTimeMinutes = 60,
                        screenings =
                            listOf(
                                MovieScreeningDto(
                                    id = 1,
                                    screenId = 1,
                                    startAt = LocalDateTime.parse("2026-04-08T10:00:00"),
                                    endAt = LocalDateTime.parse("2026-04-08T11:00:00"),
                                ),
                                MovieScreeningDto(
                                    id = 3,
                                    screenId = 3,
                                    startAt = LocalDateTime.parse("2026-04-10T20:00:00"),
                                    endAt = LocalDateTime.parse("2026-04-10T21:00:00"),
                                ),
                            ),
                    ),
                    MovieDto(
                        id = 2,
                        title = "아이언맨",
                        runningTimeMinutes = 60,
                        screenings =
                            listOf(
                                MovieScreeningDto(
                                    id = 2,
                                    screenId = 2,
                                    startAt = LocalDateTime.parse("2026-04-09T07:00:00"),
                                    endAt = LocalDateTime.parse("2026-04-09T08:00:00"),
                                ),
                            ),
                    ),
                ),
            )
    }
}
