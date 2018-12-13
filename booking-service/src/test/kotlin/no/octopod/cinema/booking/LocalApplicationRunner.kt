package no.octopod.cinema.booking

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import no.octopod.cinema.booking.controller.BookingTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

class LocalApplicationRunner: BookingApplication() {
    @Autowired private lateinit var wireMockServer: WireMockServer

    @Bean
    fun getWiremockServer(): WireMockServer {
        val wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8099))
        wireMockServer.start()
        wireMockServer.stubFor(
                WireMock.post(WireMock.urlMatching("/shows/.*/seats/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(204))
        )
        wireMockServer.stubFor(
                WireMock.delete(urlMatching("/shows/.*/seats/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(204))
        )
        wireMockServer.stubFor(
                WireMock.post(urlMatching("/tickets"))
                        .willReturn(WireMock.aResponse()
                                .withHeader("Location", "/tickets/1")
                                .withStatus(204))
        )
        wireMockServer.stubFor(
                WireMock.delete(urlMatching("/tickets/.*"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(201))
        )
        return wireMockServer
    }
}

fun main(args: Array<String>) {
    runApplication<LocalApplicationRunner>(*args, "--spring.profiles.active=test")
}