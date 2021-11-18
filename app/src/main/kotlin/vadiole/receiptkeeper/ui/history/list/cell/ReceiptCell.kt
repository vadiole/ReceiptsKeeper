package vadiole.receiptkeeper.ui.history.list.cell

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.widget.TextViewCompat.setTextAppearance
import androidx.recyclerview.widget.RecyclerView
import vadiole.core.extensions.RippleRect
import vadiole.core.extensions.dp
import vadiole.core.extensions.fill
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.model.presentation.HistoryItem

class ReceiptCell(context: Context, click: (id: Int) -> Unit) : LinearLayout(context) {
    private var receiptId: Int = -1

    private val title = AppCompatTextView(context).apply {
        setTextAppearance(this, R.style.TextAppearance_AppCompat_Body2)
        setTextColor(getColor(context, R.color.textPrimary))
        layoutParams = RecyclerView.LayoutParams(fill, 36.dp(context))
        typeface = getFont(context, R.font.sf_mono_regular)
        gravity = Gravity.START or Gravity.BOTTOM
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
    }

    private val date = AppCompatTextView(context).apply {
        setTextAppearance(this, R.style.TextAppearance_AppCompat_Caption)
        setTextColor(getColor(context, R.color.textSecondary))
        layoutParams = RecyclerView.LayoutParams(fill, 36.dp(context))
        typeface = getFont(context, R.font.sf_mono_regular)
        gravity = Gravity.START or Gravity.TOP
        ellipsize = TextUtils.TruncateAt.END
        isSingleLine = true
    }

    init {
        layoutParams = RecyclerView.LayoutParams(fill, 72.dp(context))
        background = RippleRect(getColor(context, R.color.ripple))
        setPadding(16.dp(context), 0, 16.dp(context), 0)
        orientation = VERTICAL
        isClickable = true
        isFocusable = true

        onClick = { // onClick in init to avoid creating a new listener every bind
            if (receiptId >= 0) click.invoke(receiptId)
        }

        addView(title)
        addView(date)
    }

    fun bind(data: HistoryItem.Receipt) {
        receiptId = data.id
        title.text = context.getString(R.string.receipt, data.title)
        date.text = context.getString(R.string.date, data.date)
    }
}