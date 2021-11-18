package vadiole.core.utils

import android.view.View
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Helper class to prevent double clicks in the same frame
 */
private class DebouncingOnClick<T : View>(
    private val intervalMillis: Long = 0,
    private val click: ((T) -> Unit),
) : View.OnClickListener {

    override fun onClick(view: View) {
        if (enabled.getAndSet(false)) {
            view.postDelayed(ENABLE_AGAIN, intervalMillis)
            @Suppress("UNCHECKED_CAST")
            click.invoke(view as T) // as long as the onClick is set correctly - it is safe
        }
    }

    companion object {
        @JvmStatic
        private var enabled = AtomicBoolean(true)

        @JvmStatic
        private val ENABLE_AGAIN = { enabled.set(true) }
    }
}

var <T : View> T.onClick: (T) -> Unit
    get() = throw RuntimeException("There is no getter for onClick")
    set(action) = setOnClickListener(DebouncingOnClick(click = action))
