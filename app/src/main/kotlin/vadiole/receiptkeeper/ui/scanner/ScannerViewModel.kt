package vadiole.receiptkeeper.ui.scanner

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import vadiole.core.base.BaseViewModel
import vadiole.receiptkeeper.model.raw.ReceiptRaw
import vadiole.receiptkeeper.usecase.SaveReceiptUseCase
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val saveUseCase: SaveReceiptUseCase,
) : BaseViewModel() {

    private val _result = MutableStateFlow(Result.success<String?>(null))
    val result = _result.shareIn(viewModelScope, SharingStarted.Lazily, 0)

    fun saveReceipt(raw: ReceiptRaw, id: String, date: String) = viewModelScope.launch {
        saveUseCase.invoke(raw, id, date).fold(
            onSuccess = {
                _result.value = Result.success(id)

            }, onFailure = {
                _result.value = Result.failure(it)
            }
        )
    }
}