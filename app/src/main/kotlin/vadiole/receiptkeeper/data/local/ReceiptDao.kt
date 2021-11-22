package vadiole.receiptkeeper.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import vadiole.receiptkeeper.model.raw.ReceiptEntity

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM Receipt")
    fun getReceipts(): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM Receipt WHERE id = :id")
    suspend fun getReceipt(id: String): ReceiptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(entity: ReceiptEntity)

    @Query("DELETE FROM Receipt WHERE id = :id")
    suspend fun deleteReceipt(id: String)
}