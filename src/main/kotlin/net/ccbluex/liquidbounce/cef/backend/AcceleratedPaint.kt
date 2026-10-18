package net.liquidflame.liquidflame.cef.backend

import com.mojang.blaze3d.platform.InputConstants
import net.liquidflame.liquidflame.cef.CefAccelerationSupport
import net.liquidflame.liquidflame.cef.CefAccelerationSupport.Support
import net.liquidflame.liquidflame.config.types.list.Tagged
import net.liquidflame.liquidflame.event.EventListener
import net.liquidflame.liquidflame.event.events.KeyboardKeyEvent
import net.liquidflame.liquidflame.event.events.WindowTitleEvent
import net.liquidflame.liquidflame.event.handler
import net.liquidflame.liquidflame.integration.backend.BrowserBackendManager
import net.liquidflame.liquidflame.integration.backend.browser.GlobalBrowserSettings
import net.liquidflame.liquidflame.integration.screen.ScreenManager
import net.liquidflame.liquidflame.utils.client.env
import net.liquidflame.liquidflame.utils.client.inGame
import net.liquidflame.liquidflame.utils.client.logger
import net.liquidflame.liquidflame.utils.client.mc

val isBrowserAccelerationDisabled = env("LB_BROWSER_DISABLE_ACCELERATION",
    "net.liquidflame.liquidflame.browser.disableAcceleration")?.toBoolean() ?: false

/**
 * Whether Chromium hands its frames over as GPU textures instead of pixels.
 */
object AcceleratedPaint : EventListener {

    private val mode = GlobalBrowserSettings.enumChoice("AcceleratedPaint", Mode.AUTO).onChanged {
        mc.execute(::reload)
    }

    var support = Support.UNSUPPORTED
        private set

    val isEnabled: Boolean
        get() = when (mode.get()) {
            Mode.AUTO -> support == Support.DEFAULT
            Mode.ON -> support != Support.UNSUPPORTED
            Mode.OFF -> false
        }

    private val isActive: Boolean
        get() = BrowserBackendManager.backend is CefBrowserBackend && BrowserBackendManager.isInitialized

    internal fun detect() {
        if (isBrowserAccelerationDisabled) {
            logger.warn("Environment variable 'LB_BROWSER_DISABLE_ACCELERATION' is set to 'true'.")
            return
        }

        support = CefAccelerationSupport.getAccelerationSupport()
    }

    private fun reload() {
        if (isActive) {
            ScreenManager.restart()
            mc.updateTitle()
        }
    }

    @Suppress("unused")
    private val keyHandler = handler<KeyboardKeyEvent> { event ->
        if (!event.isPressed || event.scanCode != InputConstants.KEY_F12 || inGame || !isActive) {
            return@handler
        }

        if (support == Support.UNSUPPORTED) {
            logger.warn("Accelerated paint is not supported on this system.")
            return@handler
        }

        mode.set(if (isEnabled) Mode.OFF else Mode.ON)
        logger.info("Accelerated paint is now ${if (isEnabled) "on" else "off"}.")
    }

    @Suppress("unused")
    private val titleHandler = handler<WindowTitleEvent> { event ->
        if (!isActive || !isEnabled) {
            return@handler
        }

        event.title.append(" | Accelerated Paint is ON")
        if (!inGame) {
            event.title.append(" [Hotkey: F12]")
        }
    }

    enum class Mode(override val tag: String) : Tagged {
        AUTO("Auto"),
        ON("On"),
        OFF("Off")
    }

}
