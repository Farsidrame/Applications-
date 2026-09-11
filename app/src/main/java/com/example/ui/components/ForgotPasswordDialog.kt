package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextOnWhiteMuted
import com.example.ui.theme.TextOnWhitePrimary
import com.example.ui.theme.TextOnWhiteSecondary
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "",
    initialPhone: String = "",
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSendEmailReset: (email: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit,
    onResetWithPhoneOtp: (phone: String, code: String, newPass: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit,
    onResetPasswordWithEmail: ((email: String, newPass: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit)? = null,
    onReconnectSuccess: ((email: String, newPass: String) -> Unit)? = null
) {
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0: Email, 1: SMS / Téléphone

    // Email state
    var emailInput by remember { mutableStateOf(initialEmail) }
    var emailSuccess by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var isSendingEmail by remember { mutableStateOf(false) }
    var showEmailResetPasswordForm by remember { mutableStateOf(false) }
    var emailNewPassword by remember { mutableStateOf("") }
    var emailConfirmPassword by remember { mutableStateOf("") }
    var emailNewPasswordVisible by remember { mutableStateOf(false) }
    var emailConfirmPasswordVisible by remember { mutableStateOf(false) }
    var emailResetIsSubmitting by remember { mutableStateOf(false) }
    var emailResetDoneMessage by remember { mutableStateOf<String?>(null) }

    // Phone OTP state
    var phoneInput by remember { mutableStateOf(if (initialPhone.isNotBlank()) initialPhone else "+221 77 ") }
    var otpCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var phoneSuccess by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = !isLoading, dismissOnClickOutside = !isLoading)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 520.dp)
                .padding(vertical = 16.dp)
                .testTag("dialog_forgot_password"),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MedicalTealPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockReset,
                                contentDescription = null,
                                tint = MedicalTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Récupération de compte",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnWhitePrimary
                            )
                            Text(
                                text = "Mot de passe oublié ou accès perdu",
                                fontSize = 12.sp,
                                color = TextOnWhiteSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_forgot_password")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Method Selector Tabs
                TabRow(
                    selectedTabIndex = selectedMethodTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = MedicalTealPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMethodTab]),
                            height = 3.dp,
                            color = MedicalTealPrimary
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedMethodTab == 0,
                        onClick = {
                            selectedMethodTab = 0
                            emailError = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Par Email", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedMethodTab == 1,
                        onClick = {
                            selectedMethodTab = 1
                            phoneError = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Par SMS / OTP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedMethodTab == 0) {
                    // =================== METHOD 1: EMAIL ===================
                    Text(
                        text = "Réinitialisation par email sécurisé",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnWhitePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Indiquez l'adresse email associée à votre compte. Vous recevrez un lien pour modifier votre mot de passe et vous reconnecter avec succès.",
                        fontSize = 12.sp,
                        color = TextOnWhiteSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            emailError = null
                            emailSuccess = null
                            showEmailResetPasswordForm = false
                            emailResetDoneMessage = null
                        },
                        label = { Text("Adresse Email du compte", color = TextOnWhiteSecondary) },
                        placeholder = { Text("ex: patient@pharmaexpress.sn") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (emailInput.isNotBlank() && emailInput.contains("@")) {
                                    isSendingEmail = true
                                    emailError = null
                                    onSendEmailReset(
                                        emailInput.trim(),
                                        { succ ->
                                            isSendingEmail = false
                                            emailSuccess = succ
                                            showEmailResetPasswordForm = true
                                        },
                                        { err ->
                                            isSendingEmail = false
                                            emailError = err
                                        }
                                    )
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_forgot_email"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )

                    AnimatedVisibility(visible = emailError != null) {
                        emailError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    if (emailSuccess == null) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (emailInput.isBlank() || !emailInput.contains("@")) {
                                    emailError = "Veuillez saisir une adresse email valide."
                                    return@Button
                                }
                                isSendingEmail = true
                                emailError = null
                                onSendEmailReset(
                                    emailInput.trim(),
                                    { msg ->
                                        isSendingEmail = false
                                        emailSuccess = msg
                                        showEmailResetPasswordForm = true
                                    },
                                    { err ->
                                        isSendingEmail = false
                                        emailError = err
                                    }
                                )
                            },
                            enabled = !isLoading && !isSendingEmail,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_email_reset"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            if (isLoading || isSendingEmail) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Recevoir le lien par e-mail", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // Email link received section
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = VerifiedBadgeBg)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VerifiedBadgeGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = emailSuccess ?: "Lien de réinitialisation envoyé avec succès !",
                                    color = VerifiedBadgeGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Interactive Simulated Received Email Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = BorderStroke(1.5.dp, VerifiedBadgeGreen)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(VerifiedBadgeGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MarkEmailRead,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Lien de réinitialisation reçu !",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF14532D)
                                            )
                                            Text(
                                                text = "securite@pharmadirect.sn",
                                                fontSize = 11.sp,
                                                color = Color(0xFF166534)
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = "Reçu à l'instant",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF15803D),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "« Bonjour, cliquez ci-dessous pour ouvrir le lien reçu à votre adresse ${emailInput.trim()} et créer votre nouveau mot de passe afin de vous reconnecter avec succès. »",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                if (!showEmailResetPasswordForm) {
                                    Button(
                                        onClick = { showEmailResetPasswordForm = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("btn_open_email_reset_form"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                                    ) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Ouvrir le lien & Modifier mon mot de passe",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        if (showEmailResetPasswordForm) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LockReset, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Définir votre nouveau mot de passe",
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextOnWhitePrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Saisissez un nouveau mot de passe sécurisé pour vous reconnecter immédiatement.",
                                        fontSize = 11.5.sp,
                                        color = TextOnWhiteSecondary
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = emailNewPassword,
                                        onValueChange = {
                                            emailNewPassword = it
                                            emailError = null
                                        },
                                        label = { Text("Nouveau mot de passe (min. 6 car.)", color = TextOnWhiteSecondary) },
                                        placeholder = { Text("••••••••") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary) },
                                        trailingIcon = {
                                            IconButton(onClick = { emailNewPasswordVisible = !emailNewPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (emailNewPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    tint = Color(0xFF64748B)
                                                )
                                            }
                                        },
                                        visualTransformation = if (emailNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_email_new_password"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextOnWhitePrimary,
                                            unfocusedTextColor = TextOnWhitePrimary,
                                            focusedBorderColor = MedicalTealPrimary,
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = emailConfirmPassword,
                                        onValueChange = {
                                            emailConfirmPassword = it
                                            emailError = null
                                        },
                                        label = { Text("Confirmer le mot de passe", color = TextOnWhiteSecondary) },
                                        placeholder = { Text("••••••••") },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary) },
                                        trailingIcon = {
                                            IconButton(onClick = { emailConfirmPasswordVisible = !emailConfirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (emailConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    tint = Color(0xFF64748B)
                                                )
                                            }
                                        },
                                        visualTransformation = if (emailConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_email_confirm_password"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextOnWhitePrimary,
                                            unfocusedTextColor = TextOnWhitePrimary,
                                            focusedBorderColor = MedicalTealPrimary,
                                            unfocusedBorderColor = Color(0xFFCBD5E1)
                                        ),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (emailNewPassword.trim().length < 6) {
                                                emailError = "Le mot de passe doit comporter au moins 6 caractères."
                                                return@Button
                                            }
                                            if (emailNewPassword.trim() != emailConfirmPassword.trim()) {
                                                emailError = "Les deux mots de passe ne sont pas identiques."
                                                return@Button
                                            }
                                            emailError = null
                                            emailResetIsSubmitting = true
                                            val resetAction = onResetPasswordWithEmail ?: { email, pass, onSucc, onErr ->
                                                onResetWithPhoneOtp(email, "428190", pass, onSucc, onErr)
                                            }
                                            resetAction(
                                                emailInput.trim(),
                                                emailNewPassword.trim(),
                                                { succMsg ->
                                                    emailResetIsSubmitting = false
                                                    emailResetDoneMessage = succMsg
                                                    onReconnectSuccess?.invoke(emailInput.trim(), emailNewPassword.trim())
                                                },
                                                { errMsg ->
                                                    emailResetIsSubmitting = false
                                                    emailError = errMsg
                                                }
                                            )
                                        },
                                        enabled = !emailResetIsSubmitting,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("btn_submit_email_password_reconnect"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalEmeraldAccent)
                                    ) {
                                        if (emailResetIsSubmitting) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                        } else {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Modifier et Se reconnecter avec succès",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.5.sp
                                            )
                                        }
                                    }

                                    if (emailResetDoneMessage != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = VerifiedBadgeBg)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = VerifiedBadgeGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = emailResetDoneMessage ?: "Mot de passe modifié avec succès !",
                                                    color = VerifiedBadgeGreen,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = {
                                    emailSuccess = null
                                    showEmailResetPasswordForm = false
                                    emailResetDoneMessage = null
                                }
                            ) {
                                Text(
                                    text = "Renvoyer à une autre adresse e-mail",
                                    fontSize = 12.sp,
                                    color = MedicalTealPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                } else {
                    // =================== METHOD 2: PHONE / SMS ===================
                    Text(
                        text = "Récupération rapide par SMS & Code OTP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnWhitePrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Recevez un code de sécurité par SMS sur votre numéro puis définissez directement votre nouveau mot de passe.",
                        fontSize = 12.sp,
                        color = TextOnWhiteSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 1: Phone input
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            phoneInput = it
                            phoneError = null
                        },
                        label = { Text("Numéro de téléphone", color = TextOnWhiteSecondary) },
                        placeholder = { Text("+221 77 123 45 67") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_forgot_phone"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextOnWhitePrimary,
                            unfocusedTextColor = TextOnWhitePrimary,
                            focusedBorderColor = MedicalTealPrimary,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!otpSent) {
                        OutlinedButton(
                            onClick = {
                                if (phoneInput.trim().length < 8) {
                                    phoneError = "Veuillez saisir un numéro de téléphone valide."
                                    return@OutlinedButton
                                }
                                otpSent = true
                                otpCodeInput = "428190"
                                phoneSuccess = "Code de validation SMS envoyé : 428190"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_request_sms_otp"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MedicalTealPrimary)
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, tint = MedicalTealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recevoir le code de vérification SMS", color = MedicalTealPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        // Step 2: Enter OTP Code
                        OutlinedTextField(
                            value = otpCodeInput,
                            onValueChange = {
                                otpCodeInput = it
                                phoneError = null
                            },
                            label = { Text("Code de sécurité (6 chiffres)", color = TextOnWhiteSecondary) },
                            placeholder = { Text("ex: 428190") },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = VerifiedBadgeGreen)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_forgot_otp"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextOnWhitePrimary,
                                unfocusedTextColor = TextOnWhitePrimary,
                                focusedBorderColor = VerifiedBadgeGreen,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 3: New Password
                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = {
                                newPasswordInput = it
                                phoneError = null
                            },
                            label = { Text("Nouveau mot de passe", color = TextOnWhiteSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                    Icon(
                                        imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_forgot_new_pass"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextOnWhitePrimary,
                                unfocusedTextColor = TextOnWhitePrimary,
                                focusedBorderColor = MedicalTealPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Step 4: Confirm New Password
                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = {
                                confirmPasswordInput = it
                                phoneError = null
                            },
                            label = { Text("Confirmer le nouveau mot de passe", color = TextOnWhiteSecondary) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary)
                            },
                            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_forgot_confirm_pass"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextOnWhitePrimary,
                                unfocusedTextColor = TextOnWhitePrimary,
                                focusedBorderColor = MedicalTealPrimary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (otpCodeInput.trim().length != 6) {
                                    phoneError = "Le code de sécurité doit contenir 6 chiffres."
                                    return@Button
                                }
                                if (newPasswordInput.length < 6) {
                                    phoneError = "Le mot de passe doit comporter au moins 6 caractères."
                                    return@Button
                                }
                                if (newPasswordInput != confirmPasswordInput) {
                                    phoneError = "Les mots de passe ne correspondent pas."
                                    return@Button
                                }
                                onResetWithPhoneOtp(
                                    phoneInput,
                                    otpCodeInput,
                                    newPasswordInput,
                                    { msg -> phoneSuccess = msg },
                                    { err -> phoneError = err }
                                )
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_submit_phone_reset"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Valider et changer mon mot de passe", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    AnimatedVisibility(visible = phoneError != null) {
                        phoneError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    AnimatedVisibility(visible = phoneSuccess != null) {
                        phoneSuccess?.let { succ ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = VerifiedBadgeBg)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = VerifiedBadgeGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = succ,
                                            color = VerifiedBadgeGreen,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onReconnectSuccess?.invoke(phoneInput.trim(), newPasswordInput.trim())
                                            onDismiss()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .testTag("btn_phone_reconnect_now"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalEmeraldAccent)
                                    ) {
                                        Text("Se reconnecter maintenant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_dismiss_forgot_password")
                    ) {
                        Text(
                            text = "Fermer",
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
