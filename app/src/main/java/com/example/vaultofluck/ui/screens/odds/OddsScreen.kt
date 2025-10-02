package com.example.vaultofluck.ui.screens.odds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vaultofluck.ui.viewmodel.OddsUiState
import com.example.vaultofluck.ui.viewmodel.OddsViewModel

@Composable
fun OddsRoute(viewModel: OddsViewModel) {
    val uiState by viewModel.state.collectAsState()
    Scaffold { padding ->
        OddsScreen(
            state = uiState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun OddsScreen(
    state: OddsUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Odds & Math", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                state.rarityWeights.forEach { (rarity, weight) ->
                    Text(text = "$rarity: ${(weight * 100).format(2)}%")
                }
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Pity soft start ${state.pitySoftStart} pulls")
                Text("Hard pity ${state.pityHardCap} pulls")
                Text("Current pity ${state.pityCurrent}")
            }
        }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Expected value x1: ${state.evSingles.format(3)}")
                Text("Expected value x10: ${state.evTens.format(3)}")
                Text("Expected value x50: ${state.evFifties.format(3)}")
            }
        }
    }
}

private fun Double.format(digits: Int): String = "%.${'$'}digitsf".format(this)
