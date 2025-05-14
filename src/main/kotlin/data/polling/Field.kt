package data.polling

import androidx.compose.runtime.MutableState
import domain.polling.PollingService

/**
 * DSL для «поля», которое опрашивается в реальном времени.
 *
 * @param pollingService фоновый сервис опроса
 * @param address        числовой адрес регистра (register.address)
 * @param abs            если true, отображаем абсолютное значение
 * @param numOfSymbols   мин. ширина строки (padStart)
 */
class Field(
    private val pollingService: PollingService,
    val address: Int,
    val abs: Boolean = false,
    val numOfSymbols: Int = 0
) {
    private lateinit var state: MutableState<String>

    /**
     * Привязать к MutableState, в котором будем показывать текст.
     */
    fun bindTo(state: MutableState<String>): Field = apply {
        this.state = state
    }

    /**
     * Запустить цикличный опрос этого регистра.
     */
    fun poll() {
        pollingService.startFieldPolling(address) { value ->
            val display = (if (abs) kotlin.math.abs(value) else value)
                .toString()
                .padStart(numOfSymbols, '0')
            state.value = display
        }
    }

    /**
     * Остановить опрос (если понадобится).
     */
    fun stop() {
        pollingService.stopAll() // или отдельно хранить Job, если нужно
    }
}
