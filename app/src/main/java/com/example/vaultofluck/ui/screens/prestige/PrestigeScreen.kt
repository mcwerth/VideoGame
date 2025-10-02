package com.example.vaultofluck.ui.screens.prestige

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.ui.viewmodel.PrestigeUiState
import com.example.vaultofluck.ui.viewmodel.PrestigeViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PrestigeRoute(viewModel: PrestigeViewModel) {
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
        PrestigeScreen(
            state = uiState,
            onPrestige = viewModel::prestige,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun PrestigeScreen(
    state: PrestigeUiState,
    modifier: Modifier = Modifier,
    onPrestige: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Prestige", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Coins available: ${"%,.0f".format(state.coins)}")
                Text(text = "Essence on reset: ${state.essencePreview}")
                Button(onClick = onPrestige) { Text("Prestige now") }
            }
        }
        state.lastResult?.let { result ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Last prestige gained ${result.essenceGained} essence")
                    Text(text = "Total prestiges ${result.totalPrestiges}")
                }
            }
        }
    }
}
