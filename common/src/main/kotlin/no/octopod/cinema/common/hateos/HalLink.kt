package no.octopod.cinema.common.hateos

import io.swagger.annotations.ApiModelProperty

/**
 * Taken from
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ee5b64dc623abb1d344e0d2b8cde660358efbb89/advanced/rest/rest-dto/src/main/kotlin/org/tsdes/advanced/rest/dto/hal/HalLink.kt
 */
open class HalLink (
    @ApiModelProperty
    var href: String = ""
)