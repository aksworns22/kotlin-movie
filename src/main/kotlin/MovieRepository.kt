import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime

data class MovieScreeningDTO(
    val title: String,
    val runningTime: Int,
    val startTime: LocalDateTime,
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
            PRIMARY KEY(`id`),
            CONSTRAINT `movie_id` FOREIGN KEY(`movie_id`) REFERENCES `movies`(`id`)
        );
        """.trimIndent()

    private val createMovieReservationQuery =
        """
        CREATE TABLE IF NOT EXISTS `movie_reservations`(
            `id` INTEGER AUTO_INCREMENT,
            `screening_id` INTEGER NOT NULL,
            `seat` CHAR(2) NOT NULL,
            PRIMARY KEY(`id`),
            CONSTRAINT `screen_id` FOREIGN KEY(`screening_id`) REFERENCES `movie_screenings`(`id`)
        );
        """.trimIndent()

    init {
        connection.createStatement().use { statement ->
            statement.execute("$createMoviesTableQuery $createMovieScreeningQuery $createMovieReservationQuery")
        }
    }

    private fun getMovieQuery(
        title: String,
        runningTime: Int,
    ): String = "SELECT * FROM `movies` WHERE `title` = '$title' AND `running_time` = '$runningTime'"

    private fun insertMovieQuery(
        title: String,
        runningTime: Int,
    ): String = "INSERT INTO `movies`(`title`, `running_time`) VALUES ('$title', $runningTime)"

    private fun getMovieScreeningQuery(
        movieId: Int,
        startTime: LocalDateTime,
    ): String = "SELECT `id` FROM `movie_screenings` WHERE `movie_id` = '$movieId' AND `start_time` = '$startTime'"

    private fun insertMovieScreeningQuery(
        movieId: Int,
        startTime: LocalDateTime,
    ): String = "INSERT INTO `movie_screenings` (`movie_id`, `start_time`) VALUES ($movieId, '$startTime')"

    private fun insertMovie(
        title: String,
        runningTime: Int,
    ) {
        connection.createStatement().use { statement ->
            if (statement.executeQuery(getMovieQuery(title, runningTime)).next()) return@use
            statement.execute(insertMovieQuery(title, runningTime))
        }
    }

    fun insertMovieScreenings(vararg movieScreenings: MovieScreeningDTO) {
        for (movieScreeningDTO in movieScreenings) {
            val (title, runningTime, startTime) = movieScreeningDTO
            connection.createStatement().use { statement ->
                insertMovie(title, runningTime)
                val movieId = findMovieId(title, runningTime) ?: continue
                if (findMovieScreeningId(movieId, startTime) != null) continue
                statement.execute(insertMovieScreeningQuery(movieId, startTime))
            }
        }
    }

    private fun findMovieId(
        title: String,
        runningTime: Int,
    ): Int? {
        connection.createStatement().use { statement ->
            val resultSet = statement.executeQuery(getMovieQuery(title, runningTime))
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
}
