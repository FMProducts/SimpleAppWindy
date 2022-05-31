package fm.coding.windy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates.notNull

class MainViewModel: ViewModel() {

    private val _printFlow = MutableStateFlow<Int?>(null)
    val printFlow = _printFlow.asStateFlow()

    private val sumFlow = MutableSharedFlow<Int>(replay = 1000, extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var nFlow = MutableSharedFlow<Int>(replay=1000, extraBufferCapacity = 100, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    var lastValue = 0
    init {
        viewModelScope.launch {
            sumFlow.collectLatest{
                lastValue += it
                _printFlow.emit(lastValue)
            }
        }

        viewModelScope.launch {
            nFlow.collect {
                val delayInMillis = it * 100
                delay(delayInMillis.toLong())
                sumFlow.emit(it)
            }
        }

    }

    fun onFlowButtonClicked(value: Int) = viewModelScope.launch {
        _printFlow.tryEmit(null)
        lastValue = 0
        for(index in 0 until value){
            nFlow.emit(index + 1)
        }
    }

}
