package com.study.vhra.appbundle

import android.service.wallpaper.WallpaperService

class Wallpaper1Service : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return object : Engine() {

        }
    }
}
