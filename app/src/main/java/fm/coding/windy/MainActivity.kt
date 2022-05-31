package fm.coding.windy

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import fm.coding.windy.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.properties.Delegates.notNull

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding by notNull()
    private var viewModel: MainViewModel by notNull()

    private var start: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContentView(binding.root)

        setupListener()
        setupObserver()
    }


    private fun setupListener() = with(binding){
        runFlowButton.setOnClickListener {
            start = System.currentTimeMillis()
            val value = valueInput.text.toString().toIntOrNull() ?: 0 // default value 0
            viewModel.onFlowButtonClicked(value)
        }
    }

    private fun setupObserver() = with(viewModel){
        printFlow.onEach {
            if (it == null) binding.resultTextView.text = "" // if receive null clear text view
            else {
                val time = System.currentTimeMillis() - start
                binding.resultTextView.append("\n") // new line
                binding.resultTextView.append(it.toString())
                binding.resultTextView.append("- $time")
            }
        }.launchIn(lifecycleScope)
    }
}