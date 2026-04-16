import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

data class MovieScreeningDto(
    val title: String,
    val screenId: Int,
    val runningTime: Int,
    val startTime: LocalDateTime,
)

data class MovieScreeningEntity(
    val id: Int,
    val movieId: Int,
    val startTime: LocalDateTime,
    val screenId: Int,
)

data class MovieEntity(
    val id: Int,
    val title: String,
    val runningTime: Int,
)

data class MovieReservationDto(
    val movieName: String,
    val startTime: LocalDateTime,
    val seatName: String,
)

class MovieRepository(
    path: String,
) {
    private val connection: Connection = DriverManager.getConnection("jdbc:h2:$path")

    private val createMoviesTableQuery =
        """
        CREATE TABLE IF NOT EXISTS `movies`(
            `id` INTEGER AUTO_INCREMENT,
            `title` CHAR(255) NOT NULL,
            `running_time` INTEGER NOT NULL,
            PRIMARY KEY(`id`)
        );
        """.trimIndent()

    private val createMovieScreeningQuery =
        """
        CREATE TABLE IF NOT EXISTS `movie_screenings`(
            `id` INTEGER AUTO_INCREMENT,
            `movie_id` INTEGER NOT NULL,
            `start_time` TIMESTAMP NOT NULL,
            `screen_id` INTEGER NOT NULL,
            PRIMARY KEY(`id`),
            CONSTRAINT `movie_id` FOREIGN KEY(`movie_id`) REFERENCES `movies`(`id`)
        );
        """.trimIndent()

    private val createMovieReservationQuery =
        """
        CREATE TABLE IF NOT EXISTS `movie_reservations`(
            `id` INTEGER AUTO_INCREMENT,
            `reservation_id` INTEGER AUTO_INCREMENT,
            `seat_name` CHAR(2) NOT NULL,
            `screening_id` INTEGER NOT NULL,
            PRIMARY KEY(`id`),
            CONSTRAINT `screening_id` FOREIGN KEY(`screening_id`) REFERENCES `movie_screenings`(`id`)
        );
        """.trimIndent()

    init {
        connection.createStatement().use { statement ->
            statement.execute("$createMoviesTableQuery $createMovieScreeningQuery $createMovieReservationQuery")
        }
    }

    private fun getMovieQuery(title: String): String = "SELECT * FROM `movies` WHERE `title` = '$title'"

    private fun getMovieQuery(): String = "SELECT * FROM `movies`"

    private fun insertMovieQuery(
        title: String,
        runningTime: Int,
    ): String = "INSERT INTO `movies`(`title`, `running_time`) VALUES ('$title', $runningTime)"

    private fun getMovieScreeningQuery(
        movieId: Int,
        startTime: LocalDateTime,
    ): String = "SELECT * FROM `movie_screenings` WHERE `movie_id` = '$movieId' AND `start_time` = '$startTime'"

    private fun insertMovieReservationQuery(
        reservationId: Int,
        screeningId: Int,
        seatName: String,
    ): String =
        "INSERT INTO `movie_reservations`(`reservation_id`, `screening_id`, `seat_name`) VALUES ($reservationId, $screeningId, '$seatName')"

    private fun getMovieScreeningQuery(): String = "SELECT * FROM `movie_screenings`"

    private fun insertMovieScreeningQuery(
        movieId: Int,
        screenId: Int,
        startTime: LocalDateTime,
    ): String = "INSERT INTO `movie_screenings` (`movie_id`, `screen_id`, `start_time`) VALUES ($movieId, $screenId, '$startTime')"

    private fun getNextReservationId(): Int {
        connection.createStatement().use { statement ->
            val resultSet =
                statement.executeQuery("SELECT COALESCE(MAX(reservation_id) + 1, 0) AS `next_id` FROM `movie_reservations`")
            resultSet.next()
            return resultSet.getInt("next_id")
        }
    }

    private fun insertMovie(
        title: String,
        runningTime: Int,
    ) {
        connection.createStatement().use { statement ->
            if (statement.executeQuery(getMovieQuery(title)).next()) return@use
            statement.execute(insertMovieQuery(title, runningTime))
        }
    }

    fun insertMovieReservation(vararg movieReservationDtoGroup: MovieReservationDto) {
        val reservationId = getNextReservationId()
        for (movieReservationDto in movieReservationDtoGroup) {
            val (movieName, startTime, seatName) = movieReservationDto
            val movieId = getMovieId(movieName) ?: continue
            var movieScreeningId: Int? = null
            connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery(getMovieScreeningQuery(movieId, startTime))
                if (resultSet.next()) {
                    movieScreeningId = resultSet.getInt("id")
                }
            }
            println("$movieScreeningId, $reservationId, $seatName")
            connection.createStatement().use { statement ->
                if (movieScreeningId != null) {
                    statement.execute(insertMovieReservationQuery(reservationId, movieScreeningId, seatName))
                }
            }
        }
    }

    fun insertMovieScreenings(vararg movieScreenings: MovieScreeningDto) {
        for (movieScreeningDTO in movieScreenings) {
            val (title, screenId, runningTime, startTime) = movieScreeningDTO
            connection.createStatement().use { statement ->
                insertMovie(title, runningTime)
                val movieId = getMovieId(title) ?: continue
                if (findMovieScreeningId(movieId, startTime) != null) continue
                statement.execute(insertMovieScreeningQuery(movieId, screenId, startTime))
            }
        }
    }

    private fun getMovieId(title: String): Int? {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(getMovieQuery(title))
            if (!resultSet.next()) return null
            return resultSet.getInt("id")
        }
    }

    private fun findMovieScreeningId(
        movieId: Int,
        startTime: LocalDateTime,
    ): Int? {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(getMovieScreeningQuery(movieId, startTime))
            if (!resultSet.next()) return null
            return resultSet.getInt("id")
        }
    }

    fun getAllMovieScreenings(): List<MovieScreeningDto> {
        connection.createStatement().use { statement ->
            val movieResultSet = statement.executeQuery(getMovieQuery())
            val movies = mutableListOf<MovieEntity>()
            while (movieResultSet.next()) {
                movies.add(
                    MovieEntity(
                        id = movieResultSet.getInt("id"),
                        title = movieResultSet.getString("title"),
                        runningTime = movieResultSet.getInt("running_time"),
                    ),
                )
            }
            val screenings = mutableListOf<MovieScreeningEntity>()
            val screeningResultSet = statement.executeQuery(getMovieScreeningQuery())
            while (screeningResultSet.next()) {
                screenings.add(
                    MovieScreeningEntity(
                        id = screeningResultSet.getInt("id"),
                        movieId = screeningResultSet.getInt("movie_id"),
                        startTime = screeningResultSet.getTimestamp("start_time").toLocalDateTime(),
                        screenId = screeningResultSet.getInt("screen_id"),
                    ),
                )
            }
            return screenings.map { screeningEntity ->
                val movieEntity = movies.first { it.id == screeningEntity.movieId }
                MovieScreeningDto(
                    title = movieEntity.title.trim(),
                    runningTime = movieEntity.runningTime,
                    startTime = screeningEntity.startTime,
                    screenId = screeningEntity.screenId,
                )
            }
        }
    }

    fun isReservedSeat(
        movieName: String,
        startTime: LocalDateTime,
        seatName: String,
    ): Boolean {
        val query =
            """
            SELECT 1
            FROM movie_reservations mr
            JOIN movie_screenings ms ON mr.screening_id = ms.id
            JOIN movies m ON ms.movie_id = m.id
            WHERE m.title = '$movieName'
              AND ms.start_time = '$startTime'
              AND mr.seat_name = '$seatName'
            LIMIT 1
            """.trimIndent()

        connection.createStatement().use { statement ->
            val rs = statement.executeQuery(query)
            return rs.next()
        }
    }
}
