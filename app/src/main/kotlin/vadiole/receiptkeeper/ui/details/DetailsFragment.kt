package vadiole.receiptkeeper.ui.details

import android.content.ComponentName
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.*
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import android.graphics.Color.BLACK
import android.graphics.PorterDuff.Mode.DST_OVER
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.*
import androidx.browser.customtabs.CustomTabsIntent.SHARE_STATE_OFF
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat.getFont
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.PrecomputedTextCompat
import androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener
import androidx.core.view.WindowInsetsCompat.*
import androidx.core.view.WindowInsetsCompat.Type.*
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vadiole.core.base.BaseFragment
import vadiole.core.extensions.dp
import vadiole.core.extensions.observe
import vadiole.core.utils.DebouncingOnClick
import vadiole.core.utils.onClick
import vadiole.receiptkeeper.R
import vadiole.receiptkeeper.databinding.FragmentDetailsBinding
import vadiole.receiptkeeper.ui.MainActivity
import java.io.File

@AndroidEntryPoint
class DetailsFragment() : BaseFragment<DetailsViewModel, FragmentDetailsBinding>() {

    constructor(args: Bundle?) : this() {
        arguments = args
    }

    override val viewModel: DetailsViewModel by viewModels()

    override val autoStartTransition = false

    private val receiptTextPaint = Paint().apply {
        textAlign = Paint.Align.LEFT
    }

    private var receiptUrl: Uri? = null

    private val receiptId: String get() = requireArguments().getString(RECEIPT_ID_KEY)!!

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentDetailsBinding.inflate(inflater)

    override fun onBindingCreated(savedInstanceState: Bundle?): Unit = with(requireContext()) {
        setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val top = insets.getInsets(statusBars()).top
            val bottom = insets.getInsets(navigationBars()).bottom
            v.setPadding(0, top, 0, bottom)
            insets
        }

        initBackButton()
        initDeleteButton()
        initOnlineButton()
        initShareButton()

        observeDelete()
        observeError()
        observeReceipt(didLoad = {
            startPostponedEnterTransition()
        })

        viewModel.loadReceipt(receiptId)

        warmupCustomTabs()
    }

    override fun onBackPressed(): Boolean {
        navigator.navigate(MainActivity.HISTORY_FRAGMENT)
        return true
    }

    private fun initBackButton() = with(binding) {
        detailsBackButton.onClick = {
            requireActivity().onBackPressed()
        }
    }

    private fun initDeleteButton() = with(binding.detailsDeleteButton) {
        onClick = {
            AlertDialog.Builder(context)
                .setTitle(R.string.details_delete_alert_title)
                .setMessage(R.string.details_delete_alert_text)
                .setPositiveButton(R.string.action_delete) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteReceipt(requireArguments().getString(RECEIPT_ID_KEY)!!)
                }
                .setNegativeButton(R.string.action_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun initOnlineButton() = with(binding.detailsOnlineButton) {
        val icon = getDrawable(context, R.drawable.ic_close)!!
        val params = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(getColor(context, R.color.windowToolbar))
            .setSecondaryToolbarColor(getColor(context, R.color.windowToolbar))
            .setNavigationBarColor(Color.WHITE)
            .setNavigationBarDividerColor(Color.WHITE)
            .build()
        val chromeIntent = CustomTabsIntent.Builder()
            .setCloseButtonIcon(icon.toBitmap())
            .setDefaultColorSchemeParams(params)
            .setStartAnimations(context, R.anim.bottom_new_enter, R.anim.bottom_parent_exit)
            .setExitAnimations(context, R.anim.bottom_parent_enter, R.anim.bottom_new_exit)
            .setShareState(SHARE_STATE_OFF)
            .setUrlBarHidingEnabled(false)
            .setShowTitle(false)
            .build()

        setOnClickListener(DebouncingOnClick<Button>(1000) {
            val url = receiptUrl ?: return@DebouncingOnClick
            try {
                chromeIntent.launchUrl(context, url)
            } catch (e: Exception) { // no chrome tabs on device
                e.printStackTrace()
                try {
                    val webIntent = Intent(ACTION_VIEW).setData(url)
                    startActivity(webIntent)
                } catch (e: Exception) { // no browser on device
                    e.printStackTrace()
                    Toast.makeText(context, R.string.details_no_browser, LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun initShareButton() = with(binding.detailsShareButton) {
        setOnClickListener(DebouncingOnClick<Button>(1000) {
            val bitmap = with(binding.detailsTextview) {
                createBitmap(width, height, ARGB_8888).applyCanvas {
                    translate(-scrollX.toFloat(), -scrollY.toFloat())
                    draw(this)
                    drawColor(BLACK, SRC_ATOP)
                    drawColor(Color.WHITE, DST_OVER)
                }
            }
            val file = File(context.cacheDir, "receipt-$receiptId.png")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
            val fileUri = FileProvider.getUriForFile(requireContext(), "vadiole.receiptkeeper.share", file)
            val intent = ShareCompat.IntentBuilder(requireActivity())
                .setStream(fileUri)
                .setType("image/*")
                .intent
                .setFlags(FLAG_GRANT_READ_URI_PERMISSION)
                .setData(fileUri)
            startActivity(Intent.createChooser(intent, getString(R.string.share_with)))
        })
    }

    private fun observeReceipt(didLoad: () -> Unit) = with(binding.detailsTextview) {
        // random size to calculate the width of the text and adjust the size
        receiptTextPaint.textSize = 100f.dp(context)
        receiptTextPaint.typeface = getFont(context, R.font.sf_mono_regular)

        viewModel.receiptDetails.observe(viewLifecycleOwner) { receipt ->
            if (receipt == null) return@observe
            receiptUrl = receipt.url

            // text size calculation to display the receipt in full width
            val maxWidth = width - paddingLeft - paddingRight
            val receiptFirstLine = receipt.plainText.lines().first()
            val oldWidth = receiptTextPaint.measureText(receiptFirstLine)
            val newSize: Float = maxWidth.toFloat() / oldWidth * receiptTextPaint.textSize
            setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
            val params = TextViewCompat.getTextMetricsParams(this)
            val precomputed = withContext(Dispatchers.Default) {
                PrecomputedTextCompat.create(receipt.plainText, params)
            }
            TextViewCompat.setPrecomputedText(this, precomputed)

            didLoad.invoke()
        }
    }

    private fun observeDelete() = with(viewModel.deleteDone) {
        observe(viewLifecycleOwner) { deleted ->
            if (deleted) activity?.onBackPressed()
        }
    }

    private fun observeError() = with(viewModel.errorLoading) {
        observe(viewLifecycleOwner) { error ->
            if (error) {
                Toast.makeText(requireContext(), R.string.details_error_loading, LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
        }
    }

    private fun warmupCustomTabs() {
        val connection = object : CustomTabsServiceConnection() {
            override fun onServiceDisconnected(name: ComponentName) = Unit

            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                client.warmup(0)
                val session = client.newSession(null)
                session?.mayLaunchUrl(receiptUrl, null, null)
            }
        }
        val packageName = CustomTabsClient.getPackageName(requireContext(), null)
        CustomTabsClient.bindCustomTabsServicePreservePriority(requireContext(), packageName, connection)
    }

    companion object {
        const val RECEIPT_ID_KEY = "receipt_id_key"
    }
}