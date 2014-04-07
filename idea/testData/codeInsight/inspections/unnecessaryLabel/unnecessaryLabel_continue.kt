fun foo() {
    @loop do {
        cont<caret>inue@loop
    } while (false)
}
