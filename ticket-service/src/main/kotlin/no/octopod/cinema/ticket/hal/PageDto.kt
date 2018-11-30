package no.octopod.cinema.ticket.hal

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.annotations.ApiModelProperty



/*

     Copied from testing_security_development_enterprise_systems repository

*/
class PageDto<T>(


        @get:ApiModelProperty("list of resources in current page")
        var list: MutableList<T> = mutableListOf(),

        @get:ApiModelProperty("index of first element")
        var rangeMin: Int = 0,

        @get:ApiModelProperty("The index of the last element of this page")
        var rangeMax: Int = 0,

        @get:ApiModelProperty("The total number of elements in all pages")
        var totalSize: Int = 0,

        next: HalLink? = null,

        previous: HalLink? = null,

        _self: HalLink? = null

) : HalObject() {


    @get:JsonIgnore
    var next: HalLink?
        set(value) {
            if(value != null) {
                _links["next"] = value
            } else {
                _links.remove("next")
            }
        }
        get() = _links["next"]


    @get:JsonIgnore
    var previous: HalLink?
        set(value) {
            if (value != null) {
                _links["previous"] = value
            }  else {
                _links.remove("previous")
            }
        }
        get() = _links["previous"]


    @get:JsonIgnore
    var _self: HalLink?
        set(value) {
            if (value != null) {
                _links["self"] = value
            } else {
                _links.remove("self")
            }
        }
        get() = _links["self"]


    init {
        this.next = next
        this.previous = previous
        this._self = _self
    }

}