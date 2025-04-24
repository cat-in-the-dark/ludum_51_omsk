@file:JvmName("Lwjgl2Launcher")

package org.catinthedark.lwjgl2

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.catinthedark.alyoep.game.MainGame

/** Launches the desktop (LWJGL) application. */
fun main() {
    LwjglApplication(MainGame(), LwjglApplicationConfiguration().apply {
        title = "ALYOEP"
        width = 800
        height = 480
        vSyncEnabled = true
        allowSoftwareMode = true
        LwjglApplicationConfiguration.getDesktopDisplayMode().refreshRate + 1
        intArrayOf(128, 64, 32, 16).forEach{
            addIcon("libgdx$it.png", Files.FileType.Internal)
        }
    })
}
