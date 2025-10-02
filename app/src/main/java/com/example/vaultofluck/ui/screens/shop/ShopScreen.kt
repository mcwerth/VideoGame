package com.example.vaultofluck.ui.screens.shop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.vaultofluck.domain.model.Upgrade
import com.example.vaultofluck.ui.viewmodel.ShopUiState
import com.example.vaultofluck.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ShopRoute(viewModel: ShopViewModel) {
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
        ShopScreen(
            state = uiState,
            onBuy = viewModel::buyUpgrade,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun ShopScreen(
    state: ShopUiState,
    modifier: Modifier = Modifier,
    onBuy: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.upgrades, key = { it.id }) { upgrade ->
            UpgradeRow(upgrade, onBuy)
        }
    }
}

@Composable
private fun UpgradeRow(upgrade: Upgrade, onBuy: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = upgrade.type.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Level ${upgrade.level}/${upgrade.maxLevel}")
            Text(text = "Cost: ${"%,.0f".format(upgrade.cost)}")
            Button(onClick = { onBuy(upgrade.id) }) { Text("Upgrade") }
        }
    }
}
