package fm.coding.windy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import fm.coding.windy.databinding.ActivityMainBinding
import kotlin.properties.Delegates.notNull

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding by notNull()
    private var viewModel: MainViewModel by notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContentView(binding.root)

        setupListener()
    }


    private fun setupListener() = with(binding){
        runFlowButton.setOnClickListener {
            // run flow
        }

    }
}