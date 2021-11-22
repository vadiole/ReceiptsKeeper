package vadiole.receiptkeeper.usecase

import vadiole.receiptkeeper.mapper.ReceiptRawMapper
import vadiole.receiptkeeper.model.raw.ReceiptRaw
import vadiole.receiptkeeper.repository.ReceiptRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class SaveReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val mapper: ReceiptRawMapper,
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    suspend fun invoke(receipt: ReceiptRaw, id: String, date: String) = kotlin.runCatching {
        val localDate = LocalDate.parse(date, dateFormatter)
        val entity = mapper.map(receipt, id, localDate)
        repository.insertReceipt(entity)
    }
}