open class A() {
    open fun f() = 43
}

class B() : A() {
    override fun f() = sup<caret>er@B.f() - 1
}
