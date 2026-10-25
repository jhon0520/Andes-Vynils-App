package com.dev.wine.music.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Entry point for Hilt: generates the app-wide dependency container. */
@HiltAndroidApp
class WineMusicApp : Application()
