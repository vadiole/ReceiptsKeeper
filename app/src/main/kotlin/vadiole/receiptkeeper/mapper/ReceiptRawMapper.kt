package vadiole.receiptkeeper.mapper

import android.util.Base64
import vadiole.receiptkeeper.model.raw.ReceiptEntity
import vadiole.receiptkeeper.model.raw.ReceiptRaw
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

/*
    ****************************************
                     УВАГА
                  ТЕСТОВИЙ ЧЕК
    ****************************************
                 ФН 4000037242
    ----------------------------------------
    Бейліз кава ФУДКОРТ
    1.0*65.0 =                          65.0
                    Готівка
    ----------------------------------------
    Сума                                65.0
    ----------------------------------------
    № 364
    Дата 04.11.2021 13:20:22
    ****************************************
                 ІД WO9KsM05Bz4
 */
class ReceiptRawMapper @Inject constructor() {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    fun map(raw: ReceiptRaw, id: String, date: LocalDate): ReceiptEntity {
        val rawString = Base64.decode(raw.receipt64, Base64.DEFAULT).decodeToString()
        var datetime: LocalDateTime? = null
        try {
            rawString.reader().forEachLine {
                if (it.contains("Дата")) {
                    datetime = LocalDateTime.parse(it.drop(5), dateTimeFormatter)
                    return@forEachLine
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return ReceiptEntity(
            id = id,
            datetime = datetime ?: LocalDateTime.of(date, LocalTime.now()),
            title = raw.name,
            raw = rawString
        )
    }
}