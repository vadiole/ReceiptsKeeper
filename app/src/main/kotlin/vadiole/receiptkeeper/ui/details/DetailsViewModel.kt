package vadiole.receiptkeeper.ui.details

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import vadiole.core.base.BaseViewModel
import vadiole.receiptkeeper.model.domain.HistoryDomain
import vadiole.receiptkeeper.usecase.DeleteReceiptUseCase
import vadiole.receiptkeeper.usecase.LoadReceiptUseCase
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val loadReceiptUseCase: LoadReceiptUseCase,
    private val deleteReceiptUseCase: DeleteReceiptUseCase,

    ) : BaseViewModel() {

    private val _receiptDetails = MutableStateFlow<HistoryDomain.Receipt?>(null)
    val receiptDetails = _receiptDetails.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    private val _errorLoading = MutableStateFlow(false)
    val errorLoading = _errorLoading.shareIn(viewModelScope, SharingStarted.Eagerly, replay = 0)

    private val _deleteDone = MutableStateFlow(false)
    val deleteDone = _deleteDone.shareIn(viewModelScope, SharingStarted.Lazily, replay = 0)

    fun loadReceipt(id: String) = viewModelScope.launch {
        loadReceiptUseCase.invoke(id).fold(
            onSuccess = {
                _receiptDetails.value = it
            },
            onFailure = {
                _errorLoading.value = true
            }
        )
    }

    fun deleteReceipt(id: String) = viewModelScope.launch {
        deleteReceiptUseCase.invoke(id)
        _deleteDone.value = true
    }
}