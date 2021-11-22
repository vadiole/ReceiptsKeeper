package vadiole.receiptkeeper.ui.history.list.cell

import android.content.Context
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.RecyclerView
import vadiole.core.extensions.dp
import vadiole.core.extensions.fill
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.model.presentation.HistoryItem

class DateCell(context: Context) : AppCompatTextView(context) {
    init {
        setTextAppearance(this, R.style.TextAppearance_AppCompat_Body1)
        setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
        layoutParams = RecyclerView.LayoutParams(fill, 50.dp(context))
        gravity = Gravity.CENTER
    }

    fun bind(data: HistoryItem.Date) {
        text = if (data.isToday) {
            context.getString(R.string.today)
        } else {
            data.value
        }
    }
}