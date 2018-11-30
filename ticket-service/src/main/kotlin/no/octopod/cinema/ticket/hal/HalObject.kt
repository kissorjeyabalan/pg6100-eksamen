package no.octopod.cinema.ticket.hal

import io.swagger.annotations.ApiModelProperty


/*

     Copied from testing_security_development_enterprise_systems repository

*/
open class HalObject(

        @ApiModelProperty("HAL links")
        var _links: MutableMap<String, HalLink> = mutableMapOf()
)
