package no.octopod.cinema.e2etests

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.awaitility.Awaitility.await
import org.junit.*
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.security.cert.CertPath
import java.util.concurrent.TimeUnit
import java.time.ZonedDateTime


class E2EDockerIT {

    companion object {

        @BeforeClass @JvmStatic
        fun checkEnvironment() {

            val travis = System.getProperty("TRAVIS") != null
            Assume.assumeTrue(!travis)
        }

        class KDockerComposeContainer(path: File): DockerComposeContainer<KDockerComposeContainer>(path)

        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)

        private var counter = System.currentTimeMillis()

        @BeforeClass
        @JvmStatic
        fun initialize() {

            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 80
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            await()
                    .atMost(300, TimeUnit.SECONDS)
                    .pollInterval(6, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {

                        given()
                                .get("http://localhost:80/user")
                                .then()
                                .statusCode(401)

                        given()
                                .get("http://localhost:80/shows")
                                .then()
                                .statusCode(200)

                        true

                    }
        }
    }

    @Test
    fun testUnauthorizedAccess() {

        given().get("/user")
                .then()
                .statusCode(401)
    }
}