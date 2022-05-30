package fm.coding.windy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _printFlow = MutableStateFlow<Int?>(null)
    val printFlow = _printFlow.asStateFlow()

    private val nFlow = MutableSharedFlow<Int>(replay = 1000)

    var tempValue = 0

    init {
        viewModelScope.launch {
            nFlow.collect{
                tempValue += it
                _printFlow.tryEmit(tempValue)
            }
        }
    }

    fun onFlowButtonClicked(value: Int) = viewModelScope.launch{
        _printFlow.tryEmit(null)
        tempValue = 0
        for(index in 0 until value){
            val delayInMillis = (index + 1) * 100
            delay(delayInMillis.toLong())
            nFlow.tryEmit(index + 1)
        }


    }

}
