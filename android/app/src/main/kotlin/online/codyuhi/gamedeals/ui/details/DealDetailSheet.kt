package online.codyuhi.gamedeals.ui.details

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.core.net.toUri
import online.codyuhi.gamedeals.domain.DealDetails
import online.codyuhi.gamedeals.domain.Store
import online.codyuhi.gamedeals.domain.formatDate
import online.codyuhi.gamedeals.domain.formatPrice
import online.codyuhi.gamedeals.ui.components.CedarButton
import online.codyuhi.gamedeals.ui.components.CedarOutlinedButton
import online.codyuhi.gamedeals.ui.components.CedarPill
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.components.SavingsBadge
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow

fun openStoreUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Exception) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealDetailSheet(
    dealID: String,
    details: DealDetails?,
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
                Text("Unable to load deal details.", color = Cedar.colors.ink2)
            }
        } else {
            val info = details.gameInfo
            val store = storeMap[info.storeID]
            val storeName = store?.storeName ?: "Store #${info.storeID}"
            val savingsPercent = if (info.retailPrice > 0) {
                ((info.retailPrice - info.salePrice) / info.retailPrice * 100.0).coerceAtLeast(0.0)
            } else 0.0

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                // Header with Store emblem, title, favorite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    AsyncImage(
                        model = info.thumb,
                        contentDescription = info.name,
                        modifier = Modifier
                            .size(width = 110.dp, height = 70.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Cedar.colors.inset),
                        contentScale = ContentScale.Crop,
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = storeName,
                            style = Eyebrow,
                            color = Cedar.colors.brand,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = info.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Cedar.colors.ink,
                        )
                    }

                    IconButton(
                        onClick = { onFavoriteClick(info.gameID, info.name) },
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

                // Price display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SavingsBadge(savingsPercent = savingsPercent)

                    Text(
                        text = if (info.salePrice == 0.0) "FREE" else "$${formatPrice(info.salePrice)}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Cedar.colors.spruce,
                    )

                    if (info.retailPrice > info.salePrice) {
                        Text(
                            text = "$${formatPrice(info.retailPrice)}",
                            style = MaterialTheme.typography.titleMedium.copy(textDecoration = TextDecoration.LineThrough),
                            color = Cedar.colors.ink4,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Cedar.colors.line)
                Spacer(Modifier.height(16.dp))

                // Key metrics: Cheapest price ever, Steam, Metacritic, Publisher
                if (details.cheapestPrice != null) {
                    val dateStr = formatDate(details.cheapestPrice.date)
                    DetailMetricRow(
                        label = "Historical Low",
                        value = "$${formatPrice(details.cheapestPrice.price)} (on $dateStr)",
                    )
                }

                if (info.steamRatingText != null) {
                    DetailMetricRow(
                        label = "Steam Reviews",
                        value = "${info.steamRatingPercent}% ${info.steamRatingText} (${info.steamRatingCount} reviews)",
                    )
                }

                if (info.metacriticScore > 0) {
                    DetailMetricRow(
                        label = "Metacritic",
                        value = "${info.metacriticScore} / 100",
                    )
                }

                if (info.publisher.isNotBlank()) {
                    DetailMetricRow(
                        label = "Publisher",
                        value = info.publisher,
                    )
                }

                if (info.releaseDate > 0) {
                    val relDateStr = formatDate(info.releaseDate)
                    DetailMetricRow(
                        label = "Release Date",
                        value = relDateStr,
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Action buttons: Open Store, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CedarButton(
                        onClick = {
                            openStoreUrl(context, "https://www.cheapshark.com/redirect?dealID=$dealID")
                        },
                        text = "Buy on $storeName",
                        leadingIcon = Lucide.ExternalLink,
                        modifier = Modifier.weight(1f),
                    )

                    CedarOutlinedButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Deal for ${info.name}: $${formatPrice(info.salePrice)} on $storeName!\nhttps://www.cheapshark.com/redirect?dealID=$dealID",
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share deal"))
                        },
                        text = "Share",
                        leadingIcon = Lucide.Share2,
                    )
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailMetricRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Cedar.colors.ink4,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Cedar.colors.ink,
        )
    }
}
