package vadiole.receiptkeeper.ui.history.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vadiole.receiptkeeper.model.presentation.HistoryItem
import vadiole.receiptkeeper.ui.history.list.cell.DateCell
import vadiole.receiptkeeper.ui.history.list.cell.ReceiptCell

class HistoryAdapter(private val onItemClick: (id: String) -> Unit) : RecyclerView.Adapter<HistoryAdapter.Cell>() {

    private val differCallback = object : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(old: HistoryItem, new: HistoryItem) = old == new

        override fun areContentsTheSame(old: HistoryItem, new: HistoryItem) = old == new
    }

    private val differ: AsyncListDiffer<HistoryItem> = AsyncListDiffer(this, differCallback)

    private val currentList: List<HistoryItem>
        get() = differ.currentList

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemCount() = differ.currentList.size

    private fun getItem(position: Int) = differ.currentList[position]

    fun submitList(list: List<HistoryItem>, callback: () -> Unit = {}) = differ.submitList(list.toList()) {
        callback.invoke()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Cell {
        val view = when (viewType) {
            ITEM_TYPE_RECEIPT -> ReceiptCell(parent.context, onItemClick)
            ITEM_TYPE_DATE -> DateCell(parent.context)
            else -> throw RuntimeException("unknown view type $viewType")
        }
        return Cell(view)
    }

    override fun onBindViewHolder(holder: Cell, position: Int) {
        when (val data = getItem(position)) {
            is HistoryItem.Receipt -> (holder.itemView as ReceiptCell).bind(data)
            is HistoryItem.Date -> (holder.itemView as DateCell).bind(data)
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is HistoryItem.Receipt -> ITEM_TYPE_RECEIPT
        is HistoryItem.Date -> ITEM_TYPE_DATE
    }

    class Cell(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val ITEM_TYPE_RECEIPT = 0
        const val ITEM_TYPE_DATE = 1
    }
}