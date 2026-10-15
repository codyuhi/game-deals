package online.codyuhi.gamedeals.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow
import online.codyuhi.gamedeals.ui.theme.ThemeMode

@Composable
fun CedarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    text: String,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = Cedar.colors.spruce,
            contentColor = Cedar.colors.onSpruce,
            disabledContainerColor = Cedar.colors.inset,
            disabledContentColor = Cedar.colors.ink4,
        ),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun CedarOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    text: String,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, Cedar.colors.lineStrong),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Cedar.colors.ink2,
            disabledContentColor = Cedar.colors.ink4,
        ),
    ) {
        if (leadingIcon != null) {
            Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun CedarCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke = BorderStroke(1.dp, Cedar.colors.line),
    content: @Composable ColumnScope.() -> Unit,
) {
    OutlinedCard(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Cedar.colors.surface,
            contentColor = Cedar.colors.ink2,
        ),
        border = border,
    ) {
        Column(content = content)
    }
}

@Composable
fun CedarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = label?.let { { Text(it) } },
        placeholder = placeholder?.let { { Text(it, color = Cedar.colors.ink4) } },
        singleLine = singleLine,
        enabled = enabled,
        isError = isError,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Cedar.colors.spruce,
            unfocusedBorderColor = Cedar.colors.lineStrong,
            focusedTextColor = Cedar.colors.ink,
            unfocusedTextColor = Cedar.colors.ink2,
            focusedContainerColor = Cedar.colors.field,
            unfocusedContainerColor = Cedar.colors.field,
        ),
    )
}

@Composable
fun CedarPill(
    text: String,
    modifier: Modifier = Modifier,
    bgColor: Color = Cedar.colors.inset,
    textColor: Color = Cedar.colors.ink3,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(13.dp))
            Spacer(Modifier.width(4.dp))
        }
        Text(text, color = textColor, style = Eyebrow, maxLines = 1)
    }
}

@Composable
fun SavingsBadge(
    savingsPercent: Double,
    modifier: Modifier = Modifier,
) {
    val isFree = savingsPercent >= 99.9
    val text = if (isFree) "FREE" else "-${savingsPercent.toInt()}%"
    val bgColor = if (isFree) Cedar.colors.okBg else Cedar.colors.errBg
    val textColor = if (isFree) Cedar.colors.ok else Cedar.colors.rust

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
fun CedarThemeToggle(
    mode: ThemeMode,
    onToggle: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = {
            val next = when (mode) {
                ThemeMode.System -> ThemeMode.Dark
                ThemeMode.Dark -> ThemeMode.Light
                ThemeMode.Light -> ThemeMode.System
            }
            onToggle(next)
        },
        modifier = modifier
            .size(44.dp)
            .clip(MaterialTheme.shapes.small)
            .background(Cedar.colors.surface),
    ) {
        val (icon, desc) = when (mode) {
            ThemeMode.System -> Lucide.Sun to "System theme (tap for dark)"
            ThemeMode.Dark -> Lucide.Moon to "Dark theme (tap for light)"
            ThemeMode.Light -> Lucide.Sun to "Light theme (tap for system)"
        }
        Icon(icon, contentDescription = desc, tint = Cedar.colors.ink2, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun OfflineBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Surface(
            color = Cedar.colors.warnBg,
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Lucide.CloudOff,
                    contentDescription = null,
                    tint = Cedar.colors.warn,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Offline: showing cached game deals",
                    color = Cedar.colors.warn,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}
