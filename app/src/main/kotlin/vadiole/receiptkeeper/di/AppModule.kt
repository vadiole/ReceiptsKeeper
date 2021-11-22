package vadiole.receiptkeeper.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import vadiole.receiptkeeper.data.local.ReceiptDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideReceiptDatabase(@ApplicationContext app: Context): ReceiptDatabase {
        return Room.databaseBuilder(app, ReceiptDatabase::class.java, ReceiptDatabase.NAME)
            .build()
    }

    @Provides
    @Singleton
    fun provideReceiptDao(database: ReceiptDatabase) = database.receiptDao
}