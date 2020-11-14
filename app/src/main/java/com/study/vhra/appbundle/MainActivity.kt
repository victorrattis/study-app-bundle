package com.study.vhra.appbundle

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitcompat.SplitCompat

class MainActivity : AppCompatActivity(), BundleInstaller.Listener {
    private lateinit var bundleInstaller: BundleInstaller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bundleInstaller = (application as MyApplication).bundleInstaller

        findViewById<TextView>(R.id.text_app_version)?.text = BuildConfig.VERSION_NAME
        findViewById<Button>(R.id.btn_install_content1)?.setOnClickListener {
            bundleInstaller.installBundle("content1")
        }
        findViewById<ImageView>(R.id.btn_content1_refresh)?.setOnClickListener { refreshContent1() }

        refreshContent1()
    }

    override fun onResume() {
        super.onResume()
        bundleInstaller.attachActivity(this)
        bundleInstaller.register(this)
    }

    override fun onStop() {
        super.onStop()
        bundleInstaller.removeActivity()
        bundleInstaller.unregister()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        SplitCompat.installActivity(newBase)
    }

    // BundleInstaller.Listener implementation ---

    override fun onCompleted(bundleName: String) {
        showInstallCompleted(bundleName)
        refreshContent1()
    }

    override fun onFailed(bundleName: String) {
        showDownloadError(bundleName)
    }

    private fun refreshContent1() {
        SplitCompat.install(this)
        try {
            val resId = resources.getIdentifier(
                "content1_image",
                "drawable",
                "com.study.vhra.appbundle.content1"
            )
            findViewById<ImageView>(R.id.image_content1_result).setImageResource(resId)
        } catch (e: Exception) {
            Log.e("devlog", e.message, e)
        }
    }

    private fun showInstallCompleted(name: String) {
        Toast.makeText(
            this,
            "Installed Bundle $name Successfully!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showDownloadError(name: String) {
        Toast.makeText(
            this,
            "Error to download $name",
            Toast.LENGTH_LONG
        ).show()
    }
}
