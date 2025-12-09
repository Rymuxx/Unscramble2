package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para la pantalla del juego.
 */
class GameViewModel : ViewModel() {

    // Propiedad de respaldo para evitar actualizaciones de estado de otras clases
    private val _uiState = MutableStateFlow(GameUiState())
    /**
     * Flujo de estado público inmutable para el estado de la IU.
     */
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /**
     * Adivinanza actual del usuario.
     */
    var userGuess by mutableStateOf("")
        private set

    // Conjunto de palabras utilizadas en el juego para evitar repeticiones.
    private var usedWords: MutableSet<String> = mutableSetOf()
    // Palabra actual para que el usuario la adivine.
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    /**
     * Restablece el juego a su estado inicial.
     */
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    /**
     * Actualiza la adivinanza del usuario.
     * @param guessedWord La palabra adivinada por el usuario.
     */
    fun updateUserGuess(guessedWord: String) {
        userGuess = guessedWord
    }

    /**
     * Comprueba si la adivinanza del usuario es correcta.
     * Si la adivinanza es correcta, actualiza la puntuación y el estado del juego.
     * De lo contrario, marca la adivinanza como incorrecta.
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            // La adivinanza del usuario es correcta, aumenta la puntuación
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // La adivinanza del usuario es incorrecta, muestra un error
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // Restablecer la adivinanza del usuario
        updateUserGuess("")
    }

    /**
     * Omite la palabra actual y pasa a la siguiente.
     */
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Restablecer la adivinanza del usuario
        updateUserGuess("")
    }

    /**
     * Actualiza el estado del juego con una nueva puntuación.
     * Si se han adivinado todas las palabras, el juego termina.
     * De lo contrario, pasa a la siguiente palabra.
     * @param updatedScore La nueva puntuación.
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            // Se han adivinado todas las palabras, el juego ha terminado
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            // Pasar a la siguiente palabra
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    /**
     * Mezcla los caracteres de una palabra.
     * @param word La palabra a mezclar.
     * @return La palabra mezclada.
     */
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Mezclar la palabra
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    /**
     * Elige una palabra al azar de la lista de todas las palabras y la mezcla.
     * Se asegura de que la palabra no se haya utilizado antes.
     * @return La palabra mezclada.
     */
    private fun pickRandomWordAndShuffle(): String {
        // Continuar eligiendo una nueva palabra al azar hasta obtener una que no se haya utilizado antes
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}