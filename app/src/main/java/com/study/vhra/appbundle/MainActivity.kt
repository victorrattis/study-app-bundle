package com.study.vhra.appbundle

import android.app.WallpaperInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.ServiceInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getWallpaperServices(this).forEach {
            WallpaperInfo(this, it).also { info ->
                findViewById<ImageView>(R.id.imageView)?.setImageDrawable(
                    info.loadThumbnail(packageManager))
                Log.d("devlog", "enabled: " + info.serviceInfo.enabled)
                Log.d("devlog", "label: " + info.loadLabel(packageManager))
            }
            Log.d("devlog", "module: " + getServiceModuleName(it.serviceInfo))
        }
    }

    private fun getWallpaperServices(context: Context): MutableList<ResolveInfo> {
        val intent = Intent("android.service.wallpaper.WallpaperService").apply {
            this.`package` = context.packageName
        }
        return context.packageManager.queryIntentServices(
            intent,
            PackageManager.GET_META_DATA or PackageManager.MATCH_DISABLED_COMPONENTS)
    }

    private fun getServiceModuleName(serviceInfo: ServiceInfo): String? =
        getString(serviceInfo.metaData.getInt("module"))
}
