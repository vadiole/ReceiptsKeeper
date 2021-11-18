package vadiole.core.base

import android.os.Bundle

interface Navigator {
    fun navigate(destination: String, args: Bundle? = null)
}