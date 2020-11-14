package com.study.vhra.appbundle

import android.app.Activity
import android.util.Log
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.lang.Exception

class BundleInstaller constructor(
    private val manager: SplitInstallManager
): SplitInstallStateUpdatedListener {
    private var listener: Listener? = null
    private var activity: Activity? = null

    interface Listener {
        fun onCompleted(bundleName: String)
        fun onFailed(bundleName: String)
    }

    init {
        Log.d("devlog", "Init BundleInstaller class")
        manager.registerListener(this)
    }

    private fun getStatusName(status: Int?): String {
        return when (status) {
            SplitInstallSessionStatus.DOWNLOADED -> "DOWNLOADED"
            SplitInstallSessionStatus.INSTALLING -> "INSTALLING"
            SplitInstallSessionStatus.INSTALLED -> "INSTALLED"
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> "REQUIRES_USER_CONFIRMATION"
            SplitInstallSessionStatus.FAILED -> "FAILED"
            SplitInstallSessionStatus.DOWNLOADING -> "DOWNLOADING"
            SplitInstallSessionStatus.PENDING -> "PENDING"
            SplitInstallSessionStatus.CANCELING -> "CANCELING"
            SplitInstallSessionStatus.CANCELED -> "CANCELED"
            else -> "UNKNOWN"
        }
    }

    override fun onStateUpdate(state: SplitInstallSessionState?) {
        val names = state?.moduleNames()?.joinToString(" - ") ?: ""

        Log.d("devlog", "onStateUpdate: ${getStatusName(state?.status())}, name: $names, sessionId: ${state?.sessionId()}")
        when(state?.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val max = state.totalBytesToDownload()
                val progress = state.bytesDownloaded()
                val percetange = progress*100/max
                Log.d("devlog", "DOWNLOADING: max= $max, progress= $progress => $percetange%")
            }
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                activity?.let {
                    manager.startConfirmationDialogForResult(state, it, 1000)
                }
            }
            SplitInstallSessionStatus.INSTALLED -> {
                onBundleInstalled(names)
            }
            SplitInstallSessionStatus.INSTALLING -> {
                val max = state.totalBytesToDownload().toInt()
                val progress = state.bytesDownloaded().toInt()
                Log.d("devlog", "INSTALLING: max= $max, progress= $progress")
            }
            SplitInstallSessionStatus.FAILED -> {
                onBundleFailed(names)
            }
            SplitInstallSessionStatus.UNKNOWN -> {
                onBundleFailed(names)
            }
            else -> {}
        }
    }

    fun attachActivity(activity: Activity) {
        Log.d("devlog", "attachActivity")
        this.activity = activity
    }

    fun removeActivity() {
        Log.d("devlog", "removeActivity")
        this.activity = null
    }

    fun register(listener: Listener) {
        this.listener = listener
    }

    fun unregister() {
        this.listener = null
    }

    fun installBundle(bundleName: String) {
        Log.d("devlog", "install Bundle $bundleName")
        if (isBundleInstalled(bundleName)) {
            onBundleInstalled(bundleName)
            return
        }
        /* TODO: Check if the bundle is in download */

        val request = SplitInstallRequest.newBuilder()
            .addModule(bundleName)
            .build()

        try {
            manager.startInstall(request)
                .addOnCompleteListener { task ->
                    Log.d("devlog", "Start Install on Complete: $bundleName")
                    if (task.isSuccessful) {
                        Log.d(
                            "devlog", "   task:" +
                                    " isSuccessful= ${task.isSuccessful}," +
                                    " isComplete= ${task.isComplete}," +
                                    " result= ${if (task.isComplete) task.result else 0}"
                        )
                    } else {
                        Log.d("devlog", "addOnCompleteListener: error")
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    Log.d("devlog", "Start Install on Failure: $bundleName")
                }
                .addOnSuccessListener { task ->
                    Log.d("devlog", "Start Install on Success: $bundleName")
                    Log.d("devlog", "   task: $task")
                }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun onBundleInstalled(bundleName: String) {
        Log.d("devlog", "on Bundle installed $bundleName")
        listener?.onCompleted(bundleName)
    }

    private fun onBundleFailed(bundleName: String) {
        Log.d("devlog", "on Bundle failed $bundleName")
        listener?.onFailed(bundleName)
    }

    private fun isBundleInstalled(bundleName: String) =
        manager.installedModules.contains(bundleName)
}