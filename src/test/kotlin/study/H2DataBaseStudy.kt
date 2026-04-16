package study

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

class H2DataBaseStudy {
    private lateinit var dbConnection: Connection

    @TempDir
    lateinit var tempDir: Path

    @BeforeEach
    fun setUp() {
        dbConnection = DriverManager.getConnection("jdbc:h2:mem:test")
        dbConnection
            .createStatement()
            .use { it.execute("CREATE TABLE `user`(`id` INTEGER PRIMARY KEY, `name` VARCHAR(255))") }
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
        statement.close()
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
        statement.close()
    }

    @Test
    fun `User 테이블에 id_2, name_Miles를 삭제할 수 있다`() {
        // given
        val statement = dbConnection.createStatement()
        statement.execute("INSERT INTO `user`(`id`, `name`) VALUES (2, 'Miles')")

        // when
        statement.execute("DELETE FROM `user` WHERE `id` = 2")
        val resultSet = statement.executeQuery("SELECT * FROM `user` WHERE `id` = 2")

        // then
        resultSet.next() shouldBe false
        statement.close()
    }

    @Test
    fun `데이터베이스가 실제로 파일로 만들어지고 SQL을 통해 읽고 쓰기가 가능하다`() {
        // given
        val dbPath = tempDir.resolve("test-db")
        val connection = DriverManager.getConnection("jdbc:h2:$dbPath")
        val statement = connection.createStatement()
        statement.execute("CREATE TABLE `user`(`id` INTEGER PRIMARY KEY, `name` VARCHAR(255))")

        // when
        statement.execute("INSERT INTO `user`(`id`, `name`) VALUES (1, 'NoseKnee')")
        val resultSet = statement.executeQuery("SELECT * FROM `user` WHERE `id` = 1")

        // then
        while (resultSet.next()) {
            resultSet.getString("name") shouldBe "NoseKnee"
        }
        statement.close()
        connection.close()
    }
}
