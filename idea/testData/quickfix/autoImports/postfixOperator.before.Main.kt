// "Import" "true"
// ERROR: Unresolved reference: ++

package h

trait H

fun f(h: H?) {
    var h1 = h
    h1<caret>++
}