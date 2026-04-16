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
        dbConnection = DriverManager.getConnection("jdbc:h2:mem:test")
        dbConnection.createStatement().execute("CREATE TABLE `user`(`id` INTEGER PRIMARY KEY, `name` VARCHAR(255))")
    }

    @AfterEach
    fun tearDown() {
        dbConnection.close()
    }

    @Test
    fun `User 테이블을 만든 후 (id_1, name_NoseKnee)를 실제로 저장하고 꺼내올 수 있다`() {
        // given
        val statement = dbConnection.createStatement()
        statement.execute("INSERT INTO `user`(`id`, `name`) VALUES (1, 'NoseKnee')")

        // when
        val resultSet = statement.executeQuery("SELECT * FROM `user` WHERE `id` = 1")

        // then
        while (resultSet.next()) {
            resultSet.getString("name") shouldBe "NoseKnee"
        }
    }

    @Test
    fun `User 테이블에 id_1, name_NoseKnee를 id_1, name_Koni로 바꿀 수 있다`() {
        // given
        val statement = dbConnection.createStatement()
        statement.execute("INSERT INTO `user`(`id`, `name`) VALUES (1, 'NoseKnee')")

        // when
        statement.execute("UPDATE `user` SET `name` = 'Koni' WHERE `id` = 1")
        val resultSet = statement.executeQuery("SELECT * FROM `user` WHERE `id` = 1")

        // then
        while (resultSet.next()) {
            resultSet.getString("name") shouldBe "Koni"
        }
    }
}
