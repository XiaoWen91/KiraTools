package io.github.angbang852.manjiao.hook

import io.github.angbang852.manjiao.data.Prefs
import io.github.angbang852.manjiao.util.Logger
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

object StealthVisitorHook {
    fun hook(module: XposedModule, cl: ClassLoader) {
        try {
            val c = Class.forName("j6l.u7", false, cl)
            val m = c.getDeclaredMethod("b", String::class.java)
            module.hook(m)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .setId("kira.stealth.visitor.path")
                .intercept { chain ->
                    if (Prefs.bool(Prefs.K_SPARK_STEALTH_VISITOR, false)) {
                        Logger.d("stealth visitor: suppress visitor path report via j6l.u7.b")
                        null
                    } else {
                        chain.proceed()
                    }
                }
        } catch (t: Throwable) {
            Logger.d("stealth visitor hook miss: ${t.message}")
        }
    }
}
