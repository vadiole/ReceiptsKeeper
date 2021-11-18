package vadiole.receiptkeeper.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import vadiole.receiptkeeper.data.local.ReceiptDao
import vadiole.receiptkeeper.data.local.ReceiptDatabase
import vadiole.receiptkeeper.model.raw.ReceiptEntity
import java.time.LocalDateTime
import java.util.*
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideReceiptDatabase(@ApplicationContext app: Context, provider: Provider<ReceiptDao>): ReceiptDatabase {
        return Room.databaseBuilder(app, ReceiptDatabase::class.java, ReceiptDatabase.NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // prepopulate database for now
                    MainScope().launch(Dispatchers.Default) {
                        val now = LocalDateTime.now()
                        repeat(2000) {
                            val randomRaw = UUID.randomUUID().toString()
                            val title = randomRaw.take(12)
                            val date = now.minusHours(it * 7L)
                            val receipt = ReceiptEntity(it, title, date, randomRaw)
                            provider.get().insertReceipt(receipt)
                        }
                    }
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideReceiptDao(database: ReceiptDatabase) = database.receiptDao
}