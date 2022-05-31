package fm.coding.windy

import android.util.Log
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

    init {
        viewModelScope.launch {
            sumFlow.collectLatest{
                val lastValue = _printFlow.value ?: 0
                _printFlow.emit(lastValue + it)
            }
        }

    }

    fun onFlowButtonClicked(value: Int) = viewModelScope.launch {
        _printFlow.tryEmit(null)
        nFlow.clear()

        for(index in 0 until value){
            nFlow.add(flow{
                val startTime = System.currentTimeMillis()
                val delayInMillis = (index + 1) * 100
                delay(delayInMillis.toLong())

                val time = System.currentTimeMillis() - startTime
                Log.v(javaClass.name, "time: $time")
                emit(index)
            })
        }

        nFlow.forEach {
            it.collect { index ->
                val result = index + 1
                sumFlow.tryEmit(result)
            }
        }
    }

}
