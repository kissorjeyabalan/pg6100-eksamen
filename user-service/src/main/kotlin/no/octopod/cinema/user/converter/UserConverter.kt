package no.octopod.cinema.user.converter

import no.octopod.cinema.user.entity.UserEntity
import no.octopod.cinema.common.dto.UserInfoDto
import no.octopod.cinema.common.hateos.HalPage
import kotlin.streams.toList

class UserConverter {
    companion object {
        fun transform(entity: UserEntity): UserInfoDto {
            return UserInfoDto(
                    phone = entity.phone.toString(),
                    email = entity.email.toString(),
                    name = entity.name.toString(),
                    created = entity.creationTime,
                    updated = entity.updatedTime
            )
        }

        fun transform(dto: UserInfoDto): UserEntity {
            return UserEntity(
                    dto.phone,
                    dto.email,
                    dto.name
            )
        }

        fun transform(entities: Iterable<UserEntity>): List<UserInfoDto> {
            return entities.map { transform(it) }
        }

        fun transform(entities: List<UserEntity>, page: Int, limit: Int): HalPage<UserInfoDto> {
            val offset = ((page -1) * limit).toLong()
            val dtoList: MutableList<UserInfoDto> = entities.stream()
                    .skip(offset)
                    .limit(limit.toLong())
                    .map { transform(it) }
                    .toList().toMutableList()

            val pageDto = HalPage<UserInfoDto>()
            pageDto.data = dtoList
            pageDto.count = entities.size.toLong()
            pageDto.pages = ((pageDto.count / limit)).toInt()

            return pageDto
        }
    }
}