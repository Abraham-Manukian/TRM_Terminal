package state

/**
 * Перечисление возможных диапазонов фильтрации регистров
 */
enum class FilterRange(val label: String) {
    ALL("Все регистры"),
    RANGE_6_11("Регистры 6-11"),
    RANGE_57_87("Регистры 57-87")
} 