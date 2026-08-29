package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentMethod
import com.example.ui.theme.EscrowGreenColor
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.MtnMomoYellow
import com.example.ui.theme.OrangeMoneyColor
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VisaBlueColor
import com.example.ui.theme.WaveBlueColor

@Composable
fun PaymentMethodSelector(
    selectedMethod: PaymentMethod,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PaymentMethodOption(
            method = PaymentMethod.WAVE,
            title = "Wave Mobile Money",
            subtitle = "Paiement direct instantané sans frais (0%) • Validation par notification ou QR Code",
            badge = "Recommandé • 0% Frais",
            badgeColor = WaveBlueColor,
            iconColor = WaveBlueColor,
            isSelected = selectedMethod == PaymentMethod.WAVE,
            onSelect = { onMethodSelected(PaymentMethod.WAVE) }
        )

        PaymentMethodOption(
            method = PaymentMethod.ORANGE_MONEY,
            title = "Orange Money",
            subtitle = "Paiement direct sécurisé • Validation code secret / USSD #144#",
            badge = "Instantané",
            badgeColor = OrangeMoneyColor,
            iconColor = OrangeMoneyColor,
            isSelected = selectedMethod == PaymentMethod.ORANGE_MONEY,
            onSelect = { onMethodSelected(PaymentMethod.ORANGE_MONEY) }
        )

        PaymentMethodOption(
            method = PaymentMethod.MTN_MOMO,
            title = "MTN MoMo",
            subtitle = "Paiement mobile sécurisé par code OTP",
            badge = "Sécurisé",
            badgeColor = Color(0xFFF57F17),
            iconColor = MtnMomoYellow,
            isSelected = selectedMethod == PaymentMethod.MTN_MOMO,
            onSelect = { onMethodSelected(PaymentMethod.MTN_MOMO) }
        )

        PaymentMethodOption(
            method = PaymentMethod.CREDIT_CARD,
            title = "Carte Bancaire (Visa / Mastercard)",
            subtitle = "Paiement en ligne sécurisé 3D-Secure",
            badge = "Banques",
            badgeColor = VisaBlueColor,
            iconColor = VisaBlueColor,
            isSelected = selectedMethod == PaymentMethod.CREDIT_CARD,
            onSelect = { onMethodSelected(PaymentMethod.CREDIT_CARD) }
        )

        PaymentMethodOption(
            method = PaymentMethod.ESCROW_WALLET,
            title = "Garantie Escrow Santé (Fonds bloqués)",
            subtitle = "Argent sécurisé et libéré au pharmacien uniquement après vérification du colis",
            badge = "100% Fiable",
            badgeColor = EscrowGreenColor,
            iconColor = EscrowGreenColor,
            isSelected = selectedMethod == PaymentMethod.ESCROW_WALLET,
            onSelect = { onMethodSelected(PaymentMethod.ESCROW_WALLET) }
        )
    }
}

@Composable
private fun PaymentMethodOption(
    method: PaymentMethod,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    iconColor: Color,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("payment_method_${method.name}")
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MedicalTealLight.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MedicalTealPrimary else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (method) {
                        PaymentMethod.ORANGE_MONEY, PaymentMethod.WAVE, PaymentMethod.MTN_MOMO -> Icons.Default.PhoneAndroid
                        PaymentMethod.CREDIT_CARD -> Icons.Default.CreditCard
                        PaymentMethod.ESCROW_WALLET -> Icons.Default.Shield
                    },
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = badgeColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MedicalTealPrimary
                )
            )
        }
    }
}
