package com.example.jatrilrun.ui.screens.event

import com.example.jatrilrun.R

internal data class RaceCategory(
    val distance: String,
    val name: String,
    val elevation: String,
    val cutoff: String,
    val price: Int,
    val slots: Int
) {
    val title get() = "$distance · $name"
    val metrics get() = "$elevation elevasi · COT $cutoff"
}

internal data class TrailEvent(
    val id: String,
    val title: String,
    val day: Int,
    val region: String,
    val location: String,
    val type: String,
    val distances: List<String>,
    val minimumPrice: Int,
    val image: Int,
    val categories: List<RaceCategory> = emptyList(),
    val soldOut: Boolean = false
) {
    val shortDate get() = "$day Nov"
    val month get() = "November 2026"
    val distanceLabel get() = distances.joinToString(" · ")
}

internal val baliTrailRun = TrailEvent(
    id = "bali", title = "Bali Trail Run 2026", day = 15, region = "Bali",
    location = "Kintamani, Bali", type = "Trail run", distances = listOf("30K", "50K", "100K"),
    minimumPrice = 450_000, image = R.drawable.event_trail,
    categories = listOf(
        RaceCategory("30K", "Trail discovery", "1.450 m", "8 jam", 450_000, 48),
        RaceCategory("50K", "Mountain challenge", "2.850 m", "14 jam", 750_000, 12),
        RaceCategory("100K", "Ultra journey", "5.500 m", "28 jam", 1_250_000, 0)
    )
)

internal val merapiSunriseRun = TrailEvent(
    id = "merapi", title = "Merapi Sunrise Run", day = 8, region = "Yogyakarta",
    location = "Kaliurang, Yogyakarta", type = "Trail run", distances = listOf("10K", "25K", "50K"),
    minimumPrice = 250_000, image = R.drawable.event_road
)

internal val jogjaCityRun = TrailEvent(
    id = "jogja", title = "Jogja City Run", day = 22, region = "Yogyakarta",
    location = "Yogyakarta", type = "Road run", distances = listOf("5K", "10K"),
    minimumPrice = 175_000, image = R.drawable.event_road, soldOut = true
)

internal val trailEvents = listOf(baliTrailRun, merapiSunriseRun, jogjaCityRun)

internal enum class EventPrice(val label: String) {
    Under300("< Rp300rb"),
    Over500("> Rp500rb")
}

internal data class EventFilters(
    val region: String = "",
    val type: String = "",
    val distance: String = "",
    val month: String = "",
    val price: EventPrice? = null,
    val availableOnly: Boolean = false
) {
    fun results(query: String): List<TrailEvent> {
        val terms = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() && !it.equals("di", true) }
        return trailEvents.filter { event ->
            val searchable = "${event.title} ${event.location} ${event.type} ${event.distanceLabel}"
            val matchesPrice = when (price) {
                EventPrice.Under300 -> event.minimumPrice < 300_000
                EventPrice.Over500 -> event.minimumPrice > 500_000 ||
                    event.categories.any { it.slots > 0 && it.price > 500_000 }
                null -> true
            }
            terms.all { searchable.contains(it, ignoreCase = true) } &&
                (region.isEmpty() || event.region == region) &&
                (type.isEmpty() || event.type == type) &&
                (distance.isEmpty() || distance in event.distances) &&
                (month.isEmpty() || event.month == month) &&
                (!availableOnly || !event.soldOut) &&
                matchesPrice
        }
    }
}

internal fun rupiah(price: Int): String =
    "Rp" + price.toString().reversed().chunked(3).joinToString(".").reversed()
