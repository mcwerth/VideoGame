package com.example.vaultofluck.ui.screens.quests

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
import com.example.vaultofluck.domain.model.Quest
import com.example.vaultofluck.ui.viewmodel.QuestsUiState
import com.example.vaultofluck.ui.viewmodel.QuestsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun QuestsRoute(viewModel: QuestsViewModel) {
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
        QuestsScreen(
            state = uiState,
            onClaim = viewModel::claim,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun QuestsScreen(
    state: QuestsUiState,
    modifier: Modifier = Modifier,
    onClaim: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.quests, key = { it.id }) { quest ->
            QuestRow(quest, onClaim)
        }
    }
}

@Composable
private fun QuestRow(quest: Quest, onClaim: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = quest.type.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Progress ${"%.0f".format(quest.progress)} / ${quest.target}")
            Text(text = "Reward: ${quest.rewardTokens} tokens")
            Button(onClick = { onClaim(quest.id) }, enabled = !quest.claimed && quest.progress >= quest.target) {
                Text(if (quest.claimed) "Claimed" else "Claim")
            }
        }
    }
}
