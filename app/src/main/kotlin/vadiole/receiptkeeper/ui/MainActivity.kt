package vadiole.receiptkeeper.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseActivity
import vadiole.receiptkeeper.ui.history.HistoryFragment

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentContainerId = View.generateViewId()
        val fragmentContainer = FragmentContainerView(this).apply {
            id = fragmentContainerId
        }
        setContentView(fragmentContainer)

        supportFragmentManager.commit {
            add(fragmentContainerId, HistoryFragment())
        }
    }
}