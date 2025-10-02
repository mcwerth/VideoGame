package com.example.vaultofluck.ui.screens.generators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import com.example.vaultofluck.domain.model.Generator
import com.example.vaultofluck.core.economy.Economy
import com.example.vaultofluck.ui.components.GameCard
import com.example.vaultofluck.ui.viewmodel.GeneratorsUiState
import com.example.vaultofluck.ui.viewmodel.GeneratorsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun GeneratorsRoute(viewModel: GeneratorsViewModel) {
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
        GeneratorsScreen(
            state = uiState,
            onUpgrade = viewModel::upgrade,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun GeneratorsScreen(
    state: GeneratorsUiState,
    modifier: Modifier = Modifier,
    onUpgrade: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.generators, key = { it.id }) { generator ->
            GeneratorRow(generator = generator, onUpgrade = onUpgrade)
        }
    }
}

@Composable
private fun GeneratorRow(generator: Generator, onUpgrade: (Int) -> Unit) {
    GameCard(title = "Generator ${generator.tier}") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Level ${generator.level}")
            Text(text = "Rate ${"%.2f".format(generator.ratePerSecond)} coins/s")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = if (generator.unlocked) "Upgrade cost ${Economy.generatorLevelCost(generator.tier, generator.level + 1).toInt()}" else "Unlock for ${generator.unlockCost.toInt()}")
                Button(onClick = { onUpgrade(generator.id) }) {
                    Text(text = if (generator.unlocked) "Level Up" else "Unlock")
                }
            }
        }
    }
}
