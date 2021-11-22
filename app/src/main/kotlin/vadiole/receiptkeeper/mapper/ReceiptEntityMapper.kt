package vadiole.receiptkeeper.mapper

import vadiole.receiptkeeper.model.domain.HistoryDomain
import vadiole.receiptkeeper.model.raw.ReceiptEntity
import javax.inject.Inject

class ReceiptEntityMapper @Inject constructor() {
    fun map(entity: ReceiptEntity) = HistoryDomain.Receipt(
        entity.id,
        entity.title,
        entity.datetime,
        entity.raw
    )
}