package no.octopod.cinema.booking.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.sun.org.apache.xpath.internal.operations.Bool
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertTrue
import no.octopod.cinema.booking.dto.SeatDto
import no.octopod.cinema.booking.repository.OrderRepository
import no.octopod.cinema.booking.repository.SeatReservationRepository
import no.octopod.cinema.kino.dto.ShowDto
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class BookingTest {
    companion object {
        private lateinit var wireMockServer: WireMockServer
        private var mapper: ObjectMapper = ObjectMapper()

        @BeforeClass @JvmStatic
        fun initClass() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 8080
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            wireMockServer = WireMockServer(wireMockConfig().port(8099).notifier(ConsoleNotifier(true)))
            wireMockServer.start()
        }

        @AfterClass @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }

    }

    // WIREMOCKS
    private fun mockUnreserveSeat(statusCode: Int) {
        wireMockServer.stubFor(
                WireMock.post(urlMatching("/shows/.*/seats/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(statusCode))
        )

    }

    private fun mockReserveSeat(statusCode: Int) {
        wireMockServer.stubFor(
                WireMock.delete(urlMatching("/shows/.*/seats/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(statusCode))
        )
    }

    private var currentTicketNumber = 0
    private fun mockAddTicket(statusCode: Int) {
        currentTicketNumber++

        wireMockServer.stubFor(
                WireMock.post(urlMatching("/tickets"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Location", "/tickets/$currentTicketNumber")
                                .withStatus(statusCode))
        )
    }

    private fun mockDeleteTicket(statusCode: Int) {
        wireMockServer.stubFor(
                WireMock.delete(urlMatching("/tickets"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(statusCode))
        )
    }


    @Autowired private lateinit var seatReservationRepo: SeatReservationRepository
    @Autowired private lateinit var orderRepo: OrderRepository

    @Before
    fun before() {
        seatReservationRepo.deleteAll()
        orderRepo.deleteAll()
    }

    @Test
    fun testAddSeatToReservation() {
        var seatDto = SeatDto("2a", "1")

        mockReserveSeat(204)

        given().auth().basic("foo", "123")
                .contentType(ContentType.JSON)
                .body(seatDto)
                .post("/orders/reserve")
                .then()
                .statusCode(204)

        assertTrue(reservationExists("foo", seatDto))
    }


    private fun reservationExists(userId: String, seatDto: SeatDto): Boolean {
        return seatReservationRepo.existsByUserIdAndScreeningIdAndSeat(
                        userId = userId,
                        seat = seatDto.seat!!,
                        screeningId= seatDto.screening_id!!.toLong()
                )
    }



}