package com.example.vaultofluck.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.domain.model.Currency
import com.example.vaultofluck.ui.components.GameCard
import com.example.vaultofluck.ui.viewmodel.HomeUiState
import com.example.vaultofluck.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeRoute(
    viewModel: HomeViewModel,
    onNavigateGacha: () -> Unit,
    onNavigateRun: () -> Unit,
    onNavigatePrestige: () -> Unit,
    onNavigateQuests: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateOdds: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.state.collectLatest { state ->
            state.message?.let {
                snackbar.showSnackbar(it)
                viewModel.consumeMessage()
            }
        }
    }
    val uiState by viewModel.state.collectAsState()
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        HomeScreen(
            state = uiState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            onStartRun = { depth -> viewModel.startRun(depth) },
            onNavigateGacha = onNavigateGacha,
            onNavigateRun = onNavigateRun,
            onNavigatePrestige = onNavigatePrestige,
            onNavigateQuests = onNavigateQuests,
            onNavigateSettings = onNavigateSettings,
            onNavigateOdds = onNavigateOdds
        )
    }
}

@Composable
private fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onStartRun: (Int) -> Unit,
    onNavigateGacha: () -> Unit,
    onNavigateRun: () -> Unit,
    onNavigatePrestige: () -> Unit,
    onNavigateQuests: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateOdds: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Vault Overview", style = MaterialTheme.typography.headlineSmall)
        CurrencyRow(state.currencies)
        state.lastIdleTick?.let {
            Text(text = "Idle gain: ${"%.1f".format(it.coinsEarned)} coins in ${it.elapsedSeconds}s")
        }
        GameCard(title = "Quick Actions") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { onStartRun(1) }) { Text("Start Run") }
                Button(onClick = onNavigateGacha) { Text("Spin Wheel") }
                Button(onClick = onNavigatePrestige) { Text("Prestige") }
                Button(onClick = onNavigateRun) { Text("Run Status") }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onNavigateQuests) { Text("Quests") }
                Button(onClick = onNavigateSettings) { Text("Settings") }
                Button(onClick = onNavigateOdds) { Text("Odds") }
            }
        }
        state.currentRun?.let { run ->
            GameCard(title = "Current Run") {
                Text(text = "Depth ${run.depth} | Status ${run.status}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = if (run.endedAt == null) "Timer running" else "Ended")
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun CurrencyRow(currencies: List<Currency>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(currencies) { currency ->
            GameCard(title = currency.name, modifier = Modifier.width(160.dp)) {
                AnimatedContent(targetState = currency.amount) { value ->
                    Text(text = "${"%,.0f".format(value)}", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
