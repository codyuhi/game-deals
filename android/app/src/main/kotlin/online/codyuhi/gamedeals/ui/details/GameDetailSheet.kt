package online.codyuhi.gamedeals.ui.details

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import online.codyuhi.gamedeals.domain.GameDeal
import online.codyuhi.gamedeals.domain.GameDetails
import online.codyuhi.gamedeals.domain.Store
import online.codyuhi.gamedeals.domain.formatDate
import online.codyuhi.gamedeals.domain.formatPrice
import online.codyuhi.gamedeals.ui.components.CedarButton
import online.codyuhi.gamedeals.ui.components.CedarCard
import online.codyuhi.gamedeals.ui.components.CedarPill
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.components.SavingsBadge
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailSheet(
    gameID: String,
    details: GameDetails?,
    isLoading: Boolean,
    storeMap: Map<String, Store>,
    onDismiss: () -> Unit,
    onFavoriteClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Cedar.colors.surface,
        modifier = modifier,
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Cedar.colors.spruce)
            }
        } else if (details == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Unable to load game details.", color = Cedar.colors.ink2)
            }
        } else {
            val info = details.info

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                // Header with thumbnail, title, favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AsyncImage(
                        model = info.thumb,
                        contentDescription = info.title,
                        modifier = Modifier
                            .size(width = 110.dp, height = 70.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Cedar.colors.inset),
                        contentScale = ContentScale.Crop,
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Game Overview",
                            style = Eyebrow,
                            color = Cedar.colors.brand,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = info.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = Cedar.colors.ink,
                        )
                    }

                    IconButton(
                        onClick = { onFavoriteClick(gameID, info.title) },
                    ) {
                        Icon(
                            imageVector = Lucide.Heart,
                            contentDescription = "Favorite game",
                            tint = Cedar.colors.rust,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Historical low callout
                if (details.cheapestPriceEver != null) {
                    val dateStr = formatDate(details.cheapestPriceEver.date)
                    CedarCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("All-Time Historical Low", style = Eyebrow, color = Cedar.colors.ink4)
                                Text(
                                    text = "$${formatPrice(details.cheapestPriceEver.price)}",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Cedar.colors.spruce,
                                )
                            }
                            Text(dateStr, style = MaterialTheme.typography.bodySmall, color = Cedar.colors.ink4)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = "Deals Across Stores (${details.deals.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Cedar.colors.ink,
                )

                Spacer(Modifier.height(8.dp))

                if (details.deals.isEmpty()) {
                    Text(
                        "No active storefront deals found currently.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cedar.colors.ink4,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        details.deals.forEach { deal ->
                            val store = storeMap[deal.storeID]
                            val storeName = store?.storeName ?: "Store #${deal.storeID}"
                            GameDealRow(
                                deal = deal,
                                store = store,
                                storeName = storeName,
                                onOpenStore = {
                                    openStoreUrl(context, "https://www.cheapshark.com/redirect?dealID=${deal.dealID}")
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun GameDealRow(
    deal: GameDeal,
    store: Store?,
    storeName: String,
    onOpenStore: () -> Unit,
) {
    CedarCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (store != null && store.iconUrl.isNotBlank()) {
                    AsyncImage(
                        model = store.iconUrl,
                        contentDescription = storeName,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        contentScale = ContentScale.Fit,
                    )
                }

                Column {
                    Text(
                        text = storeName,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Cedar.colors.ink,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        SavingsBadge(savingsPercent = deal.savings)

                        Text(
                            text = if (deal.price == 0.0) "FREE" else "$${formatPrice(deal.price)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Cedar.colors.spruce,
                        )

                        if (deal.retailPrice > deal.price) {
                            Text(
                                text = "$${formatPrice(deal.retailPrice)}",
                                style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough),
                                color = Cedar.colors.ink4,
                            )
                        }
                    }
                }
            }

            CedarButton(
                onClick = onOpenStore,
                text = "Buy",
                leadingIcon = Lucide.ExternalLink,
            )
        }
    }
}
