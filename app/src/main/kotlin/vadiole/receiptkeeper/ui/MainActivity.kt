package vadiole.receiptkeeper.ui

import android.os.Bundle
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseActivity
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.ui.history.HistoryFragment
import vadiole.receiptkeeper.ui.scanner.ScannerFragment

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, HistoryFragment(), HISTORY_FRAGMENT)
            }
        }
    }

    override fun navigate(destination: String, args: Bundle?) {
        if (supportFragmentManager.findFragmentByTag(destination)?.isAdded == true) return

        val fragment = when (destination) {
            SCANNER_FRAGMENT -> ScannerFragment()
            else -> return
        }

        supportFragmentManager.commit {
            addToBackStack(destination)
            replace(R.id.fragment_container, fragment, destination)
        }
    }

    companion object {
        const val HISTORY_FRAGMENT = "history_fragment"
        const val SCANNER_FRAGMENT = "scanner_fragment"
    }
}