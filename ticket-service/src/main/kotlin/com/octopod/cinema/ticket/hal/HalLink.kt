package com.octopod.cinema.ticket.hal

import io.swagger.annotations.ApiModelProperty


/*

     Copied from testing_security_development_enterprise_systems repository

*/
open class HalLink (


        @ApiModelProperty("the url of the link")
        var href: String = ""
)