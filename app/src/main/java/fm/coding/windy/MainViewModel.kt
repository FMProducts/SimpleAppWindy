package fm.coding.windy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _printFlow = MutableStateFlow<Int?>(null)
    val printFlow = _printFlow.asStateFlow()

    private val sumFlow = MutableSharedFlow<Int>(replay = 1000, extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var nFlow = mutableListOf<Flow<Int>>()

    var lastValue = 0


    init {
        viewModelScope.launch {
            sumFlow.collectLatest{
                lastValue += it
                _printFlow.emit(lastValue)
            }
        }

    }

    fun onFlowButtonClicked(value: Int) = viewModelScope.launch {
        _printFlow.tryEmit(null)
        lastValue = 0
        nFlow.clear()

        for(index in 0 until value){
            val flow = flowOf(index)
            nFlow.add(flow)
        }

        nFlow.forEach {
            it.collect { index ->
                val delayInMillis = (index + 1) * 100
                delay(delayInMillis.toLong())

                val result = index + 1
                sumFlow.tryEmit(result)
            }
        }
    }

}
