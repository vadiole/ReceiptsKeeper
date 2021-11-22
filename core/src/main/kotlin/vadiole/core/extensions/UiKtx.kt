package vadiole.core.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.Shape
import android.view.ViewGroup
import androidx.annotation.ColorInt
import kotlin.math.ceil

const val fill = ViewGroup.LayoutParams.MATCH_PARENT
const val wrap = ViewGroup.LayoutParams.WRAP_CONTENT

/**
 * Convert dp to pixels
 *
 * @return pixels in float
 */
fun Float.dp(context: Context): Float = this * context.resources.displayMetrics.density

/**
 * Convert dp to pixels
 *
 * @return pixels in int
 */
fun Int.dp(context: Context): Int = ceil(this * context.resources.displayMetrics.density).toInt()

fun RippleDrawable(@ColorInt color: Int, content: Drawable? = null, shape: Shape = RectShape()): RippleDrawable {
    val colorStateList = ColorStateList.valueOf(color)
    return RippleDrawable(colorStateList, content, ShapeDrawable(shape))
}