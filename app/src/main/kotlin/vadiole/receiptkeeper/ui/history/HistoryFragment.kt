package vadiole.receiptkeeper.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils.loadLayoutAnimation
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.observe
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentHistoryBinding
import vadiole.receiptkeeper.ui.MainActivity
import vadiole.receiptkeeper.ui.history.list.HistoryAdapter

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {
    override val viewModel: HistoryViewModel by activityViewModels()
    private var firstLoad = true

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHistoryBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initHistoryList(savedInstanceState == null && firstLoad)
        initScanButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstLoad = false
    }

    private fun initHistoryList(animate: Boolean) = with(binding) {
        val historyAdapter = HistoryAdapter { id: Int ->
            navigator.navigate("receipt $id")
        }

        historyList.setHasFixedSize(true)
        historyList.adapter = historyAdapter
        historyList.layoutManager = LinearLayoutManager(context)
        if (animate) {
            historyList.layoutAnimation = loadLayoutAnimation(requireContext(), R.anim.layout_animation_fade_in)
        } else {
            historyList.layoutAnimation = null
        }

        viewModel.receiptsHistory.observe(viewLifecycleOwner) { data ->
            historyList.scheduleLayoutAnimation()
            historyAdapter.submitList(data)
        }
    }

    private fun initScanButton() = with(binding) {
        historyScanReceiptButton.onClick = {
            navigator.navigate(MainActivity.SCANNER_FRAGMENT)
        }
    }
}