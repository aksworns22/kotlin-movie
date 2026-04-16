package study

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Connection
import java.sql.DriverManager

class H2DataBaseStudy {
    private lateinit var dbConnection: Connection

    @BeforeEach
    fun setUp() {
        dbConnection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        dbConnection.createStatement().execute("CREATE TABLE `test`(`id` INTEGER PRIMARY KEY, `name` VARCHAR(255))")
    }

    @AfterEach
    fun tearDown() {
        dbConnection.close()
    }

    @Test
    fun `User 테이블을 만든 후 (id_1, name_NoseKnee)를 실제로 저장하고 꺼내올 수 있다`() {
        val statement = dbConnection.createStatement()
        statement.execute("INSERT INTO `test`(`id`, `name`) VALUES (1, 'NoseKnee')")
        val resultSet = statement.executeQuery("SELECT * FROM `test` WHERE `id` = 1")
        while (resultSet.next()) {
            resultSet.getString("name") shouldBe "NoseKnee"
        }
        println(resultSet)
    }
}
