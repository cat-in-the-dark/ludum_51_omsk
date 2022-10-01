package org.catinthedark.jvcrplotter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.catinthedark.jvcrplotter.game.MainGame

object DesktopLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val config = Lwjgl3ApplicationConfiguration()
        config.setTitle("Drop")
        config.setWindowedMode(800, 480)
        config.useVsync(true)
        config.setForegroundFPS(60)
        config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 2)
        Lwjgl3Application(MainGame(), config)
    }
}
