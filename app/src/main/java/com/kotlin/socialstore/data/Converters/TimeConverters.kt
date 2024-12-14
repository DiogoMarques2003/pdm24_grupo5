package com.kotlin.socialstore.data.Converters

import androidx.room.TypeConverter
import java.sql.Time

class TimeConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Time? {
        return value?.let { Time(it) }
    }

    @TypeConverter
    fun timeToTimestamp(time: Time?): Long? {
        return time?.time
    }
}