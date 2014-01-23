package kotlin

//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//

import java.util.*

/**
 * Returns *true* if all elements match the given *predicate*
 */
public inline fun IntArray.all(predicate: (Int) -> Boolean) : Boolean {
    for (element in this) if (!predicate(element)) return false
    return true
}

/**
 * Returns *true* if any elements match the given *predicate*
 */
public inline fun IntArray.any(predicate: (Int) -> Boolean) : Boolean {
    for (element in this) if (predicate(element)) return true
    return false
}

/**
 * Appends the string from all the elements separated using the *separator* and using the given *prefix* and *postfix* if supplied
 * If a collection could be huge you can specify a non-negative value of *limit* which will only show a subset of the collection then it will
 * a special *truncated* separator (which defaults to "..."
 */
public fun IntArray.appendString(buffer: Appendable, separator: String = ", ", prefix: String ="", postfix: String = "", limit: Int = -1, truncated: String = "...") : Unit {
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            val text = if (element == null) "null" else element.toString()
            buffer.append(text)
        } else break
    }
    if (limit >= 0 && count > limit) buffer.append(truncated)
    buffer.append(postfix)
}

/**
 * Returns the number of elements which match the given *predicate*
 */
public inline fun IntArray.count(predicate: (Int) -> Boolean) : Int {
    var count = 0
    for (element in this) if (predicate(element)) count++
    return count
}

/**
 * Returns a list containing everything but the first *n* elements
 */
public fun IntArray.drop(n: Int) : List<Int> {
    return dropWhile(countTo(n))
}

/**
 * Returns a list containing the everything but the first elements that satisfy the given *predicate*
 */
public inline fun IntArray.dropWhile(predicate: (Int) -> Boolean) : List<Int> {
    return dropWhileTo(ArrayList<Int>(), predicate)
}

/**
 * Returns a list containing the everything but the first elements that satisfy the given *predicate*
 */
public inline fun <L: MutableList<in Int>> IntArray.dropWhileTo(result: L, predicate: (Int) -> Boolean) : L {
    var start = true
    for (element in this) {
        if (start && predicate(element)) {
            // ignore
        } else {
            start = false
            result.add(element)
        }
    }
    return result
}

/**
 * Returns a list containing all elements which match the given *predicate*
 */
public inline fun IntArray.filter(predicate: (Int) -> Boolean) : List<Int> {
    return filterTo(ArrayList<Int>(), predicate)
}

/**
 * Returns a list containing all elements which do not match the given *predicate*
 */
public inline fun IntArray.filterNot(predicate: (Int) -> Boolean) : List<Int> {
    return filterNotTo(ArrayList<Int>(), predicate)
}

/**
 * Returns a list containing all elements which do not match the given *predicate*
 */
public inline fun <C: MutableCollection<in Int>> IntArray.filterNotTo(result: C, predicate: (Int) -> Boolean) : C {
    for (element in this) if (!predicate(element)) result.add(element)
    return result
}

/**
 * Filters all elements which match the given predicate into the given list
 */
public inline fun <C: MutableCollection<in Int>> IntArray.filterTo(result: C, predicate: (Int) -> Boolean) : C {
    for (element in this) if (predicate(element)) result.add(element)
    return result
}

/**
 * Returns the first element which matches the given *predicate* or *null* if none matched
 */
public inline fun IntArray.find(predicate: (Int) -> Boolean) : Int? {
    for (element in this) if (predicate(element)) return element
    return null
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 */
public inline fun <R> IntArray.flatMap(transform: (Int)-> Iterable<R>) : List<R> {
    return flatMapTo(ArrayList<R>(), transform)
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single collection
 */
public inline fun <R, C: MutableCollection<in R>> IntArray.flatMapTo(result: C, transform: (Int) -> Iterable<R>) : C {
    for (element in this) {
        val list = transform(element)
        for (r in list) result.add(r)
    }
    return result
}

/**
 * Folds all elements from from left to right with the *initial* value to perform the operation on sequential pairs of elements
 */
public inline fun <R> IntArray.fold(initial: R, operation: (R, Int) -> R) : R {
    var answer = initial
    for (element in this) answer = operation(answer, element)
    return answer
}

/**
 * Folds all elements from right to left with the *initial* value to perform the operation on sequential pairs of elements
 */
public inline fun <R> IntArray.foldRight(initial: R, operation: (Int, R) -> R) : R {
    var r = initial
    var index = size - 1
    
    while (index >= 0) {
        r = operation(get(index--), r)
    }
    
    return r
}

/**
 * Performs the given *operation* on each element
 */
public inline fun IntArray.forEach(operation: (Int) -> Unit) : Unit {
    for (element in this) operation(element)
}

/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 */
public inline fun <K> IntArray.groupBy(toKey: (Int) -> K) : Map<K, List<Int>> {
    return groupByTo(HashMap<K, MutableList<Int>>(), toKey)
}

public inline fun <K> IntArray.groupByTo(result: MutableMap<K, MutableList<Int>>, toKey: (Int) -> K) : Map<K, MutableList<Int>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<Int>() }
        list.add(element)
    }
    return result
}

/**
 * Returns first index of item, or -1 if the array does not contain item
 */
public fun IntArray.indexOf(item: Int) : Int {
    for (i in indices) {
        if (item == this[i]) {
            return i
        }
    }
    return -1
}

/**
 * Returns true if the array is empty
 */
public fun IntArray.isEmpty() : Boolean {
    return size == 0
}

/**
 * Returns true if the array is empty
 */
public fun IntArray.isNotEmpty() : Boolean {
    return !isEmpty()
}

/**
 * Creates a string from all the elements separated using the *separator* and using the given *prefix* and *postfix* if supplied.
 * If a collection could be huge you can specify a non-negative value of *limit* which will only show a subset of the collection then it will
 * a special *truncated* separator (which defaults to "..."
 */
public fun IntArray.makeString(separator: String = ", ", prefix: String = "", postfix: String = "", limit: Int = -1, truncated: String = "...") : String {
    val buffer = StringBuilder()
    appendString(buffer, separator, prefix, postfix, limit, truncated)
    return buffer.toString()
}

/**
 * Returns a new List containing the results of applying the given *transform* function to each element in this collection
 */
public inline fun <R> IntArray.map(transform : (Int) -> R) : List<R> {
    return mapTo(ArrayList<R>(), transform)
}

/**
 * Transforms each element of this collection with the given *transform* function and
 * adds each return value to the given *results* collection
 */
public inline fun <R, C: MutableCollection<in R>> IntArray.mapTo(result: C, transform : (Int) -> R) : C {
    for (item in this)
        result.add(transform(item))
    return result
}

/**
 * Returns the largest element or null if there are no elements
 */
public fun IntArray.max() : Int? {
    if (isEmpty()) return null
    
    var max = this[0]
    for (i in 1..lastIndex) {
        val e = this[i]
        if (max < e) max = e
    }
    return max
}

/**
 * Returns the first element yielding the largest value of the given function or null if there are no elements
 */
public inline fun <R: Comparable<R>> IntArray.maxBy(f: (Int) -> R) : Int? {
    if (isEmpty()) return null
    
    var maxElem = this[0]
    var maxValue = f(maxElem)
    for (i in 1..lastIndex) {
        val e = this[i]
        val v = f(e)
        if (maxValue < v) {
           maxElem = e
           maxValue = v
        }
    }
    return maxElem
}

/**
 * Returns the smallest element or null if there are no elements
 */
public fun IntArray.min() : Int? {
    if (isEmpty()) return null
    
    var min = this[0]
    for (i in 1..lastIndex) {
        val e = this[i]
        if (min > e) min = e
    }
    return min
}

/**
 * Returns the first element yielding the smallest value of the given function or null if there are no elements
 */
public inline fun <R: Comparable<R>> IntArray.minBy(f: (Int) -> R) : Int? {
    if (size == 0) return null
    
    var minElem = this[0]
    var minValue = f(minElem)
    for (i in 1..lastIndex) {
        val e = this[i]
        val v = f(e)
        if (minValue > v) {
           minElem = e
           minValue = v
        }
    }
    return minElem
}

/**
 * Partitions this collection into a pair of collections
 */
public inline fun IntArray.partition(predicate: (Int) -> Boolean) : Pair<List<Int>, List<Int>> {
    val first = ArrayList<Int>()
    val second = ArrayList<Int>()
    for (element in this) {
        if (predicate(element)) {
            first.add(element)
        } else {
            second.add(element)
        }
    }
    return Pair(first, second)
}

/**
 * Creates an [[Iterator]] which iterates over this iterator then the following collection
 */
public fun IntArray.plus(collection: Iterable<Int>) : List<Int> {
    return plus(collection.iterator())
}

/**
 * Creates an [[Iterator]] which iterates over this iterator then the given element at the end
 */
public fun IntArray.plus(element: Int) : List<Int> {
    val answer = ArrayList<Int>()
    toCollection(answer)
    answer.add(element)
    return answer
}

/**
 * Creates an [[Iterator]] which iterates over this iterator then the following iterator
 */
public fun IntArray.plus(iterator: Iterator<Int>) : List<Int> {
    val answer = ArrayList<Int>()
    toCollection(answer)
    for (element in iterator) {
        answer.add(element)
    }
    return answer
}

/**
 * Applies binary operation to all elements of iterable, going from left to right.
 * Similar to fold function, but uses the first element as initial value
 */
public inline fun IntArray.reduce(operation: (Int, Int) -> Int) : Int {
    val iterator = this.iterator()
    if (!iterator.hasNext()) {
        throw UnsupportedOperationException("Empty iterable can't be reduced")
    }
    
    var result: Int = iterator.next() //compiler doesn't understand that result will initialized anyway
    while (iterator.hasNext()) {
        result = operation(result, iterator.next())
    }
    
    return result
}

/**
 * Applies binary operation to all elements of iterable, going from right to left.
 * Similar to foldRight function, but uses the last element as initial value
 */
public inline fun IntArray.reduceRight(operation: (Int, Int) -> Int) : Int {
    var index = size - 1
    if (index < 0) {
        throw UnsupportedOperationException("Empty iterable can't be reduced")
    }
    
    var r = get(index--)
    while (index >= 0) {
        r = operation(get(index--), r)
    }
    
    return r
}

/**
 * Reverses the order the elements into a list
 */
public fun IntArray.reverse() : List<Int> {
    val list = toCollection(ArrayList<Int>())
    Collections.reverse(list)
    return list
}

/**
 * Copies all elements into a [[List]] and sorts it by value of compare_function(element)
 * E.g. arrayList("two" to 2, "one" to 1).sortBy({it.second}) returns list sorted by second element of pair
 */
public inline fun <R: Comparable<R>> IntArray.sortBy(f: (Int) -> R) : List<Int> {
    val sortedList = toCollection(ArrayList<Int>())
    val sortBy: Comparator<Int> = comparator<Int> {(x: Int, y: Int) ->
        val xr = f(x)
        val yr = f(y)
        xr.compareTo(yr)
    }
    java.util.Collections.sort(sortedList, sortBy)
    return sortedList
}

/**
 * Returns a list containing the first *n* elements
 */
public fun IntArray.take(n: Int) : List<Int> {
    return takeWhile(countTo(n))
}

/**
 * Returns a list containing the first elements that satisfy the given *predicate*
 */
public inline fun IntArray.takeWhile(predicate: (Int) -> Boolean) : List<Int> {
    return takeWhileTo(ArrayList<Int>(), predicate)
}

/**
 * Returns a list containing the first elements that satisfy the given *predicate*
 */
public inline fun <C: MutableCollection<in Int>> IntArray.takeWhileTo(result: C, predicate: (Int) -> Boolean) : C {
    for (element in this) if (predicate(element)) result.add(element) else break
    return result
}

/**
 * Copies all elements into the given collection
 */
public fun <C: MutableCollection<in Int>> IntArray.toCollection(result: C) : C {
    for (element in this) result.add(element)
    return result
}

/**
 * Copies all elements into a [[LinkedList]]
 */
public fun IntArray.toLinkedList() : LinkedList<Int> {
    return toCollection(LinkedList<Int>())
}

/**
 * Copies all elements into a [[List]]
 */
public fun IntArray.toList() : List<Int> {
    return toCollection(ArrayList<Int>())
}

/**
 * Copies all elements into a [[Set]]
 */
public fun IntArray.toSet() : Set<Int> {
    return toCollection(LinkedHashSet<Int>())
}

/**
 * Copies all elements into a [[SortedSet]]
 */
public fun IntArray.toSortedSet() : SortedSet<Int> {
    return toCollection(TreeSet<Int>())
}

/**
 * Returns an iterator of Pairs(index, data)
 */
public fun IntArray.withIndices() : Iterator<Pair<Int, Int>> {
    return IndexIterator(iterator())
}

/**
 * Sums up the elements
 */
public fun IntArray.sum() : Int {
    return fold(0, {a,b -> a+b})
}

