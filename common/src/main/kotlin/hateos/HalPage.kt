package hateos

import com.fasterxml.jackson.annotation.JsonIgnore

/**
 * Partially taken from
 * https://github.com/arcuri82/testing_security_development_enterprise_systems/blob/ee5b64dc623abb1d344e0d2b8cde660358efbb89/advanced/rest/rest-dto/src/main/kotlin/org/tsdes/advanced/rest/dto/hal/PageDto.kt
 */
open class HalPage<T>(
        var list: MutableList<T> = mutableListOf(),
        var pages: Int = 0,
        var count: Long = 0,
        next: HalLink? = null,
        previous: HalLink? = null,
        _self: HalLink? = null
) : HalObject() {
    @get:JsonIgnore
    var next: HalLink?
        set(value) {
            if (value != null) {
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
            } else {
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