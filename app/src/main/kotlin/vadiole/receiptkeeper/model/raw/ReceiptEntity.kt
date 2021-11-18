package vadiole.receiptkeeper.model.raw

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "Receipt")
class ReceiptEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val datetime: LocalDateTime,
    val raw: String,
)