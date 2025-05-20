package com.dev.goalpulse.dataUtils

import com.dev.goalpulse.ResponseState
import com.dev.goalpulse.models.football.MatchStatistics
import kotlin.math.roundToInt

object StatisticsProcessor {

    private val PERCENTAGE_REGEX = """(\d+)/(\d+)\s+\((\d+)%\)""".toRegex()

    fun processStatistics(
        rawStatistics: List<MatchStatistics.MatchStatisticsItem.Statistic?>?
    ): ResponseState<MatchStatistics> {
            val processedStats = rawStatistics?.filterNotNull()
                ?.map { processStatisticItem(it) }
                ?.distinctBy { "${it.type}|${it.period}|${it.awayTeam}|${it.homeTeam}" }

            val result = MatchStatistics().apply {
                add(MatchStatistics.MatchStatisticsItem(
                    matchId = null,
                    statistics = processedStats
                ))
            }
            return ResponseState.Success(result)
    }

    private fun processStatisticItem(statistic: MatchStatistics.MatchStatisticsItem.Statistic):
            MatchStatistics.MatchStatisticsItem.Statistic {

        return when {
            isPercentageFormat(statistic.homeTeam) -> processPercentageStatistic(statistic)
            isNumericFormat(statistic.homeTeam) -> processNumericStatistic(statistic)
            else -> processDefaultStatistic(statistic)
        }
    }

    private fun isPercentageFormat(value: String?): Boolean {
        return value?.contains("%") == true
    }

    private fun isNumericFormat(value: String?): Boolean {
        return value?.toDoubleOrNull() != null
    }

    private fun processPercentageStatistic(
        statistic: MatchStatistics.MatchStatisticsItem.Statistic
    ): MatchStatistics.MatchStatisticsItem.Statistic {
        val homeValue = statistic.homeTeam!!
        val awayValue = statistic.awayTeam!!

        PERCENTAGE_REGEX.matchEntire(homeValue)?.let { match ->
            // Format like "12/20 (60%)"
            val homePercentage = match.groupValues[3].toInt()
            val awayPercentage = PERCENTAGE_REGEX.find(awayValue)?.groupValues?.get(3)?.toInt() ?: 0

            return statistic.copy(
                homeProgressbarCurrentValue = homePercentage,
                awayProgressbarCurrentValue = awayPercentage,
                homeProgressbarMaxValue = 100,
                awayProgressbarMaxValue = 100
            )
        } ?: run {
            // Simple percentage format like "60%"
            return statistic.copy(
                homeProgressbarCurrentValue = homeValue.removeSuffix("%").toIntOrNull() ?: 0,
                awayProgressbarCurrentValue = awayValue.removeSuffix("%").toIntOrNull() ?: 0,
                homeProgressbarMaxValue = 100,
                awayProgressbarMaxValue = 100
            )
        }
    }

    private fun processNumericStatistic(
        statistic: MatchStatistics.MatchStatisticsItem.Statistic
    ): MatchStatistics.MatchStatisticsItem.Statistic {
        val homeValue = statistic.homeTeam!!.toDouble()
        val awayValue = statistic.awayTeam!!.toDouble()
        val total = homeValue + awayValue

        return statistic.copy(
            homeProgressbarCurrentValue = homeValue.roundToInt(),
            awayProgressbarCurrentValue = awayValue.roundToInt(),
            homeProgressbarMaxValue = total.roundToInt(),
            awayProgressbarMaxValue = total.roundToInt()
        )
    }

    private fun processDefaultStatistic(
        statistic: MatchStatistics.MatchStatisticsItem.Statistic
    ): MatchStatistics.MatchStatisticsItem.Statistic {
        val homeValue = statistic.homeTeam?.toIntOrNull() ?: 0
        val awayValue = statistic.awayTeam?.toIntOrNull() ?: 0
        val total = homeValue + awayValue

        return statistic.copy(
            homeProgressbarCurrentValue = homeValue,
            awayProgressbarCurrentValue = awayValue,
            homeProgressbarMaxValue = total,
            awayProgressbarMaxValue = total
        )
    }
}