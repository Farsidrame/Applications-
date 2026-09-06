package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Brush
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
import com.example.auth.AuthUser
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueLight
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMuted
import com.example.ui.theme.VerifiedBadgeBg
import com.example.ui.theme.VerifiedBadgeGreen

@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    isLoading: Boolean,
    onSignInEmail: (email: String, password: String, onSuccess: (AuthUser) -> Unit, onError: (String) -> Unit) -> Unit,
    onSignUpEmail: (email: String, password: String, fullName: String, phoneNumber: String, role: String, onSuccess: (AuthUser) -> Unit, onError: (String) -> Unit) -> Unit,
    onSignInPhone: (phoneNumber: String, otp: String, fullName: String, role: String, onSuccess: (AuthUser) -> Unit, onError: (String) -> Unit) -> Unit,
    onSignInGoogle: (email: String, name: String, role: String, onSuccess: (AuthUser) -> Unit, onError: (String) -> Unit) -> Unit,
    onPasswordReset: (email: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit,
    onAuthSuccess: (AuthUser) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Connexion, 1: Inscription, 2: SMS OTP
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    // Sign In form fields
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up form fields
    var registerFullName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPhone by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var registerRole by remember { mutableStateOf("Patient / Client") }
    var acceptedTerms by remember { mutableStateOf(true) }

    // Phone OTP form fields
    var phoneInput by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var phoneUserName by remember { mutableStateOf("") }

    val rolesList = listOf("Patient / Client", "Pharmacien Diplômé", "Livreur Partenaire")

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = !isLoading, dismissOnClickOutside = !isLoading)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .widthIn(max = 560.dp)
                .padding(vertical = 16.dp)
                .testTag("auth_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(MedicalTealPrimary, MedicalTealDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PharmaDirect SN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Portail de Santé Sécurisé",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Fermer",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Authentification & Espace Profil",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Créez votre compte certifié pour synchroniser vos ordonnances et vos livraisons",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }

                // Tabs Navigation
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MedicalTealPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MedicalTealPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                        },
                        modifier = Modifier.testTag("tab_signin"),
                        text = {
                            Text(
                                text = "Connexion",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                        },
                        modifier = Modifier.testTag("tab_signup"),
                        text = {
                            Text(
                                text = "Créer compte",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            errorMessage = null
                        },
                        modifier = Modifier.testTag("tab_phone"),
                        text = {
                            Text(
                                text = "SMS OTP",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = {
                            Icon(imageVector = Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                }

                // Status Message Banners
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { err ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = successMessage != null) {
                    successMessage?.let { msg ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
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
                                    text = msg,
                                    color = VerifiedBadgeGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Form Content based on selected Tab
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Crossfade(targetState = selectedTab, label = "AuthTabCrossfade") { tabIndex ->
                        when (tabIndex) {
                            0 -> {
                                // TAB 0: CONNEXION (SIGN IN)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    OutlinedTextField(
                                        value = loginEmail,
                                        onValueChange = {
                                            loginEmail = it
                                            errorMessage = null
                                        },
                                        label = { Text("Adresse Email") },
                                        placeholder = { Text("votre.email@domaine.com") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Email,
                                                contentDescription = null,
                                                tint = MedicalTealPrimary
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Email,
                                            imeAction = ImeAction.Next
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_login_email"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalTealPrimary,
                                            unfocusedBorderColor = BorderSoft
                                        ),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = loginPassword,
                                        onValueChange = {
                                            loginPassword = it
                                            errorMessage = null
                                        },
                                        label = { Text("Mot de passe") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = MedicalTealPrimary
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = "Afficher mot de passe"
                                                )
                                            }
                                        },
                                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(
                                            onDone = {
                                                if (loginEmail.isNotBlank() && loginPassword.isNotBlank()) {
                                                    onSignInEmail(
                                                        loginEmail,
                                                        loginPassword,
                                                        { user ->
                                                            successMessage = "Connexion réussie !"
                                                            onAuthSuccess(user)
                                                        },
                                                        { err -> errorMessage = err }
                                                    )
                                                }
                                            }
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_login_password"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MedicalTealPrimary,
                                            unfocusedBorderColor = BorderSoft
                                        ),
                                        singleLine = true
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { showResetPasswordDialog = true },
                                            modifier = Modifier.testTag("forgot_password_btn")
                                        ) {
                                            Text(
                                                text = "Mot de passe oublié ?",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = SafeBlueSecondary
                                            )
                                        }
                                    }

                                    // Submit Button
                                    Button(
                                        onClick = {
                                            if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                                errorMessage = "Veuillez saisir votre email et mot de passe."
                                                return@Button
                                            }
                                            onSignInEmail(
                                                loginEmail,
                                                loginPassword,
                                                { user ->
                                                    successMessage = "Bienvenue, ${user.displayName ?: user.email} !"
                                                    onAuthSuccess(user)
                                                },
                                                { err -> errorMessage = err }
                                            )
                                        },
                                        enabled = !isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("btn_submit_login"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(22.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("Connexion sécurisée en cours...", color = Color.White, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Se connecter à mon compte", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                    }

                                    // Quick Demo / Google Sign In Divider
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSoft)
                                        Text(
                                            text = " OU ",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSoft)
                                    }

                                    // Google Fast Auth
                                    OutlinedButton(
                                        onClick = {
                                            val demoEmail = if (loginEmail.isNotBlank()) loginEmail else "patient@pharmaexpress.sn"
                                            val demoName = if (demoEmail.contains("@")) demoEmail.substringBefore("@").replace(".", " ") else "Client PharmaDirect"
                                            onSignInGoogle(
                                                demoEmail,
                                                demoName,
                                                "Patient / Client",
                                                { user ->
                                                    successMessage = "Connexion Google validée !"
                                                    onAuthSuccess(user)
                                                },
                                                { err -> errorMessage = err }
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("btn_google_signin"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryDark)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = SafeBlueSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Continuer avec Google Account",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            1 -> {
                                // TAB 1: INSCRIPTION COMPLÈTE (SIGN UP)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // User Role Selector Pills
                                    Text(
                                        text = "Type de compte / Profession",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryDark
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rolesList.forEach { role ->
                                            val isSelected = registerRole == role
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isSelected) MedicalTealPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSelected) MedicalTealPrimary else BorderSoft,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable { registerRole = role }
                                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = when (role) {
                                                        "Patient / Client" -> "Patient"
                                                        "Pharmacien Diplômé" -> "Pharmacien"
                                                        else -> "Livreur"
                                                    },
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else TextPrimaryDark,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = registerFullName,
                                        onValueChange = {
                                            registerFullName = it
                                            errorMessage = null
                                        },
                                        label = { Text("Nom et Prénom") },
                                        placeholder = { Text("ex: Fatou Diop ou Dr. Babacar Sy") },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = when (registerRole) {
                                                    "Pharmacien Diplômé" -> Icons.Default.LocalPharmacy
                                                    "Livreur Partenaire" -> Icons.Default.TwoWheeler
                                                    else -> Icons.Default.Person
                                                },
                                                contentDescription = null,
                                                tint = MedicalTealPrimary
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_register_name"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = registerEmail,
                                        onValueChange = {
                                            registerEmail = it
                                            errorMessage = null
                                        },
                                        label = { Text("Adresse Email") },
                                        placeholder = { Text("contact@domaine.com") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_register_email"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = registerPhone,
                                        onValueChange = {
                                            registerPhone = it
                                            errorMessage = null
                                        },
                                        label = { Text("Téléphone mobile (SMS notifications)") },
                                        placeholder = { Text("Numéro mobile") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_register_phone"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = registerPassword,
                                        onValueChange = {
                                            registerPassword = it
                                            errorMessage = null
                                        },
                                        label = { Text("Mot de passe sécurisé") },
                                        placeholder = { Text("Au moins 6 caractères") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        trailingIcon = {
                                            IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (registerPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_register_password"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    // Password strength indicator
                                    if (registerPassword.isNotBlank()) {
                                        val strength = when {
                                            registerPassword.length >= 8 && registerPassword.any { it.isDigit() } && registerPassword.any { it.isLetter() } -> "Fort (Recommandé)"
                                            registerPassword.length >= 6 -> "Moyen"
                                            else -> "Trop court (< 6 caractères)"
                                        }
                                        val strengthColor = when {
                                            registerPassword.length >= 8 && registerPassword.any { it.isDigit() } -> VerifiedBadgeGreen
                                            registerPassword.length >= 6 -> DutyPharmacyOrange
                                            else -> MaterialTheme.colorScheme.error
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Sécurité du mot de passe :", fontSize = 11.sp, color = TextSecondaryMuted)
                                            Text(text = strength, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = strengthColor)
                                        }
                                    }

                                    OutlinedTextField(
                                        value = registerConfirmPassword,
                                        onValueChange = {
                                            registerConfirmPassword = it
                                            errorMessage = null
                                        },
                                        label = { Text("Confirmer le mot de passe") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_register_confirm_password"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    // Terms & Conditions Checkbox
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { acceptedTerms = !acceptedTerms },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = acceptedTerms,
                                            onCheckedChange = { acceptedTerms = it },
                                            colors = CheckboxDefaults.colors(checkedColor = MedicalTealPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "J'accepte la politique de confidentialité médicale et la certification des données de santé",
                                            fontSize = 11.sp,
                                            color = TextSecondaryMuted,
                                            lineHeight = 15.sp
                                        )
                                    }

                                    // Submit Register Button
                                    Button(
                                        onClick = {
                                            if (registerFullName.isBlank()) {
                                                errorMessage = "Veuillez renseigner votre nom complet."
                                                return@Button
                                            }
                                            if (registerEmail.isBlank() || !registerEmail.contains("@")) {
                                                errorMessage = "Veuillez saisir une adresse email valide."
                                                return@Button
                                            }
                                            if (registerPassword.length < 6) {
                                                errorMessage = "Le mot de passe doit contenir au moins 6 caractères."
                                                return@Button
                                            }
                                            if (registerPassword != registerConfirmPassword) {
                                                errorMessage = "Les deux mots de passe ne correspondent pas."
                                                return@Button
                                            }
                                            if (!acceptedTerms) {
                                                errorMessage = "Veuillez accepter les conditions de protection des données de santé."
                                                return@Button
                                            }

                                            onSignUpEmail(
                                                registerEmail,
                                                registerPassword,
                                                registerFullName,
                                                registerPhone,
                                                registerRole,
                                                { user ->
                                                    successMessage = "Compte créé avec succès ! Bienvenue $registerFullName."
                                                    onAuthSuccess(user)
                                                },
                                                { err -> errorMessage = err }
                                            )
                                        },
                                        enabled = !isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("btn_submit_register"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalEmeraldAccent)
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("Création du compte en cours...", color = Color.White, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Créer mon compte sécurisé", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // TAB 2: CONNEXION RAPIDE PAR SMS / OTP
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = SafeBlueLight)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Sms,
                                                contentDescription = null,
                                                tint = SafeBlueSecondary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Recevez un code de validation SMS sécurisé instantané sur votre numéro Orange, Wave ou MoMo.",
                                                fontSize = 12.sp,
                                                color = SafeBlueSecondary,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = phoneUserName,
                                        onValueChange = {
                                            phoneUserName = it
                                            errorMessage = null
                                        },
                                        label = { Text("Votre Nom (Optionnel)") },
                                        placeholder = { Text("ex: Amadou Ba") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = phoneInput,
                                        onValueChange = {
                                            phoneInput = it
                                            errorMessage = null
                                        },
                                        label = { Text("Numéro de Téléphone Mobile") },
                                        placeholder = { Text("Numéro de téléphone") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = MedicalTealPrimary)
                                        },
                                        trailingIcon = {
                                            TextButton(
                                                onClick = {
                                                    if (phoneInput.trim().length >= 9) {
                                                        otpSent = true
                                                        otpCodeInput = "428190" // Pre-generate standard secure OTP code for testing convenience
                                                        successMessage = "Code SMS envoyé au $phoneInput : 428190"
                                                    } else {
                                                        errorMessage = "Veuillez renseigner un numéro valide."
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = if (otpSent) "Renvoyer" else "Envoyer SMS",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = MedicalTealPrimary
                                                )
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_phone_auth"),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    if (otpSent) {
                                        OutlinedTextField(
                                            value = otpCodeInput,
                                            onValueChange = {
                                                if (it.length <= 6) {
                                                    otpCodeInput = it
                                                    errorMessage = null
                                                }
                                            },
                                            label = { Text("Code de confirmation SMS (6 chiffres)") },
                                            placeholder = { Text("ex: 428190") },
                                            leadingIcon = {
                                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = VerifiedBadgeGreen)
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("input_otp_code"),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (phoneInput.trim().length < 8) {
                                                errorMessage = "Numéro de téléphone incomplet."
                                                return@Button
                                            }
                                            if (!otpSent) {
                                                otpSent = true
                                                otpCodeInput = "428190"
                                                successMessage = "Code SMS envoyé : 428190"
                                                return@Button
                                            }
                                            if (otpCodeInput.length != 6) {
                                                errorMessage = "Le code OTP doit comporter 6 chiffres."
                                                return@Button
                                            }

                                            onSignInPhone(
                                                phoneInput,
                                                otpCodeInput,
                                                if (phoneUserName.isNotBlank()) phoneUserName else "Client Mobile",
                                                "Patient / Client",
                                                { user ->
                                                    successMessage = "Authentification par SMS validée avec succès !"
                                                    onAuthSuccess(user)
                                                },
                                                { err -> errorMessage = err }
                                            )
                                        },
                                        enabled = !isLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("btn_submit_phone_auth"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                                        } else {
                                            Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (otpSent) "Valider le code de sécurité" else "Recevoir mon code par SMS",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer Actions (Mode Invité)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(color = BorderSoft)
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_skip_auth")
                    ) {
                        Text(
                            text = "Continuer sans compte pour l'instant (Mode Invité)",
                            fontSize = 12.sp,
                            color = TextSecondaryMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }

    // Password Reset Dialog
    if (showResetPasswordDialog) {
        var resetEmail by remember { mutableStateOf(loginEmail) }
        var resetError by remember { mutableStateOf<String?>(null) }
        var resetSuccess by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = MedicalTealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Réinitialiser mon mot de passe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Saisissez votre adresse email. Nous vous transmettrons un lien de réinitialisation sécurisé Firebase Auth.",
                        fontSize = 13.sp,
                        color = TextSecondaryMuted
                    )

                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = {
                            resetEmail = it
                            resetError = null
                        },
                        label = { Text("Email associé") },
                        placeholder = { Text("votre.email@domaine.com") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalTealPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    resetError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    resetSuccess?.let {
                        Text(text = it, color = VerifiedBadgeGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                            resetError = "Veuillez renseigner un email valide."
                            return@Button
                        }
                        onPasswordReset(
                            resetEmail,
                            { successMsg ->
                                resetSuccess = successMsg
                            },
                            { errMsg ->
                                resetError = errMsg
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary)
                ) {
                    Text("Envoyer le lien", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetPasswordDialog = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}
