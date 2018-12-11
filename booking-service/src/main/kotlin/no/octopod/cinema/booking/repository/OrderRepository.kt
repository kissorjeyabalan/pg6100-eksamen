package no.octopod.cinema.booking.repository

import no.octopod.cinema.booking.entity.OrderEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CrudRepository<OrderEntity, Long> {
    fun findAllByUserId(userId: String): MutableIterable<OrderEntity>
}