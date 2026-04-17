
import model.CinemaConstants
import model.movie.Movie
import model.movie.MovieName
import model.movie.RunningTime
import model.schedule.MovieScreening
import model.time.CinemaTime
import model.time.CinemaTimeRange
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

data class MovieReservationDto(
    val movieName: String,
    val startTime: LocalDateTime,
    val seatName: String,
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

    fun getMovieScreeningId(
        movieId: Int,
        startTime: LocalDateTime,
    ): Int? {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(getMovieScreeningQuery(movieId, startTime))
            if (resultSet.next()) {
                return resultSet.getInt("id")
            }
        }
        return null
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

    fun insertMovieScreenings(vararg movieScreenings: MovieScreening) {
        for (movieScreening in movieScreenings) {
            val title = movieScreening.movie.getName()
            val runningTime = movieScreening.movie.runningTime.getMinutes()
            insertMovie(title, runningTime)

            val movieId = getMovieId(title) ?: continue
            val screenId = movieScreening.screenId
            val startTime = movieScreening.getMovieStartTime()

            connection.createStatement().use { statement ->
                if (statement.executeQuery(getMovieScreeningQuery(movieId, startTime)).next()) return@use
                statement.execute(insertMovieScreeningQuery(movieId, screenId, startTime))
            }
        }
    }

    fun getMovieId(title: String): Int? {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(getMovieQuery(title))
            if (!resultSet.next()) return null
            return resultSet.getInt("id")
        }
    }

    fun getAllMovieScreenings(): List<MovieScreening> {
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
                toDomain(movieEntity, screeningEntity)
            }
        }
    }

    fun getMovieScreeningById(id: Int): MovieScreening? {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM `movie_screenings` WHERE `id` = $id").use { rs ->
                if (!rs.next()) return null
                val screeningEntity =
                    MovieScreeningEntity(
                        id = rs.getInt("id"),
                        movieId = rs.getInt("movie_id"),
                        startTime = rs.getTimestamp("start_time").toLocalDateTime(),
                        screenId = rs.getInt("screen_id"),
                    )

                connection.createStatement().use { statement2 ->
                    statement2.executeQuery("SELECT * FROM `movies` WHERE `id` = ${screeningEntity.movieId}").use { movieRs ->
                        if (!movieRs.next()) return null
                        val movieEntity =
                            MovieEntity(
                                id = movieRs.getInt("id"),
                                title = movieRs.getString("title").trim(),
                                runningTime = movieRs.getInt("running_time"),
                            )

                        return toDomain(movieEntity, screeningEntity)
                    }
                }
            }
        }
    }

    fun getMovieById(id: Int): MovieEntity? {
        connection.createStatement().use { statement ->
            statement.executeQuery("SELECT * FROM `movies` WHERE `id` = $id").use { rs ->
                if (!rs.next()) return null
                return MovieEntity(
                    id = rs.getInt("id"),
                    title = rs.getString("title").trim(),
                    runningTime = rs.getInt("running_time"),
                )
            }
        }
    }

    fun insertMovieReservationBatch(screeningSeatMap: Map<Int, List<String>>): Int {
        val reservationId = getNextReservationId()
        connection.createStatement().use { statement ->
            for ((screeningId, seats) in screeningSeatMap) {
                for (seatName in seats) {
                    statement.execute(insertMovieReservationQuery(reservationId, screeningId, seatName))
                }
            }
        }
        return reservationId
    }

    fun isReservedSeatById(
        screeningId: Int,
        seatName: String,
    ): Boolean {
        val query = "SELECT 1 FROM `movie_reservations` WHERE `screening_id` = $screeningId AND `seat_name` = '$seatName' LIMIT 1"
        connection.createStatement().use { statement ->
            val rs = statement.executeQuery(query)
            return rs.next()
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

    private fun toDomain(
        movieEntity: MovieEntity,
        screeningEntity: MovieScreeningEntity,
    ): MovieScreening {
        val movie =
            Movie(
                name = MovieName(movieEntity.title.trim()),
                runningTime = RunningTime(movieEntity.runningTime),
            )
        val startTime = CinemaTime(screeningEntity.startTime)
        val endTime = startTime.plusMinutes(movieEntity.runningTime)
        val screenTime = CinemaTimeRange(startTime, endTime)
        return MovieScreening(
            screenId = screeningEntity.screenId,
            movie = movie,
            screenTime = screenTime,
            seatGroup = CinemaConstants.fixedSeatGroup,
        )
    }
}
