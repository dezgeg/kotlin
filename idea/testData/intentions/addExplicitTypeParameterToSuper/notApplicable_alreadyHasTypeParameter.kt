// IS_APPLICABLE: false
trait A {
    fun bar() {
    }
}

class C() : A {
    override fun bar() {
        su<caret>per<A>.bar()
    }
}