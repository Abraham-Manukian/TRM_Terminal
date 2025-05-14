package data.register

import domain.model.Register
import domain.model.RegisterType

fun preloadFromProtocol(): List<Register> {
    return listOf(
        // --- строки 6–11 ---
        Register(4102, "Мощность", "Текущая мощность", RegisterType.ANALOG),
        Register(4104, "Напряжение", "Текущее напряжение", RegisterType.ANALOG),
        Register(4106, "Ток", "Текущий ток", RegisterType.ANALOG),
        Register(4108, "Коэффициент мощности", "Текущий cos(φ)", RegisterType.ANALOG),
        Register(4110, "Температура", "Температура внутри устройства", RegisterType.ANALOG),
        Register(4112, "Счётчик CRM", "Счётчик CRM", RegisterType.CRM, readOnly = false),

        // --- строки 57–87 ---
        Register(4142, "Состояние реле 1", "Управление выходом реле 1", RegisterType.DISCRETE, readOnly = false),
        Register(4143, "Состояние реле 2", "Управление выходом реле 2", RegisterType.DISCRETE, readOnly = false),
        Register(4144, "Состояние реле 3", "Управление выходом реле 3", RegisterType.DISCRETE, readOnly = false),
        Register(4145, "Состояние реле 4", "Управление выходом реле 4", RegisterType.DISCRETE, readOnly = false),
        Register(4146, "Состояние реле 5", "Управление выходом реле 5", RegisterType.DISCRETE, readOnly = false),
        Register(4147, "Состояние реле 6", "Управление выходом реле 6", RegisterType.DISCRETE, readOnly = false),
        Register(4148, "Состояние реле 7", "Управление выходом реле 7", RegisterType.DISCRETE, readOnly = false),
        Register(4149, "Состояние реле 8", "Управление выходом реле 8", RegisterType.DISCRETE, readOnly = false),
        Register(4150, "Состояние реле 9", "Управление выходом реле 9", RegisterType.DISCRETE, readOnly = false),
        Register(4151, "Состояние реле 10", "Управление выходом реле 10", RegisterType.DISCRETE, readOnly = false),
        Register(4152, "Состояние реле 11", "Управление выходом реле 11", RegisterType.DISCRETE, readOnly = false),
        Register(4153, "Состояние реле 12", "Управление выходом реле 12", RegisterType.DISCRETE, readOnly = false),
        Register(4154, "Уставка тока", "Порог срабатывания по току", RegisterType.CONFIG, readOnly = false),
        Register(4155, "Уставка мощности", "Порог по мощности", RegisterType.CONFIG, readOnly = false),
        Register(4156, "Уставка напряжения", "Порог по напряжению", RegisterType.CONFIG, readOnly = false),
        Register(4157, "Режим работы", "Выбор режима работы", RegisterType.CONFIG, readOnly = false),
        Register(4158, "Сброс счетчика CRM", "Команда сброса", RegisterType.CRM, readOnly = false),
        Register(4159, "Темп. авария", "Порог аварии по температуре", RegisterType.CONFIG, readOnly = false),
        Register(4160, "ЛАТР вверх", "Управление ЛАТРом вверх", RegisterType.DISCRETE, readOnly = false),
        Register(4161, "ЛАТР вниз", "Управление ЛАТРом вниз", RegisterType.DISCRETE, readOnly = false),
        Register(4162, "ЛАТР стоп", "Остановка ЛАТРа", RegisterType.DISCRETE, readOnly = false),
        Register(4163, "Реле аварии", "Состояние реле аварии", RegisterType.DISCRETE),
        Register(4164, "Температура ЛАТР", "Температура ЛАТРа", RegisterType.ANALOG)
    )
}
