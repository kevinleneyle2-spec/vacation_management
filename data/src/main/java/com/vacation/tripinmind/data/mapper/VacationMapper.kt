package com.vacation.tripinmind.data.mapper

import com.vacation.tripinmind.data.local.model.ActivityEntity
import com.vacation.tripinmind.data.local.model.DayEntity
import com.vacation.tripinmind.data.local.model.VacationEntity
import com.vacation.tripinmind.data.remote.model.ActivityDto
import com.vacation.tripinmind.data.remote.model.DayDto
import com.vacation.tripinmind.data.remote.model.VacationDto
import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.model.Day
import com.vacation.tripinmind.model.Vacation

fun VacationDto.toEntity(): VacationEntity {
    return VacationEntity(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toEntity()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun DayDto.toEntity(): DayEntity {
    return DayEntity(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toEntity()
        }
    )
}

fun ActivityDto.toEntity(): ActivityEntity {
    return ActivityEntity(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}

fun VacationDto.toDomain(): Vacation {
    return Vacation(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toDomain()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun DayDto.toDomain(): Day {
    return Day(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toDomain()
        }
    )
}

fun ActivityDto.toDomain(): Activity {
    return Activity(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}

fun ActivityEntity.toDto(): ActivityDto {
    return ActivityDto(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}

fun DayEntity.toDto(): DayDto {
    return DayDto(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toDto()
        }
    )
}

fun VacationEntity.toDto(): VacationDto {
    return VacationDto(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toDto()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}

fun DayEntity.toDomain(): Day {
    return Day(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toDomain()
        }
    )
}

fun VacationEntity.toDomain(): Vacation {
    return Vacation(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toDomain()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun Vacation.toDto(): VacationDto {
    return VacationDto(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toDto()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun Day.toDto(): DayDto {
    return DayDto(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toDto()
        }
    )
}

fun Activity.toDto(): ActivityDto {
    return ActivityDto(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}

fun Vacation.toEntity(): VacationEntity {
    return VacationEntity(
        id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        days = days.map {
            it.toEntity()
        },
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}

fun Day.toEntity(): DayEntity {
    return DayEntity(
        date = date,
        nameDay = nameDay,
        additionalInfo = additionalInfo,
        activity = activity.map {
            it.toEntity()
        }
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        activityName = activityName,
        activityTime = activityTime,
        activityDuration = activityDuration,
        activityLocation = activityLocation
    )
}