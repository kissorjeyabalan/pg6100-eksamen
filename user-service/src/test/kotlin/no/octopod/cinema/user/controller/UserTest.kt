package no.octopod.cinema.user.controller

import no.octopod.cinema.common.dto.UserInfoDto
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
@ActiveProfiles("test")
class UserTest {

    @LocalServerPort private var port = 0
    @Autowired lateinit var userRepo: UserRepository

    @Before
    fun before() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        userRepo.deleteAll()

        given().auth().basic("admin", "admin").get("/users")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGetSpecificUserInfo() {
        val phone = "12345678"
        val originalUserDto = UserInfoDto(phone = phone, email = "test@test.xyz", name = "Test User")

        val userInfoPath = given().auth()
                .basic(phone, "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth()
                .basic(phone, "123")
                .get(userInfoPath)
                .then()
                .statusCode(200)
                .body("data.phone", equalTo(originalUserDto.phone))
                .body("data.email", equalTo(originalUserDto.email))
                .body("data.name", equalTo(originalUserDto.name))
                .body("data.created", notNullValue())
                .body("data.updated", notNullValue())
    }

    @Test
    fun testDelete() {
        val originalUserDto = UserInfoDto(phone = "12345678", email = "test@test.xyz", name = "Test User")

        val userInfoPath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth()
                .basic("admin", "admin")
                .delete(userInfoPath)
                .then()
                .statusCode(204)

        given().auth()
                .basic("admin", "admin")
                .delete(userInfoPath)
                .then()
                .statusCode(404)
    }
    @Test
    fun createFailsWhenInvalidContentSupplied() {
        val originalUserDto = UserInfoDto()

        given().auth()
                .basic("12345678", "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.email = "test@test.xyz"
        given().auth()
                .basic("12345678", "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.email = null
        originalUserDto.name = "Test User"
        given().auth()
                .basic("12345678", "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)

        originalUserDto.name = null
        originalUserDto.phone = "12341234"
        given().auth()
                .basic("12345678", "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(400)
    }

    @Test
    fun testCreateFailsWhenUserAlreadyExists() {
        val phone = "12345678"
        val originalUserDto = UserInfoDto(phone = phone, email = "test@test.xyz", name = "Test User")

        given().auth()
                .basic(phone, "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic(phone, "123")
                .contentType(ContentType.JSON)
                .body(originalUserDto)
                .post("/users")
                .then()
                .statusCode(409)
    }

    @Test
    fun testGetReturnsResourceNotFoundWhenInvalidId() {
        given().auth()
                .basic("admin", "admin")
                .get("/users/1")
                .then()
                .statusCode(404)
                .body("code", equalTo(404))
    }

    @Test
    fun testGetAll() {
        val userDto1 = UserInfoDto(phone = "12345678", email = "test1@test1.xyz", name = "Test User 1")
        val userDto2 = UserInfoDto(phone = "87654321", email = "test2@test2.xyz", name = "Test User 2")

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userDto1)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userDto2)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.count", equalTo(2))
                .body("data.data.phone", hasItems(userDto1.phone, userDto2.phone))
                .body("data.data.email", hasItems(userDto1.email, userDto2.email))
                .body("data.data.name", hasItems(userDto1.name, userDto2.name))
    }

    @Test
    fun testGetAllWithInvalidRequestParameters() {
       given().auth()
               .basic("admin", "admin")
               .param("limit", 0)
               .get("/users")
               .then()
               .statusCode(400)

        given().auth()
                .basic("admin", "admin")
                .param("page", 0)
                .get("/users")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetAllWithPaginationAndLimit() {
        val userDto1 = UserInfoDto(phone = "12345678", email = "test1@test1.xyz", name = "Test User 1")
        val userDto2 = UserInfoDto(phone = "87654321", email = "test2@test2.xyz", name = "Test User 2")

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userDto1)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userDto2)
                .post("/users")
                .then()
                .statusCode(201)

        val nextPage = given().auth()
                .basic("admin", "admin")
                .param("limit", 1)
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

        given().auth()
                .basic("admin", "admin")
                .get(nextPage)
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
        val originalUserInfoDto = UserInfoDto(phone = "12345678", email = "test1@test1.xyz", name = "Test User 1")
        val resourcePath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put("/users/invalidId")
                .then()
                .statusCode(409)

        originalUserInfoDto.email = null
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)

        originalUserInfoDto.email = "test1@test1.xyz"
        originalUserInfoDto.phone = null
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)

        originalUserInfoDto.phone = "43214321"
        originalUserInfoDto.name = null
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .put(resourcePath)
                .then()
                .statusCode(400)
    }

    @Test
    fun testReplaceUserWithValidRequestBody() {
        val originalUserInfoDto = UserInfoDto(phone = "12345678", email = "test1@test1.xyz", name = "Test User 1")
        val resourcePath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val userInfoDtoFromServer = given().auth()
                .basic("admin", "admin")
                .get(resourcePath)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject("data", UserInfoDto::class.java)


        userInfoDtoFromServer.email = "new@test1.xyz"
        userInfoDtoFromServer.name = "New Name"
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userInfoDtoFromServer)
                .put(resourcePath)
                .then()
                .statusCode(204)

        given().auth()
                .basic("admin", "admin")
                .get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.phone", equalTo(originalUserInfoDto.phone))
                .body("data.email", equalTo(userInfoDtoFromServer.email))
                .body("data.name", equalTo(userInfoDtoFromServer.name))
                .body("data.updated", not(equalTo(userInfoDtoFromServer.created)))
    }

    @Test
    fun testReplaceUserCreatesNewResourceIfNotExists() {
        val nonExistingUserDto = UserInfoDto(phone = "12345678", email = "test1@test1.xyz", name = "Test User 1")
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(nonExistingUserDto)
                .put("/users/${nonExistingUserDto.phone}")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .get("/users")
                .then()
                .statusCode(200)
                .body("data.data.phone", hasItems(nonExistingUserDto.phone))
    }

    @Test
    fun testJsonMergePatchWithValidRequestBody() {
        val originalUserInfoDto = UserInfoDto("12345678", email = "test1@test1.xyz", name = "Test User")
        val resourcePath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")

        val newName = "new name"
        val newEmail = "email@email.xyz"
        val jsonBody = "{\"name\":\"$newName\", \"email\":\"$newEmail\"}"

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(jsonBody)
                .patch(resourcePath)
                .then()
                .statusCode(204)

        given().auth()
                .basic("admin", "admin")
                .get(resourcePath)
                .then()
                .statusCode(200)
                .body("data.name", equalTo(newName))
                .body("data.phone", equalTo(originalUserInfoDto.phone))
                .body("data.email", equalTo(newEmail))
    }

    @Test
    fun testJsonMergePatchWithInvalidRequestBody() {
        val originalUserInfoDto = UserInfoDto("12345678", email = "test1@test1.xyz", name = "Test User")
        val resourcePath = given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body(originalUserInfoDto)
                .post("/users")
                .then()
                .statusCode(201)
                .extract().header("Location")


        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"name\":\"new name\"}")
                .patch("$resourcePath-invalid")
                .then()
                .statusCode(404)

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{")
                .patch(resourcePath)
                .then()
                .statusCode(400)

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"phone\":\"1234\"}")
                .patch(resourcePath)
                .then()
                .statusCode(400)

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"name\":null}")
                .patch(resourcePath)
                .then()
                .statusCode(400)

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"email\":null}")
                .patch(resourcePath)
                .then()
                .statusCode(400)
    }


    /*** Authentication Tests ***/

    @Test
    fun testUserCanOnlyRetrieveOwnUserInfoAndAdminAny() {
        val phone = "12345678"
        val phone2 = "87654321"

        given().auth()
                .basic(phone, "123")
                .get("/users/$phone")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic(phone2, "123")
                .get("/users/$phone")
                .then()
                .statusCode(403)

        given().get("/users/$phone")
                .then()
                .statusCode(401)

        given().auth()
                .basic("admin", "admin")
                .get("/users/1234")
                .then()
                .statusCode(not(403))
    }


    @Test
    fun testOnlyAdminCanGetAll() {
        given().auth()
                .basic("12345678", "123")
                .get("/users")
                .then()
                .statusCode(403)

        given().get("/users")
                .then()
                .statusCode(401)

        given().auth()
                .basic("admin", "admin")
                .get("/users")
                .then()
                .statusCode(200)
    }

    @Test
    fun testUserMustBeAuthenticatedToPost() {
        given().auth()
                .basic("12345678", "123")
                .post("/users")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic("admin", "admin")
                .post("/users")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testUserMustBeAuthenticatedToPatch() {
        given().auth()
                .basic("12345678", "123")
                .body("")
                .patch("/users/12345678")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic("admin", "admin")
                .body("")
                .patch("/users/12345678")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testUserMustBeAuthenticatedToPut() {
        given().auth()
                .basic("12345678", "123")
                .body("")
                .put("/users/12345678")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic("admin", "admin")
                .body("")
                .put("/users/12345678")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testOnlyAdminCanDeleteUser() {
        given().auth()
                .basic("12345678", "123")
                .delete("/users/12345678")
                .then()
                .statusCode(403)

        given().auth()
                .basic("admin", "admin")
                .delete("/users/12345678")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testUserCanOnlyPatchTheirOwnInfoAndAdminAny() {
        given().auth()
                .basic("12345678", "123")
                .contentType("application/merge-patch+json")
                .body("{\"name\":\"new name\"}")
                .patch("/users/12345678")
                .then()
                .statusCode(not(403))

        given().auth()
                .basic("87654321", "123")
                .contentType("application/merge-patch+json")
                .body("{\"name\":\"new name\"}")
                .patch("/users/12345678")
                .then()
                .statusCode(403)

        given().auth()
                .basic("admin", "admin")
                .contentType("application/merge-patch+json")
                .body("{\"name\":\"new name\"}")
                .patch("/users/12345678")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testUserCanOnlyPostTheirOwnInfoAndAdminAny() {
        val userInfoDto = UserInfoDto(phone = "12345678", email = "test@test.xyz", name = "Test User")

        given().auth()
                .basic(userInfoDto.phone, "123")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic("87654321", "123")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .post("/users")
                .then()
                .statusCode(403)

        given()
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .post("/users")
                .then()
                .statusCode(401)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .post("/users")
                .then()
                .statusCode(not(403))
    }

    @Test
    fun testUserCanOnlyReplaceTheirOwnInfoAndAdminAny() {
        val userInfoDto = UserInfoDto(phone = "12345678", email = "test@test.xyz", name = "Test User")

        given().auth()
                .basic(userInfoDto.phone, "123")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .post("/users")
                .then()
                .statusCode(201)

        given().auth()
                .basic(userInfoDto.phone, "123")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .put("/users/${userInfoDto.phone}")
                .then()
                .statusCode(204)

        given().auth()
                .basic("87654321", "123")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .put("/users/${userInfoDto.phone}")
                .then()
                .statusCode(403)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .put("/users/${userInfoDto.phone}")
                .then()
                .statusCode(204)

        given()
                .contentType(ContentType.JSON)
                .body(userInfoDto)
                .put("/users/${userInfoDto.phone}")
                .then()
                .statusCode(401)



    }
}