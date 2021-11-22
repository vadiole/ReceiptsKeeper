package vadiole.receiptkeeper.ui.history

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.AnimationUtils.loadLayoutAnimation
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat.Type.navigationBars
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.dp
import vadiole.core.extensions.observe
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.BuildConfig.APPLICATION_ID
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentHistoryBinding
import vadiole.receiptkeeper.ui.MainActivity.Companion.DETAILS_FRAGMENT
import vadiole.receiptkeeper.ui.MainActivity.Companion.SCANNER_FRAGMENT
import vadiole.receiptkeeper.ui.details.DetailsFragment.Companion.RECEIPT_ID_KEY
import vadiole.receiptkeeper.ui.history.list.HistoryAdapter

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {

    override val viewModel: HistoryViewModel by activityViewModels()

    private var permissionsRationale: AlertDialog? = null

    private val cameraResultCallback: (Boolean) -> Unit = { isGranted ->
        if (isGranted) {
            navigator.navigate(SCANNER_FRAGMENT)
        } else {
            val canRequest = shouldShowRequestPermissionRationale(requireActivity(), CAMERA)

            val dialog = permissionsRationale ?: AlertDialog.Builder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.permission_camera_message)
                .setPositiveButton(R.string.action_grant) { dialog, _ ->
                    dialog.dismiss()
                    if (canRequest) {
                        cameraPermissionLauncher.launch(CAMERA)
                    } else {
                        val uri = Uri.fromParts("package", APPLICATION_ID, null)
                        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(uri)
                        startActivity(intent)
                    }
                }
                .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .also { permissionsRationale = it }

            dialog.show()
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(RequestPermission(), cameraResultCallback)

    private var firstLoad = true

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHistoryBinding.inflate(inflater)

    override fun onBindingCreated(savedInstanceState: Bundle?) = with(binding) {
        setOnApplyWindowInsetsListener(root) { _, insets ->
            val insetTop = insets.getInsets(statusBars()).top
            val insetBottom = insets.getInsets(navigationBars()).bottom
            root.setPadding(0, insetTop, 0, 0)
            historyList.setPadding(0, 0, 0, 64.dp(requireContext()) + insetBottom)
            historyScanReceiptButton.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insetBottom + 8.dp(requireContext())
            }
            insets
        }

        initHistoryList(animate = savedInstanceState == null && firstLoad)
        initScanButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstLoad = false
        permissionsRationale?.dismiss()
        permissionsRationale = null
    }

    private fun initHistoryList(animate: Boolean) = with(binding.historyList) {
        val historyAdapter = HistoryAdapter { id: String ->
            val args = bundleOf(RECEIPT_ID_KEY to id)
            navigator.navigate(DETAILS_FRAGMENT, args)
        }

        setHasFixedSize(true)
        adapter = historyAdapter
        layoutManager = LinearLayoutManager(context)
        layoutAnimation = if (animate) loadLayoutAnimation(context, R.anim.layout_animation_fade_in) else null

        viewModel.receiptsHistory.observe(viewLifecycleOwner) { data ->
            scheduleLayoutAnimation()
            historyAdapter.submitList(data)
        }
    }

    private fun initScanButton() = with(binding.historyScanReceiptButton) {
        onClick = {
            val hasPermission = checkSelfPermission(context, CAMERA) == PERMISSION_GRANTED
            if (hasPermission) {
                navigator.navigate(SCANNER_FRAGMENT)
            } else {
                cameraPermissionLauncher.launch(CAMERA)
            }
        }
    }
}