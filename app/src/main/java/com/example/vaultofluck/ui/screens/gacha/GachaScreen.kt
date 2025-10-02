package com.example.vaultofluck.ui.screens.gacha

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.ui.viewmodel.GachaUiState
import com.example.vaultofluck.ui.viewmodel.GachaViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GachaRoute(viewModel: GachaViewModel) {
    val snackbar = remember { SnackbarHostState() }
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(Unit) {
        viewModel.state.collectLatest { state ->
            state.message?.let {
                snackbar.showSnackbar(it)
                viewModel.consumeMessage()
            }
            if (state.lastItems.any { it.rarity == "Legendary" || it.rarity == "Mythic" }) {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            }
        }
    }
    val uiState by viewModel.state.collectAsState()
    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        GachaScreen(
            state = uiState,
            onPull = viewModel::pull,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun GachaScreen(
    state: GachaUiState,
    modifier: Modifier = Modifier,
    onPull: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Tokens: ${"%,.0f".format(state.tokens)}", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Pity counter: ${state.pity}")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onPull(1) }, enabled = !state.isRolling) { Text("x1") }
            Button(onClick = { onPull(10) }, enabled = !state.isRolling) { Text("x10") }
            Button(onClick = { onPull(50) }, enabled = !state.isRolling) { Text("x50") }
        }
        AnimatedVisibility(visible = state.lastItems.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Last pull", style = MaterialTheme.typography.titleMedium)
                    state.lastItems.forEach { item ->
                        Text(
                            text = "${item.name} (${item.rarity})",
                            color = rarityColor(item.rarity)
                        )
                    }
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Odds Snapshot", style = MaterialTheme.typography.titleMedium)
                state.audit.forEach { (rarity, weight) ->
                    Text(text = "$rarity -> ${(weight * 100).format(2)}%")
                }
            }
        }
    }
}

private fun rarityColor(rarity: String): Color = when (rarity) {
    "Common" -> Color(0xFFA0A0A0)
    "Rare" -> Color(0xFF3F51B5)
    "Epic" -> Color(0xFF9C27B0)
    "Legendary" -> Color(0xFFFFC107)
    "Mythic" -> Color(0xFFFF5252)
    else -> Color.White
}

private fun Double.format(digits: Int): String = "%.${'$'}digitsf".format(this)
