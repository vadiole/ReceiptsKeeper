package vadiole.receiptkeeper.ui.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import vadiole.core.base.BaseFragment
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.databinding.FragmentScannerBinding

class ScannerFragment : BaseFragment<ScannerViewModel, FragmentScannerBinding>() {

    override val viewModel: ScannerViewModel by viewModels()

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentScannerBinding.inflate(inflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBackButton()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    private fun initBackButton() = with(binding) {
        scannerBackButton.onClick = {
            requireActivity().onBackPressed()
        }
    }
}