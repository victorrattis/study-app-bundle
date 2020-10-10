package com.study.vhra.appbundle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {
    private lateinit var manager: SplitInstallManager

    private val listener = SplitInstallStateUpdatedListener { state ->
        val names = state.moduleNames().joinToString(" - ")
        Log.d("devlog", "status: ${state.status()}, name: $names")
        when(state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {}
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
            SplitInstallSessionStatus.INSTALLED -> {
                onInstalled(names)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                val max = state.totalBytesToDownload().toInt()
                val progress = state.bytesDownloaded().toInt()
                Log.d("devlog", "INSTALLING: max= $max, progress= $progress")
            }
            SplitInstallSessionStatus.FAILED -> { }
            else -> {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text_app_version)?.text = BuildConfig.VERSION_NAME
        findViewById<Button>(R.id.btn_install_content1)?.setOnClickListener {
            installBundle("content1")
        }
        findViewById<ImageView>(R.id.btn_content1_refresh)?.setOnClickListener { refreshContent1() }

        manager = SplitInstallManagerFactory.create(this)
        refreshContent1()
    }

    override fun onResume() {
        super.onResume()
        manager.registerListener(listener)
    }

    override fun onStop() {
        super.onStop()
        manager.unregisterListener(listener)
    }

    private fun onInstalled(name: String) {
        showInstallCompleted(name)
        refreshContent1()
    }

    private fun installBundle(moduleName: String) {
        if (manager.installedModules.contains(moduleName)) {
            onInstalled(moduleName)
        }

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        // Load and install the requested feature module.
        manager.startInstall(request)
    }

    private fun refreshContent1() {
        val resId = resources.getIdentifier(
            "content1_image",
            "drawable",
            "com.study.vhra.appbundle.content1"
        )
        findViewById<ImageView>(R.id.image_content1_result).setImageResource(resId)
    }

    private fun setContent1Result() {
        findViewById<ImageView>(R.id.image_content1_result)
    }

    private fun showInstallCompleted(name: String) {
        Toast.makeText(
            this,
            "Installed Bundle $name Successfully!",
            Toast.LENGTH_LONG
        ).show()
    }
}
