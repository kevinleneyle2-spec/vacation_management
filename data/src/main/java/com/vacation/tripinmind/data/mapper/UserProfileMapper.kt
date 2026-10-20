package com.vacation.tripinmind.data.mapper

import com.vacation.tripinmind.data.local.model.UserProfileEntity
import com.vacation.tripinmind.data.remote.model.UserProfileDto
import com.vacation.tripinmind.model.UserProfile

fun UserProfile.toDto(): UserProfileDto {
    return UserProfileDto(
        uuid = uuid,
        shareCode = shareCode
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        uuid = uuid,
        shareCode = shareCode
    )
}

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        uuid = uuid,
        shareCode = shareCode
    )
}

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        uuid = uuid,
        shareCode = shareCode
    )
}