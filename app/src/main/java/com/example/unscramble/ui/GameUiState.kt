package com.example.unscramble.ui

/**
 * Clase de datos que representa el estado de la IU del juego.
 */
data class GameUiState(
    /** La palabra desordenada actual que el usuario tiene que adivinar. */
    val currentScrambledWord: String = "",
    /** El número de palabras que el usuario ha adivinado hasta ahora. */
    val currentWordCount: Int = 1,
    /** La puntuación actual del usuario. */
    val score: Int = 0,
    /** Si la última adivinanza del usuario fue incorrecta. */
    val isGuessedWordWrong: Boolean = false,
    /** Si el juego ha terminado. */
    val isGameOver: Boolean = false
)