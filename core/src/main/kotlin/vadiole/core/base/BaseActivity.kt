package vadiole.core.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

abstract class BaseActivity : AppCompatActivity(), Navigator {

    val insetsControllerX: WindowInsetsControllerCompat?
        get() = WindowCompat.getInsetsController(window, window.decorView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onResume() {
        super.onResume()
        updateSystemBars(resources.configuration)
    }

    private fun updateSystemBars(configuration: Configuration) {
        val isNightMode = Build.VERSION.SDK_INT >= 30 && configuration.isNightModeActive
        insetsControllerX?.isAppearanceLightStatusBars = !isNightMode
        insetsControllerX?.isAppearanceLightNavigationBars = !isNightMode
    }
}