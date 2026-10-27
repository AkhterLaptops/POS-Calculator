package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PosCalculationResult
import com.example.ui.theme.DarkSlate
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldMint
import com.example.ui.theme.EmeraldPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PosResultCards(
    result: PosCalculationResult,
    currencySymbol: String,
    onCopiedToClipboard: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var justCopiedPos by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // CARD 1: POS AMOUNT (Hero Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    val textToCopy = String.format(java.util.Locale.US, "%.2f", result.posMachineAmount)
                    copyTextToClipboard(context, textToCopy)
                    justCopiedPos = true
                    onCopiedToClipboard("Copied POS amount: $currencySymbol $textToCopy")
                    scope.launch {
                        delay(2000)
                        justCopiedPos = false
                    }
                }
                .testTag("card_pos_machine_amount"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkSlate
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF16251E),
                                DarkSlate
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                // Header Row (Point 6 & 7: App logo with text "POS Amount" & updated subtext Point 5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pos_launcher_icon_1791356608041),
                                contentDescription = "POS Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column {
                            Text(
                                text = "POS Amount",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                color = EmeraldAccent
                            )
                            Text(
                                text = "Punch this exact Amount on POS Machine",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Copy Pill Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (justCopiedPos) EmeraldAccent else Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.clickable {
                            val textToCopy = String.format(java.util.Locale.US, "%.2f", result.posMachineAmount)
                            copyTextToClipboard(context, textToCopy)
                            justCopiedPos = true
                            onCopiedToClipboard("Copied POS amount: $currencySymbol $textToCopy")
                            scope.launch {
                                delay(2000)
                                justCopiedPos = false
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (justCopiedPos) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy Amount",
                                tint = if (justCopiedPos) DarkSlate else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (justCopiedPos) "COPIED" else "COPY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (justCopiedPos) DarkSlate else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Huge bold formatted number
                Text(
                    text = result.formatCurrency(result.posMachineAmount, currencySymbol),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = Color.White,
                    modifier = Modifier.testTag("text_pos_machine_amount")
                )
            }
        }

        // Point 4: Vertical Up & Down order for "You will Receive" and "POS Charges"
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // CARD 2: THIS AMOUNT WILL BE RECEIVED (Net Payout)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("card_received_amount"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldMint,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Bank",
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            // Point 1 & 2: Color matches "POS Amount" (EmeraldAccent) & Text is "You will Receive"
                            Text(
                                text = "You will Receive",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp,
                                color = EmeraldAccent
                            )
                            Text(
                                text = "Net into bank account",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = result.formatCurrency(result.amountReceived, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("text_received_amount")
                    )
                }
            }

            // CARD 3: TOTAL CHARGES ON TRANSACTION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("card_total_charges"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFE5A93B).copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFF3CD),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = "Fee",
                                    tint = Color(0xFF856404),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "POS Charges",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                color = Color(0xFF856404)
                            )
                            Text(
                                text = "Rate: ${result.formatRate()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Text(
                        text = result.formatCurrency(result.totalCharges, currencySymbol),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color(0xFFBA4A00),
                        modifier = Modifier.testTag("text_total_charges")
                    )
                }
            }
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("POS Amount", text)
    clipboard.setPrimaryClip(clip)
}
