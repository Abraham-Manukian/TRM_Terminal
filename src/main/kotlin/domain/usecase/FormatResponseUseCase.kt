package domain.usecase

import state.ByteOrder
import state.DisplayMode
import java.nio.ByteBuffer

class FormatResponseUseCase {
    fun execute(response: String, displayMode: DisplayMode, byteOrder: ByteOrder): String {
        val bytes = response
            .split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull(16)?.toByte() }

        return when (displayMode) {
            DisplayMode.HEX -> response

            DisplayMode.DEC -> {
                val data = bytes.drop(3).dropLast(2)
                data.chunked(2).mapIndexed { index, pair ->
                    val value = ((pair.getOrNull(0)?.toInt() ?: 0) shl 8) +
                            (pair.getOrNull(1)?.toInt() ?: 0)
                    "Регистр $index = $value"
                }.joinToString("\n")
            }

            DisplayMode.FLOAT -> {
                val data = bytes.drop(3).dropLast(2)

                data.chunked(4).mapIndexed { index, group ->
                    if (group.size == 4) {
                        val byteArray = when (byteOrder) {
                            ByteOrder.ABCD -> byteArrayOf(group[0], group[1], group[2], group[3])
                            ByteOrder.CDAB -> byteArrayOf(group[2], group[3], group[0], group[1])
                            ByteOrder.BADC -> byteArrayOf(group[1], group[0], group[3], group[2])
                            ByteOrder.DCBA -> byteArrayOf(group[3], group[2], group[1], group[0])
                        }

                        try {
                            val float = ByteBuffer.wrap(byteArray).float
                            "Float $index = %.5f".format(float)
                        } catch (e: Exception) {
                            "Float $index = Ошибка декодирования"
                        }
                    } else {
                        "Float $index = ???"
                    }
                }.joinToString("\n")
            }
        }
    }
} 