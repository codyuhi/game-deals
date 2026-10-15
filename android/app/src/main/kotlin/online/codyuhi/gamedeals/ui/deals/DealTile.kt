package online.codyuhi.gamedeals.ui.deals

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.Store
import online.codyuhi.gamedeals.domain.formatPrice
import online.codyuhi.gamedeals.domain.formatScore
import online.codyuhi.gamedeals.ui.components.CedarCard
import online.codyuhi.gamedeals.ui.components.CedarPill
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.components.SavingsBadge
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow
import online.codyuhi.gamedeals.ui.theme.Mono

fun shareDeal(context: Context, deal: Deal, storeName: String) {
    val savingsText = if (deal.savings >= 99.9) "FREE" else "${deal.savings.toInt()}% off"
    val text = "Check out this PC game deal: ${deal.title} is $savingsText for $${formatPrice(deal.salePrice)} on $storeName!\nhttps://www.cheapshark.com/redirect?dealID=${deal.dealID}"
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Share game deal"))
}

@Composable
fun DealTile(
    deal: Deal,
    store: Store?,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val storeName = store?.storeName ?: "Store #${deal.storeID}"

    CedarCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Store pill, Deal rating, Share, Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (store != null && store.iconUrl.isNotBlank()) {
                        AsyncImage(
                            model = store.iconUrl,
                            contentDescription = store.storeName,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Text(
                        text = storeName,
                        style = Eyebrow,
                        color = Cedar.colors.brand,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    CedarPill(
                        text = "Score ${formatScore(deal.dealRating)}",
                        bgColor = Cedar.colors.inset,
                        textColor = Cedar.colors.ink3,
                    )

                    Spacer(Modifier.width(4.dp))

                    IconButton(
                        onClick = { shareDeal(context, deal, storeName) },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Lucide.Share2,
                            contentDescription = "Share deal",
                            tint = Cedar.colors.ink3,
                            modifier = Modifier.size(16.dp),
                        )
                    }

                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Lucide.Heart,
                            contentDescription = "Favorite game",
                            tint = Cedar.colors.rust,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Body: Thumbnail and Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Game thumbnail
                AsyncImage(
                    model = deal.thumb,
                    contentDescription = deal.title,
                    modifier = Modifier
                        .size(width = 80.dp, height = 50.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(Cedar.colors.inset),
                    contentScale = ContentScale.Crop,
                )

                // Title and Prices
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deal.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Cedar.colors.ink,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SavingsBadge(savingsPercent = deal.savings)

                        Text(
                            text = if (deal.salePrice == 0.0) "FREE" else "$${formatPrice(deal.salePrice)}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Cedar.colors.spruce,
                        )

                        if (deal.normalPrice > deal.salePrice) {
                            Text(
                                text = "$${formatPrice(deal.normalPrice)}",
                                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.LineThrough),
                                color = Cedar.colors.ink4,
                            )
                        }
                    }
                }
            }

            // Footer tags: Steam rating & Metacritic if available
            if (deal.steamRatingText != null || deal.metacriticScore > 0) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (deal.steamRatingText != null) {
                        CedarPill(
                            text = "Steam: ${deal.steamRatingPercent}% (${deal.steamRatingText})",
                            bgColor = Cedar.colors.inset,
                            textColor = Cedar.colors.ink3,
                        )
                    }

                    if (deal.metacriticScore > 0) {
                        CedarPill(
                            text = "Metacritic: ${deal.metacriticScore}",
                            bgColor = if (deal.metacriticScore >= 75) Cedar.colors.okBg else Cedar.colors.inset,
                            textColor = if (deal.metacriticScore >= 75) Cedar.colors.ok else Cedar.colors.ink3,
                        )
                    }
                }
            }
        }
    }
}
