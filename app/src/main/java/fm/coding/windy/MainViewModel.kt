package fm.coding.windy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _printFlow = MutableStateFlow<Int?>(null)
    val printFlow = _printFlow.asStateFlow()

    private val sumFlow = MutableSharedFlow<Int>(replay = 1000,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var nFlow = mutableListOf<Flow<Int>>()

    init {
        viewModelScope.launch {
            sumFlow.collectLatest {
                val lastValue = _printFlow.value ?: 0
                _printFlow.emit(lastValue + it)
            }
        }

    }

    fun onFlowButtonClicked(value: Int) {
        _printFlow.tryEmit(null)
        nFlow.clear()

        for (index in 0 until value) {
            val flow = flow {
                val delayInMillis = (index + 1) * 100
                delay(delayInMillis.toLong())
                emit(index + 1)
            }.shareIn(viewModelScope, SharingStarted.Lazily)
            nFlow.add(flow)
        }

        nFlow.forEach {
            viewModelScope.launch {
                it.collect { index ->
                    sumFlow.tryEmit(index)
                }
            }
        }
    }

}
