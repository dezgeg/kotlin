// IS_APPLICABLE: false
class C {
    fun get(f: (Int) -> Int, x: Int) = f(x)
    fun test() = C().get({ it + 1 }, 41)
}
