package state

enum class ByteOrder(val label: String) {
    ABCD("ABCD"),
    CDAB("CDAB"),
    BADC("BADC"),
    DCBA("DCBA")
}