@file:Suppress("unused")

package alfheim.common.core.util

/**
 * Checks if element reference is in array
 */
infix fun <T> T.inn(array: Array<out T>): Boolean {
	return array.indexOfLink(this) >= 0
}

/**
 * Checks if element reference is in array
 */
infix fun <T> T.inn(iterable: Iterable<T>): Boolean {
	return iterable.indexOfLink(this) >= 0
}

fun <T> Array<out T>.indexOfLink(element: T): Int {
	if (element === null) {
		for (index in indices)
			if (this[index] === null)
				return index
	} else {
		for (index in indices)
			if (element === this[index])
				return index
	}
	return -1
}

fun <T> Iterable<T>.indexOfLink(element: T): Int {
	this.forEachIndexed { id, it -> if (element === it) return id }
	return -1
}

inline fun ByteArray.mapInPlace(transform: (Byte) -> Byte): ByteArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun CharArray.mapInPlace(transform: (Char) -> Char): CharArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun ShortArray.mapInPlace(transform: (Short) -> Short): ShortArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun IntArray.mapInPlace(transform: (Int) -> Int): IntArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun FloatArray.mapInPlace(transform: (Float) -> Float): FloatArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun DoubleArray.mapInPlace(transform: (Double) -> Double): DoubleArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun BooleanArray.mapInPlace(transform: (Boolean) -> Boolean): BooleanArray {
	for (i in indices) this[i] = transform(this[i])
	return this
}

inline fun <T> Array<T>.mapInPlace(transform: (T) -> T): Array<T> {
	for (i in indices) this[i] = transform(this[i])
	return this
}