package vadiole.receiptkeeper.ui.history

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.animation.AnimationUtils.loadLayoutAnimation
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.observe
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.BuildConfig.APPLICATION_ID
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentHistoryBinding
import vadiole.receiptkeeper.ui.MainActivity
import vadiole.receiptkeeper.ui.history.list.HistoryAdapter

@AndroidEntryPoint
class HistoryFragment : BaseFragment<HistoryViewModel, FragmentHistoryBinding>() {

    override val viewModel: HistoryViewModel by activityViewModels()

    private val cameraRequestCallback: (Boolean) -> Unit = { isGranted ->
        if (isGranted) {
            navigator.navigate(MainActivity.SCANNER_FRAGMENT)
        } else {
            val canRequest = shouldShowRequestPermissionRationale(requireActivity(), CAMERA)

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(R.string.permission_camera_message)
                .setPositiveButton(R.string.action_grant) { dialog, _ ->
                    dialog.dismiss()

                    if (canRequest) {
                        cameraRequest.launch(CAMERA)
                    } else {
                        val uri = Uri.fromParts("package", APPLICATION_ID, null)
                        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
                        startActivity(intent)
                    }
                }
                .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private val cameraRequest = registerForActivityResult(RequestPermission(), cameraRequestCallback)

    private var firstLoad = true

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentHistoryBinding.inflate(inflater)

    override fun onBindingCreated(savedInstanceState: Bundle?) {
        setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(
                0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
                0, insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            )
            insets
        }

        initHistoryList(savedInstanceState == null && firstLoad)
        initScanButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firstLoad = false
    }

    private fun initHistoryList(animate: Boolean) = with(binding) {
        val historyAdapter = HistoryAdapter { id: String ->
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
            if (checkSelfPermission(requireContext(), CAMERA) == PERMISSION_GRANTED) {
                navigator.navigate(MainActivity.SCANNER_FRAGMENT)
            } else {
                cameraRequest.launch(CAMERA)
            }
        }
    }
}