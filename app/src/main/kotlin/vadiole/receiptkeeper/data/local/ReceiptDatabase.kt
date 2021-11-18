package vadiole.receiptkeeper.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import vadiole.receiptkeeper.data.local.converters.LocalDateTimeConverter
import vadiole.receiptkeeper.model.raw.ReceiptEntity

@TypeConverters(LocalDateTimeConverter::class)
@Database(version = 1, entities = [ReceiptEntity::class], exportSchema = true)
abstract class ReceiptDatabase : RoomDatabase() {
    abstract val receiptDao: ReceiptDao

    companion object {
        const val NAME = "receipt_database"
    }
}