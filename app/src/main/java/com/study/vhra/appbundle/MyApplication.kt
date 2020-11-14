package com.study.vhra.appbundle

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory

class MyApplication : SplitCompatApplication() {
    val bundleInstaller by lazy { BundleInstaller(SplitInstallManagerFactory.create(this)) }
}