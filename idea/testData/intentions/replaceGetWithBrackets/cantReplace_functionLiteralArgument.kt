// IS_APPLICABLE: false
class C {
    fun get(x: Int, f: (Int) -> Int) = f(x)
    fun test() = C().get(41) { it + 1 }
}
