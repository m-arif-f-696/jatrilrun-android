package com.example.jatrilrun.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jatrilrun.ui.components.CommonButton
import com.example.jatrilrun.ui.components.JrBottomNavigationBar
import com.example.jatrilrun.ui.screens.event.*
import com.example.jatrilrun.data.remote.dto.CategoryDto
import com.example.jatrilrun.data.remote.dto.EventDto
import com.example.jatrilrun.ui.screens.tickets.TicketsScreen
import com.example.jatrilrun.ui.screens.tickets.TicketsViewModel
import com.example.jatrilrun.ui.screens.tickets.ServerTicketsPanel
import com.example.jatrilrun.ui.theme.JatrilrunTheme
import com.example.jatrilrun.ui.theme.JrSpacing

private object DiscoverRoute {
    const val Home = "home"
    const val Events = "event"
    const val Tickets = "tickets"
    const val Search = "search"
    const val Filter = "filter"
    const val Results = "results"
    const val Detail = "detail"
    const val Route = "route"
    const val Schedule = "schedule"
    const val Categories = "categories"
    const val SoldOut = "sold-out"
}

@Composable
fun HomeScreen(
    onNavigateToPayment: () -> Unit,
    onNavigateToLogin: (String) -> Unit,
    onNavigateToServerRegistration: (EventDto, CategoryDto) -> Unit,
    onLogout: () -> Unit,
    onOpenRacePass: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val navigation = rememberNavController()
    var query by rememberSaveable { mutableStateOf("") }
    var filters by rememberSaveable(stateSaver = EventFiltersSaver) { mutableStateOf(EventFilters()) }
    var recentSearches by rememberSaveable {
        mutableStateOf(arrayListOf("Bali Trail Run", "Merapi", "Trail 30K"))
    }

    fun open(route: String) {
        navigation.navigate(route) { launchSingleTop = true }
    }

    fun openTab(route: String) {
        navigation.navigate(route) {
            popUpTo(DiscoverRoute.Home)
            launchSingleTop = true
        }
    }

    fun back() {
        navigation.popBackStack()
    }

    fun showResults() {
        if (navigation.previousBackStackEntry?.destination?.route == DiscoverRoute.Results) {
            back()
        } else {
            navigation.navigate(DiscoverRoute.Results) {
                popUpTo(navigation.currentDestination?.route ?: DiscoverRoute.Home) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val serverEvents: ServerEventViewModel = hiltViewModel()
    val eventState by serverEvents.state.collectAsState()
    val catalog = remember(eventState) {
        (eventState as? ServerEventState.Ready)?.events.orEmpty().map { it.toListing() }
    }

    fun openEvent(event: TrailEvent) {
        event.serverSlug?.let {
            open("server-event/${android.net.Uri.encode(it)}")
            return
        }
        open(if (event.soldOut) DiscoverRoute.SoldOut else "${DiscoverRoute.Detail}/${event.id}")
    }

    NavHost(navigation, startDestination = DiscoverRoute.Home) {
        composable(DiscoverRoute.Home) {
            DiscoverHome(
                onSearch = {
                    query = ""
                    filters = EventFilters()
                    open(DiscoverRoute.Search)
                },
                onBrowse = { openTab(DiscoverRoute.Events) },
                onTickets = { openTab(DiscoverRoute.Tickets) },
                onEvent = ::openEvent,
                catalog = catalog,
                feedback = { ServerEventFeedback(eventState) { serverEvents.reload() } },
                bottomBar = { JrBottomNavigationBar(DiscoverRoute.Home, ::openTab) },
                account = {
                    EventPanel {
                        EventText("Pengguna: ${user?.name ?: "Guest"}")
                        CommonButton(text = "Simulasi daftar event", onClick = { openEvent(baliTrailRun) })
                        CommonButton(text = "Simulasi pembayaran", onClick = onNavigateToPayment)
                        CommonButton(text = "Keluar", onClick = {
                            viewModel.logout()
                            onLogout()
                        })
                    }
                }
            )
        }
        composable(DiscoverRoute.Events) {
            BrowseEvents(
                catalog = catalog,
                feedback = { ServerEventFeedback(eventState) { serverEvents.reload() } },
                onBack = ::back,
                onSearch = {
                    query = ""
                    filters = EventFilters()
                    open(DiscoverRoute.Search)
                },
                onFilter = {
                    query = ""
                    filters = EventFilters()
                    open(DiscoverRoute.Filter)
                },
                onEvent = ::openEvent,
                bottomBar = {
                    JrBottomNavigationBar(DiscoverRoute.Events, ::openTab, containerColor = EventCardWhite)
                }
            )
        }
        composable(DiscoverRoute.Tickets) {
            val ticketsViewModel: TicketsViewModel = hiltViewModel()
            val tickets by ticketsViewModel.tickets.collectAsState()
            TicketsScreen(
                tickets = tickets,
                onBrowse = { openTab(DiscoverRoute.Events) },
                onOpenPass = { onOpenRacePass() },
                bottomBar = { JrBottomNavigationBar(DiscoverRoute.Tickets, ::openTab) },
                serverContent = { ServerTicketsPanel() }
            )
        }
        composable(DiscoverRoute.Search) {
            SearchEvents(
                query = query,
                recent = recentSearches,
                onBack = ::back,
                onSearch = { term ->
                    query = term
                    if (term.isNotEmpty()) {
                        recentSearches = ArrayList(
                            (listOf(term) + recentSearches.filterNot { it.equals(term, ignoreCase = true) }).take(3)
                        )
                    }
                    showResults()
                },
                onDistance = { distance ->
                    query = ""
                    filters = filters.copy(distance = distance)
                    showResults()
                }
            )
        }
        composable(DiscoverRoute.Filter) {
            FilterEvents(catalog, query, filters, ::back) {
                filters = it
                showResults()
            }
        }
        composable(DiscoverRoute.Results) {
            SearchResults(
                query, filters, ::back,
                catalog = catalog,
                onReset = { query = ""; filters = EventFilters() },
                onSearch = { open(DiscoverRoute.Search) },
                onFilter = { open(DiscoverRoute.Filter) },
                onEvent = ::openEvent,
                bottomBar = {
                    JrBottomNavigationBar(DiscoverRoute.Events, ::openTab, containerColor = EventCardWhite)
                }
            )
        }
        composable("server-event/{slug}") { entry ->
            ServerEventDetail(
                entry.arguments?.getString("slug").orEmpty(), ::back,
                onNavigateToServerRegistration, serverEvents
            )
        }
        composable("${DiscoverRoute.Detail}/{eventId}") { entry ->
            val event = trailEvents.first { it.id == entry.arguments?.getString("eventId") }
            EventDetail(
                event, ::back,
                onRoute = { open(DiscoverRoute.Route) },
                onSchedule = { open(DiscoverRoute.Schedule) },
                onCategories = { open(DiscoverRoute.Categories) }
            )
        }
        composable(DiscoverRoute.Route) {
            EventRoute(::back) { open(DiscoverRoute.Categories) }
        }
        composable(DiscoverRoute.Schedule) {
            EventSchedule(
                onBack = ::back,
                onRoute = {
                    navigation.navigate(DiscoverRoute.Route) {
                        popUpTo(DiscoverRoute.Schedule) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCategories = { open(DiscoverRoute.Categories) }
            )
        }
        composable(DiscoverRoute.Categories) {
            EventCategories(::back, onNavigateToLogin)
        }
        composable(DiscoverRoute.SoldOut) {
            SoldOutEvent(::back) { openTab(DiscoverRoute.Events) }
        }
    }
}
