package no.octopod.cinema.user.controller

import no.octopod.cinema.common.dto.UserDto
import no.octopod.cinema.user.repository.UserRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.hamcrest.Matchers.*
import org.springframework.test.context.ActiveProfiles

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("disable-auth")
class UserTest {

    @LocalServerPort private var port = 0
    @Autowired lateinit var userRepo: UserRepository

    @Before
    fun before() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        userRepo.deleteAll()

        given().get("/users")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGetSpecificUserInfo() {
        val originalUserDto = UserDto(phone = "1243234323", email = "test@test.xyz", name = "Test User")
        val userInfoPath = given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get(userInfoPath)
                .then()
                .statusCode(200)
                .body("data.phone", equalTo(originalUserDto.phone))
                .body("data.email", equalTo(originalUserDto.email))
                .body("data.name", equalTo(originalUserDto.name))
                .body("data.created", notNullValue())
                .body("data.updated", notNullValue())
    }

    @Test
    fun createFailsWhenInvalidContentSupplied() {
        val originalUserDto = UserDto()

        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.email = "test@test.xyz"
        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.email = null
        originalUserDto.name = "Test User"
        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.name = null
        originalUserDto.phone = "12341234"
        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)
    }

    @Test
    fun testCreateFailsWhenUserAlreadyExists() {
        val originalUserDto = UserDto(phone = "1243234323", email = "test@test.xyz", name = "Test User")
        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(201)

        given().contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetReturnsResourceNotFoundWhenInvalidId() {
        given().get("/users/1")
                .then()
                .statusCode(404)
                .body("code", equalTo(404))
                .body("message", equalTo("Resource not found"))
    }

    @Test
    fun testGetAll() {
        val userDto1 = UserDto(phone = "43214321", email = "test1@test1.xyz", name = "Test User 1")
        val userDto2 = UserDto(phone = "12341234", email = "test2@test2.xyz", name = "Test User 2")

        given().contentType(ContentType.JSON)
                .body(userDto1)
                .post("/users")
                .then()
                .statusCode(201)

        given().contentType(ContentType.JSON)
                .body(userDto2)
                .post("/users")
                .then()
                .statusCode(201)

        given().get("/users")
                .then()
                .statusCode(200)
                .body("data.count", equalTo(2))
                .body("data.data.phone", hasItems(userDto1.phone, userDto2.phone))
                .body("data.data.email", hasItems(userDto1.email, userDto2.email))
                .body("data.data.name", hasItems(userDto1.name, userDto2.name))
    }

    @Test
    fun testGetAllWithInvalidRequestParameters() {
       given().param("limit", 0)
               .get("/users")
               .then()
               .statusCode(400)

        given().param("page", 0)
                .get("/users")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetAllWithPaginationAndLimit() {
        val userDto1 = UserDto(phone = "43214321", email = "test1@test1.xyz", name = "Test User 1")
        val userDto2 = UserDto(phone = "12341234", email = "test2@test2.xyz", name = "Test User 2")

        given().contentType(ContentType.JSON)
                .body(userDto1)
                .post("/users")
                .then()
                .statusCode(201)

        given().contentType(ContentType.JSON)
                .body(userDto2)
                .post("/users")
                .then()
                .statusCode(201)

        val nextPage = given().param("limit", 1)
                .get("/users")
                .then()
                .body("data.count", equalTo(2))
                .body("data.pages", equalTo(2))
                .body("data.data.phone", hasItem(userDto1.phone))
                .body("data.data.phone", not(hasItem(userDto2.phone)))
                .body("data._links.next.href", notNullValue())
                .body("$", not(hasKey("data._links.previous")))
                .body("data._links.self.href", notNullValue())
                .extract().response().body().jsonPath()
                .getObject("data._links.next.href", String::class.java)

        given().get(nextPage)
                .then()
                .body("data.count", equalTo(2))
                .body("data.pages", equalTo(2))
                .body("data.data.phone", hasItem(userDto2.phone))
                .body("data.data.phone", not(hasItem(userDto1.phone)))
                .body("$", not(hasKey("data._links.next")))
                .body("data._links.previous.href", notNullValue())
                .body("data._links.self.href", notNullValue())
    }

    @Test
    fun testReplaceUserWithInvalidRequestBody() {
        val originalUserInfoDto = UserDto(phone = "43214321", email = "test1@test1.xyz", name = "Test User 1")
        val resourcePath = given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put("/users/invalidId")
                .then()
                .statusCode(409)

        originalUserInfoDto.email = null
        given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)

        originalUserInfoDto.email = "test1@test1.xyz"
        originalUserInfoDto.phone = null
        given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)

        originalUserInfoDto.phone = "43214321"
        originalUserInfoDto.name = null
        given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)
    }

    @Test
    fun testReplaceUserWithValidRequestBody() {
        val originalUserInfoDto = UserDto(phone = "43214321", email = "test1@test1.xyz", name = "Test User 1")
        val resourcePath = given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val userInfoDtoFromServer = given().get(resourcePath)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject("data", UserDto::class.java)


        userInfoDtoFromServer.email = "new@test1.xyz"
        userInfoDtoFromServer.name = "New Name"
        given().contentType(ContentType.JSON)
                .body(userInfoDtoFromServer)
                .put(resourcePath)
                .then()
                .statusCode(204)

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.phone", equalTo(originalUserInfoDto.phone))
                .body("data.email", equalTo(userInfoDtoFromServer.email))
                .body("data.name", equalTo(userInfoDtoFromServer.name))
                .body("data.updated", not(equalTo(userInfoDtoFromServer.created)))
    }

    @Test
    fun testReplaceUserCreatesNewResourceIfNotExists() {
        val nonExistingUserDto = UserDto(phone = "43214321", email = "test1@test1.xyz", name = "Test User 1")
        given().contentType(ContentType.JSON)
                .body(nonExistingUserDto)
                .post("/users")
                .then()
                .statusCode(201)
    }

    @Test
    fun testJsonMergePatchWithValidRequestBody() {
        val originalUserInfoDto = UserDto("12341234", email = "test1@test1.xyz", name = "Test User")
        val resourcePath = given().contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val newName = "new name"
        val newEmail = "email@email.xyz"
        val jsonBody = "{\"name\":\"$newName\", \"email\":\"$newEmail\"}"

        given().contentType("application/merge-patch+json")
                .body(jsonBody)
                .patch(resourcePath)
                .then()
                .statusCode(204)

        given().get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(newName))
                .body("data.phone", equalTo(originalUserInfoDto.phone))
                .body("data.email", equalTo(newEmail))
    }

    @Test
    fun testJsonMergePatchWithInvalidRequestBody() {
        val originalUserInfoDto = UserDto("12341234", email = "test1@test1.xyz", name = "Test User")
        val resourcePath = given().contentType("application/merge-patch+json")
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")


        given().contentType("application/merge-patch+json")
                .body("{\"name\":\"new name\"}")
                .patch("$resourcePath-invalid")
                .then()
                .statusCode(404)

        given().contentType("application/merge-patch+json")
                .body("{")
                .patch(resourcePath)
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"phone\":\"1234\"}")
                .patch(resourcePath)
                .then()
                .statusCode(409)

        given().contentType("application/merge-patch+json")
                .body("{\"name\":null}")
                .patch(resourcePath)
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"email\":null}")
                .patch(resourcePath)
                .then()
                .statusCode(400)
    }
}