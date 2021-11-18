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
import androidx.fragment.app.Fragment
import kotlin.math.ceil

const val fill = ViewGroup.LayoutParams.MATCH_PARENT
const val wrap = ViewGroup.LayoutParams.WRAP_CONTENT

fun Float.dp(context: Context): Int = ceil(this * context.resources.displayMetrics.density).toInt()

fun Float.dpf(context: Context): Float = this * context.resources.displayMetrics.density

fun Int.dp(context: Context): Int = ceil(this * context.resources.displayMetrics.density).toInt()

fun Int.dpf(context: Context): Float = this * context.resources.displayMetrics.density

fun RippleDrawable(@ColorInt color: Int, content: Drawable? = null, shape: Shape = RectShape()): RippleDrawable {
    val colorStateList = ColorStateList.valueOf(color)
    return RippleDrawable(colorStateList, content, ShapeDrawable(shape))
}