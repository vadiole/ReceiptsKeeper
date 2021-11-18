package vadiole.receiptkeeper.model.presentation

sealed class HistoryItem {
    class Receipt(val id: Int, val title: String, val date: String) : HistoryItem()

    class Date(val value: String, val isToday: Boolean) : HistoryItem()
}