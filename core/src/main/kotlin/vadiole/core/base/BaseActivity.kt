package vadiole.core.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

abstract class BaseActivity : AppCompatActivity(), Navigator {

    private val insetsControllerX: WindowInsetsControllerCompat?
        get() = WindowCompat.getInsetsController(window, window.decorView)

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(
                0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                0, insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            insets
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        updateSystemBars(resources.configuration)
    }

    private fun updateSystemBars(configuration: Configuration) {
        val isLight = Build.VERSION.SDK_INT >= 30 && configuration.isNightModeActive
        insetsControllerX?.isAppearanceLightStatusBars = !isLight
        insetsControllerX?.isAppearanceLightNavigationBars = !isLight
    }
}