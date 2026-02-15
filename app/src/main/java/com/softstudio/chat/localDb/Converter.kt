package com.softstudio.chat.localDb

import androidx.room.TypeConverter
import com.softstudio.chat.models.MessageStatus
import com.softstudio.chat.models.MessageType

class Converters {
    @TypeConverter
    fun fromMessageType(value: MessageType?) = value?.name
    @TypeConverter
    fun toMessageType(value: String?) = value?.let { MessageType.valueOf(it) }

    @TypeConverter
    fun fromMessageStatus(value: MessageStatus?) = value?.name
    @TypeConverter
    fun toMessageStatus(value: String?) = value?.let { MessageStatus.valueOf(it) }

    @TypeConverter
    fun fromStringList(value: List<String>?) = value?.joinToString(",")
    @TypeConverter
    fun toStringList(value: String?) = value?.split(",") ?: emptyList()

    @TypeConverter
    fun fromTimestamp(value: com.google.firebase.Timestamp?): Long? {
        return value?.toDate()?.time
    }
    @TypeConverter
    fun toTimestamp(value: Long?): com.google.firebase.Timestamp? {
        return value?.let { com.google.firebase.Timestamp(java.util.Date(it)) }
    }
}