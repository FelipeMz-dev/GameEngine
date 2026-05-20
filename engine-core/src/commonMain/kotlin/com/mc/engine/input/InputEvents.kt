package com.mc.engine.input

import com.mc.engine.math.Vec2

/**
 * Evento de toque agnóstico de plataforma.
 */
data class TouchEvent(
    val id: Int,
    val position: Vec2,
    val phase: TouchPhase
)

enum class TouchPhase {
    Began, Moved, Ended, Cancelled
}

/**
 * Evento de teclado agnóstico de plataforma.
 */
data class KeyEvent(
    val key: Key,
    val type: KeyEventType
)

enum class KeyEventType {
    Pressed, Released
}

enum class Key {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    Space, Enter, Escape, Backspace, Tab, Shift, Control, Alt,
    ArrowUp, ArrowDown, ArrowLeft, ArrowRight,
    // Agregar más teclas según sea necesario
}

/**
 * Botón de mouse agnóstico de plataforma.
 */
enum class MouseButton {
    Left, Right, Middle, Unknown
}
