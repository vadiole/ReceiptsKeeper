package vadiole.receiptkeeper.repository

import kotlinx.coroutines.flow.Flow
import vadiole.receiptkeeper.data.local.ReceiptDao
import vadiole.receiptkeeper.model.raw.ReceiptEntity
import javax.inject.Inject

class ReceiptRepository @Inject constructor(private val receiptDao: ReceiptDao) {
    fun getReceipts(): Flow<List<ReceiptEntity>> = receiptDao.getReceipts()

    suspend fun getReceipt(id: String): ReceiptEntity? = receiptDao.getReceipt(id)

    suspend fun insertReceipt(entity: ReceiptEntity) = receiptDao.insertReceipt(entity)

    suspend fun deleteReceipt(id: String) = receiptDao.deleteReceipt(id)
}