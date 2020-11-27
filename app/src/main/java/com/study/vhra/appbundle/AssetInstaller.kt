package com.study.vhra.appbundle

import android.content.Context
import android.util.Log
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener

class AssetInstaller(context: Context?) : AssetPackStateUpdateListener {

    var packManager: AssetPackManager = AssetPackManagerFactory.getInstance(context!!)

    fun installPack(packName: String) {
        packManager.fetch(listOf(packName)).addOnCompleteListener {
            Log.d("AssetInstaller", "starting download ${packName}")

        }.addOnFailureListener {
            Log.d("AssetInstaller", "failed download ${packName}", it)

        }
    }

    override fun onStateUpdate(state: AssetPackState) {
        Log.d("AssetInstaller", "onStateUpdate: name:" + state.name() + " status: " + state.status())
    }

    init {
        packManager.registerListener(this)
    }
}