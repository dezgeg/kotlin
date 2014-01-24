// IS_APPLICABLE: false
class C {
    fun get&lt;T&gt;(x: Int) = 42
    fun test() = g<caret>et&lt;Int&gt;(42)
}
