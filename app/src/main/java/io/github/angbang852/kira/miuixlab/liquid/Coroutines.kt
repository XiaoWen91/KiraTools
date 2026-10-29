package io.github.angbang852.kira.miuixlab.liquid

import androidx.compose.runtime.withFrameNanos

suspend fun awaitFrame() {
    withFrameNanos { }
}
