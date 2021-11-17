package vadiole.receiptkeeper.ui

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseActivity
import vadiole.core.extensions.dpf
import vadiole.core.extensions.fill

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(fill, fill)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, 14f.dpf(context))
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                text = "Receipts"
            }
        )
    }
}