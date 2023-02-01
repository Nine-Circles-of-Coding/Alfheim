package alfheim.common.core.handler

import java.util.*

object TimeHandler {
	val calendar = Calendar.getInstance()!!
	val year = calendar[Calendar.YEAR]
	val month = calendar[Calendar.MONTH] + 1
	val day = calendar[Calendar.DATE]
}

/**
 * Two weeks per each month of winter
 */
val WRATH_OF_THE_WINTER = TimeHandler.month in arrayOf(1, 2, 12, 13) && TimeHandler.day in 8..21

/**
 * Two weeks per each month of summer
 */
val HELLISH_VACATION = TimeHandler.month in 6..8 && TimeHandler.day in 8..21