package vadiole.receiptkeeper.data.local.converters

import androidx.room.TypeConverter

/**
 * Base converter to convert an object to a String and vice-versa for Room
 */

abstract class BaseConverter<T> {
    abstract fun parseFromString(value: String): T?

    @TypeConverter
    open fun toString(value: T?): String? = value?.toString()

    @TypeConverter
    open fun fromString(value: String?): T? = if (value.isNullOrEmpty()) null else parseFromString(value)
}