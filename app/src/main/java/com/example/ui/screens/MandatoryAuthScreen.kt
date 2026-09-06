package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BiometricPromptDialog
import com.example.ui.theme.BorderSoft
import com.example.ui.theme.DutyPharmacyOrange
import com.example.ui.theme.MedicalEmeraldAccent
import com.example.ui.theme.MedicalTealDark
import com.example.ui.theme.MedicalTealPrimary
import com.example.ui.theme.SafeBlueSecondary
import com.example.ui.theme.VerifiedBadgeGreen
import com.example.ui.viewmodel.PharmaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandatoryAuthScreen(
    viewModel: PharmaViewModel,
    onAuthenticated: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Connexion, 1: Inscription Santé
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Login fields
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Signup fields (including emergency contact and health profile)
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }
    var knownAllergies by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var bloodDropdownExpanded by remember { mutableStateOf(false) }

    // Biometric prompt dialog state
    var showBiometricDialog by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "Non déterminé")

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mandatory_auth_screen"),
        color = Color(0xFF0C1715) // Deep medical slate-emerald
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header & Shield
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(MedicalTealPrimary, Color(0xFF003824))
                        )
                    )
                    .border(2.dp, MedicalEmeraldAccent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = "Sécurité Santé",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PharmaDirect Sénégal",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.3).sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Espace Santé Sécurisé • Authentification Obligatoire",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Security Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF132B25))
                    .border(1.dp, BorderSoft, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MedicalEmeraldAccent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Chiffrement AES-256 GCM • Données Protégées TEE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Selector (Connexion vs Inscription)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF142622),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MedicalEmeraldAccent,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSoft, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        errorMessage = null
                    },
                    text = {
                        Text(
                            text = "Connexion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        errorMessage = null
                    },
                    text = {
                        Text(
                            text = "Créer mon Compte",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error display if any
            errorMessage?.let { msg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF451A1A))
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Quick 1-Click Demo Login Banner (For Instant Access & Evaluation)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isLoading = true
                        errorMessage = null
                        viewModel.loginAsDemoUser {
                            isLoading = false
                            onAuthenticated()
                        }
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF162D27)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalEmeraldAccent.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MedicalTealPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Accès Rapide Démo Médicale (1-Clic)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Compte Démo Patient • Dossier médical & Groupe O+ inclus",
                            fontSize = 11.5.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Biometric Quick Unlock Button
            OutlinedButton(
                onClick = { showBiometricDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_biometric_login"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MedicalEmeraldAccent),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MedicalEmeraldAccent,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Identification Empreinte / Code PIN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = BorderSoft)

            Spacer(modifier = Modifier.height(20.dp))

            // TAB 0: CONNEXION CLASSIQUE
            if (selectedTab == 0) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Identifiant ou Email",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        placeholder = { Text("Ex: patient@pharmaexpress.sn ou téléphone", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MedicalEmeraldAccent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = MedicalEmeraldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Mot de passe",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        placeholder = { Text("Votre mot de passe sécurisé", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalEmeraldAccent)
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_login_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = MedicalEmeraldAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            if (loginIdentifier.isBlank()) {
                                errorMessage = "Veuillez renseigner votre email ou identifiant."
                                return@Button
                            }
                            if (loginPassword.isBlank()) {
                                errorMessage = "Veuillez renseigner votre mot de passe."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            viewModel.signInWithEmail(
                                email = loginIdentifier.trim(),
                                password = loginPassword.trim(),
                                onSuccess = {
                                    isLoading = false
                                    onAuthenticated()
                                },
                                onError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_login"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "S'identifier et Accéder",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // TAB 1: CRÉATION DE COMPTE AVEC CONTACT D'URGENCE & FICHE SANTÉ
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "1. Informations Personnelles",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalEmeraldAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nom et Prénom", color = Color.White) },
                        placeholder = { Text("Ex: Nom et Prénom", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalEmeraldAccent) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_fullname"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Adresse Email", color = Color.White) },
                        placeholder = { Text("Ex: patient@pharmaexpress.sn", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalEmeraldAccent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_email"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Téléphone Principal (Wave / Orange Money)", color = Color.White) },
                        placeholder = { Text("Numéro mobile", color = Color.White.copy(alpha = 0.5f)) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalEmeraldAccent) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. CONTACT D'URGENCE VITAL (OBLIGATOIRE)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF172924)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DutyPharmacyOrange.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = DutyPharmacyOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Contact d'Urgence Vital (ICE)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cette personne sera affichée directement dans votre profil et alertée en cas de nécessité médicale.",
                                fontSize = 11.5.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = emergencyContactName,
                                onValueChange = { emergencyContactName = it },
                                label = { Text("Nom de la personne d'urgence", color = Color.White) },
                                placeholder = { Text("Ex: Awa Dramé (Épouse / SOS)", color = Color.White.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth().testTag("input_reg_emergency_name"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DutyPharmacyOrange,
                                    unfocusedBorderColor = BorderSoft,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = emergencyContactPhone,
                                onValueChange = { emergencyContactPhone = it },
                                label = { Text("Numéro de téléphone d'urgence", color = Color.White) },
                                placeholder = { Text("Numéro de contact d'urgence", color = Color.White.copy(alpha = 0.5f)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier.fillMaxWidth().testTag("input_reg_emergency_phone"),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DutyPharmacyOrange,
                                    unfocusedBorderColor = BorderSoft,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. GROUPE SANGUIN & ALLERGIES
                    Text(
                        text = "2. Fiche Santé & Médicale",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalEmeraldAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = bloodDropdownExpanded,
                        onExpandedChange = { bloodDropdownExpanded = !bloodDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Groupe Sanguin", color = Color.White) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_reg_blood_group"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MedicalEmeraldAccent,
                                unfocusedBorderColor = BorderSoft,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = bloodDropdownExpanded,
                            onDismissRequest = { bloodDropdownExpanded = false },
                            modifier = Modifier.background(Color(0xFF132420))
                        ) {
                            bloodGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group, color = Color.White, fontWeight = FontWeight.Bold) },
                                    onClick = {
                                        bloodGroup = group
                                        bloodDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = knownAllergies,
                        onValueChange = { knownAllergies = it },
                        label = { Text("Allergies connues (ex: Pénicilline...)", color = Color.White) },
                        placeholder = { Text("Ex: Aucune allergie médicamenteuse", color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_allergies"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = registerPassword,
                        onValueChange = { registerPassword = it },
                        label = { Text("Créer un Mot de passe ou Code PIN", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("input_reg_password"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedicalEmeraldAccent,
                            unfocusedBorderColor = BorderSoft,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                errorMessage = "Veuillez renseigner votre nom et prénom."
                                return@Button
                            }
                            if (phoneNumber.isBlank()) {
                                errorMessage = "Veuillez renseigner votre numéro de téléphone principal."
                                return@Button
                            }
                            if (emergencyContactName.isBlank()) {
                                errorMessage = "Veuillez renseigner le contact d'urgence."
                                return@Button
                            }
                            if (emergencyContactPhone.isBlank()) {
                                errorMessage = "Veuillez renseigner le téléphone du contact d'urgence."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            viewModel.registerOrLoginWithEmergencyContact(
                                fullName = fullName,
                                email = email,
                                phoneNumber = phoneNumber,
                                emergencyContactName = emergencyContactName,
                                emergencyContactPhone = emergencyContactPhone,
                                bloodGroup = bloodGroup,
                                knownAllergies = knownAllergies,
                                passwordOrPin = registerPassword,
                                onSuccess = {
                                    isLoading = false
                                    onAuthenticated()
                                },
                                onError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_submit_signup"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalTealPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text(
                                text = "Valider mon Dossier & Entrer",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Biometric & PIN prompt dialog
    if (showBiometricDialog) {
        BiometricPromptDialog(
            biometricAuthManager = viewModel.biometricAuthManager,
            title = "Identification Biométrique",
            reason = "Placez votre empreinte ou saisissez votre code PIN secret",
            onSuccess = {
                showBiometricDialog = false
                viewModel.loginAsDemoUser {
                    onAuthenticated()
                }
            },
            onDismiss = {
                showBiometricDialog = false
            }
        )
    }
}
