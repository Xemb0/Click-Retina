package com.click.retina

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.click.retina.databinding.ActivityMainBinding
import com.click.retina.network.NetworkChecker
import com.click.retina.database.viewmodels.MainViewModel
import com.click.retina.database.viewmodels.MainViewModelFactory
import com.click.retina.database.viewmodels.DataRepository
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var bottomSheet: RetryBottomSheetFragment? = null
    private lateinit var networkChecker: NetworkChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataRepository = DataRepository()
        networkChecker = NetworkChecker(getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager)
        val viewModelFactory = MainViewModelFactory(dataRepository, networkChecker)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        val circularProgressIndicator = binding.overallScrollInclude.circularProgressIndicator

        lifecycleScope.launch {
            animateProgress(circularProgressIndicator, 53, 100)
        }

        viewModel.data.observe(this) { data ->
            if (data != null) {
                binding.txTitleData.text = data.title
                binding.txDescriptionData.text = data.description
                bottomSheet?.dismiss()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                showRetryBottomSheet()
            }
        }

        networkChecker.setOnNetworkAvailable {
            viewModel.fetchData()
        }

        networkChecker.setOnNetworkLost {
            showRetryBottomSheet()
        }

        setupButtonListeners()
    }

    private suspend fun animateProgress(progressIndicator: CircularProgressIndicator, progress: Int, max: Int) {
        val animator = ValueAnimator.ofInt(0, progress)
        delay(1000)
        animator.duration = 500
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            progressIndicator.progress = animatedValue
            binding.overallScrollInclude.progressText.text = animatedValue.toString()
        }
        animator.start()
    }

    private fun setupButtonListeners() {
        binding.btnCopyTitle.setOnClickListener {
            copyToClipboard(binding.txTitleData.text.toString())
        }

        binding.btnShareTitle.setOnClickListener {
            shareText(binding.txTitleData.text.toString())
        }

        binding.btnCopyDescription.setOnClickListener {
            copyToClipboard(binding.txDescriptionData.text.toString())
        }

        binding.btnShareDescription.setOnClickListener {
            shareText(binding.txDescriptionData.text.toString())
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share text via"))
    }

    private fun showRetryBottomSheet() {
        if (bottomSheet == null) {
            bottomSheet = RetryBottomSheetFragment().apply {
                retryAction = {
                    viewModel.fetchData()
                }
            }
            bottomSheet?.show(supportFragmentManager, "RetryBottomSheet")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
