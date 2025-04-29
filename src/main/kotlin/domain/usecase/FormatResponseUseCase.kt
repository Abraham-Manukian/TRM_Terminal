package domain.usecase

import state.ByteOrder
import state.DisplayMode
import java.nio.ByteBuffer
import java.nio.ByteOrder as JavaByteOrder

class FormatResponseUseCase {
    fun execute(response: String, displayMode: DisplayMode, byteOrder: ByteOrder): String {
        if (response.isBlank()) return ""
        
        val bytes = response
            .split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { 
                try {
                    it.toIntOrNull(16)?.toByte()
                } catch (e: Exception) {
                    null
                }
            }
            .toByteArray()
        
        if (bytes.isEmpty()) return "Ошибка: пустой ответ"

        return when (displayMode) {
            DisplayMode.HEX -> response

            DisplayMode.DEC -> {
                try {
                    // Извлечь полезные данные из Modbus ответа (пропустить адрес, функцию, длину и CRC)
                    val data = if (bytes.size >= 5) bytes.drop(3).dropLast(2).toList() else bytes.toList()
                    data.windowed(2, 2, true).mapIndexed { index, pair ->
                        val value = if (pair.size >= 2) {
                            ((pair[0].toInt() and 0xFF) shl 8) or (pair[1].toInt() and 0xFF)
                        } else if (pair.size == 1) {
                            pair[0].toInt() and 0xFF
                        } else 0
                        "Регистр $index = $value"
                    }.joinToString("\n")
                } catch (e: Exception) {
                    "Ошибка при декодировании: ${e.message}"
                }
            }

            DisplayMode.FLOAT -> {
                try {
                    // Извлечь полезные данные из Modbus ответа
                    val data = if (bytes.size >= 5) bytes.drop(3).dropLast(2).toList() else bytes.toList()
                    
                    // Вывести отладочную информацию о байтах
                    val bytesHex = data.joinToString(" ") { byte -> "%02X".format(byte) }
                    val debugInfo = "Байты: $bytesHex\n"
                    
                    debugInfo + data.windowed(4, 4, true).mapIndexed { index, group ->
                        if (group.size == 4) {
                            try {
                                // Переупорядочивание байтов в соответствии с выбранным порядком
                                val byteArray = when (byteOrder) {
                                    ByteOrder.ABCD -> byteArrayOf(group[0], group[1], group[2], group[3])
                                    ByteOrder.CDAB -> byteArrayOf(group[2], group[3], group[0], group[1])
                                    ByteOrder.BADC -> byteArrayOf(group[1], group[0], group[3], group[2])
                                    ByteOrder.DCBA -> byteArrayOf(group[3], group[2], group[1], group[0])
                                }
                                
                                // Создаем буфер и устанавливаем порядок байтов в соответствии с форматом
                                val buffer = ByteBuffer.wrap(byteArray)
                                
                                // Используем порядок байтов, соответствующий ожидаемому формату данных
                                // ABCD, BADC - BigEndian, CDAB, DCBA - LittleEndian
                                val javaByteOrder = when (byteOrder) {
                                    ByteOrder.ABCD, ByteOrder.BADC -> JavaByteOrder.BIG_ENDIAN
                                    ByteOrder.CDAB, ByteOrder.DCBA -> JavaByteOrder.LITTLE_ENDIAN
                                }
                                buffer.order(javaByteOrder)
                                
                                val float = buffer.getFloat(0)
                                val rawHex = byteArray.joinToString(" ") { byte -> "%02X".format(byte) }
                                "Float $index = %.3f [%s]".format(float, rawHex)
                            } catch (e: Exception) {
                                "Float $index = Ошибка декодирования: ${e.message}"
                            }
                        } else {
                            val available = group.joinToString(" ") { byte -> "%02X".format(byte) }
                            "Float $index = Недостаточно байт (нужно 4, получено ${group.size}): [$available]"
                        }
                    }.joinToString("\n")
                } catch (e: Exception) {
                    "Ошибка при декодировании: ${e.message}"
                }
            }
        }
    }
} 