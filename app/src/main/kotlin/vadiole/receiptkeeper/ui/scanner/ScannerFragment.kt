package vadiole.receiptkeeper.ui.scanner

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.KEYBOARD_TAP
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import vadiole.core.base.BaseActivity
import vadiole.core.base.BaseFragment
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.BuildConfig.URL_VALIDATOR
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentScannerBinding

class ScannerFragment : BaseFragment<ScannerViewModel, FragmentScannerBinding>() {

    override val viewModel: ScannerViewModel by viewModels()

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentScannerBinding.inflate(inflater)

    override fun onBindingCreated(savedInstanceState: Bundle?) {
        setOnApplyWindowInsetsListener(binding.scannerBackButton) { v, insets ->
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top, 0, 0)
            }
            insets
        }
        setOnApplyWindowInsetsListener(binding.scannerTitle) { v, insets ->
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(0, insets.getInsets(WindowInsetsCompat.Type.statusBars()).top, 0, 0)
            }
            insets
        }

        initBackButton()
        initScannerView()
    }

    override fun onResume() = with(activity as BaseActivity) {
        super.onResume()
        insetsControllerX?.isAppearanceLightNavigationBars = false
        insetsControllerX?.isAppearanceLightStatusBars = false
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        binding.scannerView.resume()
    }

    override fun onPause() = with(activity as BaseActivity) {
        super.onPause()
        val isNightMode = Build.VERSION.SDK_INT >= 30 && resources.configuration.isNightModeActive
        insetsControllerX?.isAppearanceLightStatusBars = !isNightMode
        insetsControllerX?.isAppearanceLightNavigationBars = !isNightMode
        requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
        binding.scannerView.pause()
    }


    override fun onBackPressed(): Boolean {
        return false
    }

    private fun initBackButton() = with(binding) {
        scannerBackButton.onClick = {
            requireActivity().onBackPressed()
        }
    }

    private fun initScannerView() = with(binding) {
        val urlRegex = Regex(URL_VALIDATOR)
        val errorToast = Toast.makeText(context, R.string.scan_error, Toast.LENGTH_SHORT)

        scannerView.keepScreenOn = true
        scannerView.decodeContinuous { result ->
            if (!isVisible) return@decodeContinuous
            val context = context ?: return@decodeContinuous

            errorToast.cancel()
            val url = result.text

            if (urlRegex.matches(url)) {
                val uri = Uri.parse(result.text)
                val id = uri.getQueryParameter("id")
                val date = uri.getQueryParameter("date")

                Toast.makeText(context, "Success: $id, $date", LENGTH_LONG).show()
                scannerView.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)
                scannerView.stopDecoding()
            } else {
                errorToast.show()
            }
        }
    }
}