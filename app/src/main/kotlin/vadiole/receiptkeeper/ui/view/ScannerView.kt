package vadiole.receiptkeeper.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.journeyapps.barcodescanner.BarcodeView
import vadiole.core.extensions.dp

class ScannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : BarcodeView(context, attrs, defStyle) {

    private val backgroundPaint = Paint().apply {
        color = 0x7f000000
    }
    private val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        strokeWidth = 2f.dp(context)
        color = -0x1
    }
    private val cornerPath = Path()
    private val corner = 32f.dp(context)

    init {
        isUseTextureView = true
    }

    // drawing dim and corners
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val size = (width.coerceAtMost(height) / 1.5f).toInt()
        val x = (width - size) / 2f
        val y = (height - size) / 2f
        canvas.drawRect(0f, 0f, width.toFloat(), y, backgroundPaint)
        canvas.drawRect(0f, (y + size), width.toFloat(), height.toFloat(), backgroundPaint)
        canvas.drawRect(0f, y, x, (y + size), backgroundPaint)
        canvas.drawRect((x + size), y, width.toFloat(), (y + size), backgroundPaint)

        with(cornerPath) {
            reset()
            moveTo(x, y + corner)
            lineTo(x, y)
            lineTo(x + corner, y)
            canvas.drawPath(this, cornerPaint)

            reset()
            moveTo((x + size), y + corner)
            lineTo((x + size), y)
            lineTo(x + size - corner, y)
            canvas.drawPath(this, cornerPaint)

            reset()
            moveTo(x, y + size - corner)
            lineTo(x, (y + size))
            lineTo(x + corner, (y + size))
            canvas.drawPath(this, cornerPaint)

            reset()
            moveTo((x + size), y + size - corner)
            lineTo((x + size), (y + size))
            lineTo(x + size - corner, (y + size))
            canvas.drawPath(this, cornerPaint)
        }
    }
}