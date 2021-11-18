package vadiole.receiptkeeper.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.observe
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentHistoryBinding
import vadiole.receiptkeeper.ui.history.list.HistoryAdapter

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {
    override val viewModel: HistoryViewModel by activityViewModels()

    private val onReceiptClick = { id: Int ->
        Toast.makeText(requireContext(), "Item $id click", Toast.LENGTH_SHORT).show()
    }

    private val historyAdapter = HistoryAdapter(onReceiptClick)


    override fun onCreateBinding(inflater: LayoutInflater): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initHistoryList()
        initScanButton()

        val fadeInAnim = loadLayoutAnimation(requireContext(), R.anim.layout_animation_fade_in)
        viewModel.historyData.observe(viewLifecycleOwner) { data ->
            if (historyAdapter.itemCount == 0) {
                binding.historyList.layoutAnimation = fadeInAnim
                binding.historyList.scheduleLayoutAnimation()
            }
            historyAdapter.submitList(data)
        }
    }

    private fun initHistoryList() = with(binding.historyList) {
        adapter = historyAdapter
        layoutManager = LinearLayoutManager(context)
        setHasFixedSize(true)
    }

    private fun initScanButton() = with(binding) {
        historyScanReceiptButton.onClick = {
            Toast.makeText(requireContext(), "Scan click", Toast.LENGTH_SHORT).show()
        }
    }
}