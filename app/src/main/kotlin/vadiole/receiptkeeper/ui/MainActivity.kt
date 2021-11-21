package vadiole.receiptkeeper.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseActivity
import vadiole.core.base.Navigator
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.ui.details.DetailsFragment
import vadiole.receiptkeeper.ui.history.HistoryFragment
import vadiole.receiptkeeper.ui.scanner.ScannerFragment

@AndroidEntryPoint
class MainActivity : BaseActivity(), Navigator {

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

        when (destination) {
            SCANNER_FRAGMENT -> {
                supportFragmentManager.commit {
                    addToBackStack(destination)
                    val fragment = ScannerFragment()
                    replace(R.id.fragment_container, fragment, destination)

                }
            }
            DETAILS_FRAGMENT -> {
                supportFragmentManager.commit {
                    addToBackStack(destination)
                    val fragment = DetailsFragment(args)
                    replace(R.id.fragment_container, fragment, destination)
                }
            }
            HISTORY_FRAGMENT -> {
                supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
            }
            else -> return
        }
    }

    companion object {
        const val HISTORY_FRAGMENT = "history_fragment"
        const val SCANNER_FRAGMENT = "scanner_fragment"
        const val DETAILS_FRAGMENT = "details_fragment"
    }
}