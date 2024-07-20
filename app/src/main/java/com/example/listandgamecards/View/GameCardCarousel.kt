package com.example.listandgamecards.View

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GameCardList(cardItems: List<String>) {
    LazyRow {
        items(cardItems) { cardItem ->
            GameCardView(cardItem = "cardItem")
        }
    }
}

@Composable
fun GameCardView(cardItem: String) {
    Card {
        Row {
            Text(text = cardItem)
            // Add other content for your card layout
        }
    }
}

@Preview
@Composable
fun PreviewLazyRowWithCards() {
    val sampleItems = listOf("Item 1", "Item 2", "Item 3")
    GameCardList(cardItems = sampleItems)
}
