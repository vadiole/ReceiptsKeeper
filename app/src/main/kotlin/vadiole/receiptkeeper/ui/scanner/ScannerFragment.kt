package vadiole.receiptkeeper.ui.scanner

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.graphics.Color
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.KEYBOARD_TAP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.webkit.*
import android.webkit.WebSettings.FORCE_DARK_OFF
import android.webkit.WebSettings.FORCE_DARK_ON
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat.Type.navigationBars
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import vadiole.core.base.BaseActivity
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.dp
import vadiole.core.extensions.observe
import vadiole.core.utils.hasNetworkConnection
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.BuildConfig.*
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentScannerBinding
import vadiole.receiptkeeper.model.raw.ReceiptRaw
import vadiole.receiptkeeper.ui.MainActivity
import vadiole.receiptkeeper.ui.details.DetailsFragment
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.URL

@AndroidEntryPoint
class ScannerFragment : BaseFragment<ScannerViewModel, FragmentScannerBinding>() {

    override val viewModel: ScannerViewModel by viewModels()
    private val jsonConfig = Json { ignoreUnknownKeys = true }
    private val urlValidator = Regex(URL_VALIDATOR)

    private var receiptsId: String? = null
    private var receiptDate: String? = null
    private var receiptUrl: String? = null

    private val loadingHandler = Handler(Looper.getMainLooper())
    private val loadReceiptRunnable = object : Runnable {
        override fun run() {
            val url = receiptUrl ?: return
            if (requireContext().hasNetworkConnection) {
                setTitle(R.string.scan_loading)
                binding.scannerWebview.loadUrl(url)
            } else {
                setTitle(R.string.scan_error_no_network, shake = true)
                loadingHandler.postDelayed(this, 2000)
            }
        }
    }
    private val invalidQrRunnable = object : Runnable {
        override fun run() {
            if (binding.scannerTitle.text != getString(R.string.scan_invalid)) return
            binding.scannerTitle.text = getString(R.string.scan_qr_code)
        }
    }

    private val qrCallback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (!isVisible) return

            val scannedUrl = result.text

            if (urlValidator.matches(scannedUrl)) {
                setTitle(R.string.scan_loading)

                val uri = Uri.parse(scannedUrl)
                receiptsId = uri.getQueryParameter("id")
                receiptDate = uri.getQueryParameter("date")

                binding.scannerView.stopDecoding()
                binding.root.performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)

                loadReceipt(scannedUrl)
            } else {
                setTitle(R.string.scan_invalid, shake = true)
            }
        }
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentScannerBinding.inflate(inflater)

    override fun onBindingCreated(savedInstanceState: Bundle?): Unit = with(binding) {
        setOnApplyWindowInsetsListener(root) { _, insets ->
            val insetTop = insets.getInsets(statusBars()).top
            val insetBottom = insets.getInsets(navigationBars()).bottom
            scannerBackButton.updateLayoutParams<MarginLayoutParams> {
                setMargins(0, insetTop, 0, 0)
            }
            scannerTitle.updateLayoutParams<MarginLayoutParams> {
                setMargins(0, insetTop, 0, 0)
            }
            scannerWebview.updateLayoutParams<MarginLayoutParams> {
                setMargins(0, insetTop + 56.dp(requireContext()), 0, insetBottom)
            }
            insets
        }

        initBackButton()
        initScannerView()
        initWebView()

        viewModel.result.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { receiptId ->
                    if (receiptId != null) {
                        val args = bundleOf(DetailsFragment.RECEIPT_ID_KEY to receiptId)
                        navigator.navigate(MainActivity.DETAILS_FRAGMENT, args)
                    }
                },
                onFailure = {
                    Toast.makeText(requireContext(), R.string.scan_error_save, Toast.LENGTH_SHORT).show()
                }
            )
        }

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
        val isNightMode = VERSION.SDK_INT >= 30 && resources.configuration.isNightModeActive
        insetsControllerX?.isAppearanceLightStatusBars = !isNightMode
        insetsControllerX?.isAppearanceLightNavigationBars = !isNightMode
        requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
        binding.scannerView.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadingHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed(): Boolean {
        navigator.navigate(MainActivity.HISTORY_FRAGMENT)
        return true
    }

    private fun initBackButton() = with(binding.scannerBackButton) {
        onClick = {
            requireActivity().onBackPressed()
        }
    }

    private fun initScannerView() = with(binding.scannerView) {
        keepScreenOn = true
        decodeContinuous(qrCallback)
    }

    @SuppressLint("SetJavaScriptEnabled")
    // this method looks scary, but what can you do, I had to parse via WebView
    private fun initWebView() = with(binding.scannerWebview) {
        with(settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            useWideViewPort = true
            if (VERSION.SDK_INT >= VERSION_CODES.R) {
                val isNightMode = resources.configuration.isNightModeActive
                forceDark = if (isNightMode) FORCE_DARK_ON else FORCE_DARK_OFF
            }
        }
        webViewClient = object : WebViewClient() {
            // clearing web page when loaded
            override fun onPageFinished(view: WebView, url: String?) {
                view.visibility = View.INVISIBLE
                view.loadUrl(cleanPageScript)
                view.setBackgroundColor(Color.TRANSPARENT)
            }

            // intercepting qr and captcha loading
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                try {
                    val requestUrl = request.url.toString()
                    when {
                        requestUrl.contains(URL_SLICE_RECEIPT) -> {
                            receiptUrl = null
                            val urlConnection = URL(requestUrl).openConnection() as HttpURLConnection
                            if (urlConnection.responseCode == HTTP_OK) {
                                val encoded = urlConnection.inputStream.bufferedReader().readText()
                                val receiptRaw = jsonConfig.decodeFromString<ReceiptRaw>(encoded)
                                viewModel.saveReceipt(receiptRaw, receiptsId!!, receiptDate!!)
                                return null
                            }
                        }
                        requestUrl.contains(URL_SLICE_CAPTCHA) -> {
                            receiptUrl = null
                            activity?.runOnUiThread {
                                view.loadUrl(cleanPageScript)
                                // dirty hack to avoid showing background dim
                                view.postDelayed(1000L) {
                                    view.isVisible = true
                                }
                            }
                            return null
                        }
                        else -> return null
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                activity?.runOnUiThread {
                    setTitle(R.string.scan_qr_code)
                    Toast.makeText(context, R.string.scan_error_connection, LENGTH_LONG).show()
                    binding.scannerView.decodeContinuous(qrCallback)
                }
                return null
            }
        }
    }

    private fun loadReceipt(url: String) {
        receiptUrl = url
        loadingHandler.post(loadReceiptRunnable)
        loadingHandler.postDelayed(10000) { // timeout
            if (receiptUrl != null) {
                setTitle(R.string.scan_qr_code)
                Toast.makeText(requireContext(), R.string.scan_error_connection, LENGTH_LONG).show()
                binding.scannerView.decodeContinuous(qrCallback)
                receiptUrl = null
            }
        }
    }

    private fun setTitle(@StringRes title: Int, shake: Boolean = false) = with(binding) {
        scannerTitle.removeCallbacks(invalidQrRunnable)
        scannerTitle.text = getString(title)
        scannerTitle.clearAnimation()

        if (shake) {
            val shakeAnim = TranslateAnimation(0f, 10f, 0f, 0f).apply {
                interpolator = CycleInterpolator(7f)
                duration = 500
            }
            scannerTitle.startAnimation(shakeAnim)
        }

        if (title == R.string.scan_invalid) {
            scannerTitle.postDelayed(invalidQrRunnable, 3000)
        }
    }

    companion object {
        private const val cleanPageScript = """javascript:(() => {
            document.getElementsByClassName("p-grid p-nogutter main-page")[0].style.display = "none";
            document.body.style.background = 0;
            document.body.childNodes[document.body.childNodes.length - 1].firstChild.style.display = "none";
        })()"""
    }
}