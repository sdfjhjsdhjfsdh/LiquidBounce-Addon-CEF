package net.liquidflame.liquidflame.cef

import net.liquidflame.liquidflame.cef.backend.AcceleratedPaint
import net.liquidflame.liquidflame.cef.backend.CefBrowserBackend
import net.liquidflame.liquidflame.cef.error.CefQuickFixes
import net.liquidflame.liquidflame.features.addon.LiquidFlameAddon
import net.liquidflame.liquidflame.integration.backend.BrowserBackendProvider

/**
 * Offers Chromium, through CEF, as the browser of the client's interface. LiquidFlame comes with it.
 */
class CefAddon : LiquidFlameAddon() {

    override fun onInitialize() {
        registerQuickFix(CefQuickFixes.JCEF_ISNT_COMPATIBLE_WITH_THAT_SYSTEM)
        registerQuickFix(CefQuickFixes.D3D11_UNSATISFIED_LINK)
        registerQuickFix(CefQuickFixes.JCEF_UNSATISFIED_LINK)

        registerBrowserBackend(
            BrowserBackendProvider(
                "cef",
                "Chromium",
                "The browser LiquidFlame comes with (Chromium).",
                create = ::CefBrowserBackend
            )
        )

        // Its setting has to exist before the global settings load, which happens ahead of the first browser
        AcceleratedPaint
    }

}
