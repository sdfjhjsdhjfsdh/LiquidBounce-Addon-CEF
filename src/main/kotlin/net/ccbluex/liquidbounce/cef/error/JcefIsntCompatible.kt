package net.liquidflame.liquidflame.cef.error

import net.liquidflame.liquidflame.utils.client.error.errors.ClientError

class JcefIsntCompatible : ClientError(
    message = "JCEF Isn't compatible",
    needToReport = false
) {
    fun readResolve(): Any = JcefIsntCompatible()
}
