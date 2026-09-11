package com.example.ui.util

import com.example.data.local.InitialData
import com.example.data.model.Medicine
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ScannedPrescriptionItem(
    val medicineName: String,
    val quantity: Int = 1,
    val posology: String = "1 prise selon ordonnance",
    val matchedMedicine: Medicine? = null
) {
    val estimatedUnitPrice: Int
        get() = matchedMedicine?.priceFcfa ?: 2500

    val estimatedTotalPrice: Int
        get() = estimatedUnitPrice * quantity
}

data class ScannedPrescriptionData(
    val doctorName: String,
    val doctorSpecialty: String = "Médecin Praticien",
    val patientName: String,
    val prescriptionDate: String,
    val pharmacyId: String? = null,
    val pharmacyName: String? = null,
    val pharmacyRegion: String? = null,
    val items: List<ScannedPrescriptionItem>,
    val notes: String = "",
    val rawQrContent: String = "",
    val verificationCode: String = "ORD-SN-" + (1000..9999).random(),
    val isBarcodeBox: Boolean = false,
    val barcodeValue: String? = null
) {
    val totalPrescriptionCost: Int
        get() = items.sumOf { it.estimatedTotalPrice }

    val totalItemsCount: Int
        get() = items.sumOf { it.quantity }
}

data class DemoPrescriptionPreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val doctor: String,
    val jsonPayload: String,
    val isBarcode: Boolean = false
)

object PrescriptionQrParser {

    private val currentDateFormatted: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())

    /**
     * Analyse le texte brut d'un QR code d'ordonnance ou d'un Code-barres de boîte de médicament.
     */
    fun parse(rawContent: String): ScannedPrescriptionData {
        val trimmed = rawContent.trim()

        // 1. Détection Code-barres GS1 DataMatrix (boîtes de médicaments pharmaceutiques avec GTIN/CIP)
        val gs1Match = Regex("(?:\\(01\\)|01)0?([0-9]{13})").find(trimmed)
        if (gs1Match != null) {
            val extractedGtin = gs1Match.groupValues[1]
            return parseMedicineBarcode(extractedGtin)
        }

        // 2. Détection Code-barres numérique (EAN-13, EAN-8, UPC, Code 128, Code 39)
        val isNumericBarcode = trimmed.matches(Regex("^[0-9]{8,14}$"))
        if (isNumericBarcode || trimmed.startsWith("CIP-") || trimmed.startsWith("CIP13-") || trimmed.startsWith("ACL-") || trimmed.startsWith("EAN-")) {
            val cleanBarcode = trimmed.removePrefix("CIP-").removePrefix("CIP13-").removePrefix("ACL-").removePrefix("EAN-").trim()
            return parseMedicineBarcode(cleanBarcode)
        }

        // 3. Tenter l'analyse au format JSON (QR code ordonnance numérique)
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            val fromJson = parseJson(trimmed)
            if (fromJson != null) return fromJson
        }

        // 4. Tenter l'analyse de texte structuré ou délimité
        return parseStructuredText(trimmed)
    }

    /**
     * Analyse un code-barres provenant d'une boîte de médicament (EAN-13, CIP, Code 128).
     * Associe en priorité aux médicaments correspondants (ex: Doliprane, Amox, Spasfon)
     * ou au catalogue complet de manière déterministe.
     */
    fun parseMedicineBarcode(barcode: String): ScannedPrescriptionData {
        val medicines = InitialData.medicines

        // Recherche ciblée selon les codes-barres standards pharmaceutiques
        val matchedMed = when (barcode) {
            "3400936087796" -> medicines.find { it.name.contains("Doliprane 1000", ignoreCase = true) }
            "3400936359053" -> medicines.find { it.name.contains("Amoxicilline", ignoreCase = true) }
            "3400936881738" -> medicines.find { it.name.contains("Spasfon", ignoreCase = true) }
            "3400935824552" -> medicines.find { it.name.contains("Biogaran", ignoreCase = true) || it.name.contains("Paracétamol", ignoreCase = true) }
            "3400930024479" -> medicines.find { it.name.contains("Ibuprofène", ignoreCase = true) || it.name.contains("Advil", ignoreCase = true) }
            "3400935201179" -> medicines.find { it.name.contains("Efferalgan", ignoreCase = true) }
            "3400930062402" -> medicines.find { it.name.contains("Ciprofloxacine", ignoreCase = true) }
            "3400931234567" -> medicines.find { it.name.contains("Coartem", ignoreCase = true) }
            else -> {
                // Recherche par nom ou hash
                val hash = kotlin.math.abs(barcode.hashCode())
                medicines[hash % medicines.size]
            }
        } ?: medicines.first()

        val item = ScannedPrescriptionItem(
            medicineName = matchedMed.name,
            quantity = 1,
            posology = matchedMed.posology.ifBlank { "1 prise selon la posologie de la boîte" },
            matchedMedicine = matchedMed
        )

        return ScannedPrescriptionData(
            doctorName = "Scan Code-barres Boîte",
            doctorSpecialty = "Identification Officine Directe",
            patientName = "Détenteur de la boîte",
            prescriptionDate = currentDateFormatted,
            pharmacyId = matchedMed.pharmacyId,
            pharmacyName = InitialData.pharmacies.find { it.id == matchedMed.pharmacyId }?.name ?: "Pharmacie partenaire",
            pharmacyRegion = InitialData.pharmacies.find { it.id == matchedMed.pharmacyId }?.region ?: "Dakar",
            items = listOf(item),
            notes = "Boîte de médicament scannée avec succès. Code-barres EAN : $barcode. Dosage : ${matchedMed.dosageStrength}. Forme : ${matchedMed.dosageForm}.",
            rawQrContent = barcode,
            verificationCode = "EAN-$barcode",
            isBarcodeBox = true,
            barcodeValue = barcode
        )
    }

    private fun parseJson(jsonStr: String): ScannedPrescriptionData? {
        return try {
            val obj = JSONObject(jsonStr)
            val doctor = obj.optString("doctor", obj.optString("doctorName", "Dr. Praticien Hospitalier"))
            val specialty = obj.optString("specialty", "Médecine Générale • Ordre des Médecins du Sénégal")
            val patient = obj.optString("patient", obj.optString("patientName", "Patient(e)"))
            val date = obj.optString("date", obj.optString("prescriptionDate", currentDateFormatted))
            val pharmacyId = obj.optString("pharmacyId").takeIf { it.isNotBlank() }
            val pharmacyName = obj.optString("pharmacyName").takeIf { it.isNotBlank() }
            val pharmacyRegion = obj.optString("pharmacyRegion", "Dakar").takeIf { it.isNotBlank() }
            val notes = obj.optString("notes", obj.optString("posology", "Traitement délivré sur ordonnance médicale certifiée."))
            val code = obj.optString("code", "ORD-SN-${(1000..9999).random()}")

            val rawItems = mutableListOf<ScannedPrescriptionItem>()

            // Les médicaments peuvent être dans "medicines", "meds" ou "items"
            val array = obj.optJSONArray("medicines")
                ?: obj.optJSONArray("meds")
                ?: obj.optJSONArray("items")

            if (array != null) {
                for (i in 0 until array.length()) {
                    val element = array.get(i)
                    if (element is JSONObject) {
                        val name = element.optString("name", element.optString("medicine", "Médicament"))
                        val qty = element.optInt("qty", element.optInt("quantity", 1))
                        val posology = element.optString("posology", "1 prise selon ordonnance")
                        val matched = matchMedicine(name)
                        rawItems.add(
                            ScannedPrescriptionItem(
                                medicineName = matched?.name ?: name,
                                quantity = if (qty <= 0) 1 else qty,
                                posology = posology,
                                matchedMedicine = matched
                            )
                        )
                    } else if (element is String && element.isNotBlank()) {
                        val matched = matchMedicine(element)
                        rawItems.add(
                            ScannedPrescriptionItem(
                                medicineName = matched?.name ?: element,
                                quantity = 1,
                                posology = "1 prise selon ordonnance",
                                matchedMedicine = matched
                            )
                        )
                    }
                }
            } else {
                // Peut-être une chaîne séparée par des virgules dans "medicines"
                val medString = obj.optString("medicines", obj.optString("meds", ""))
                if (medString.isNotBlank()) {
                    medString.split(",", ";", "\n").forEach { str ->
                        val clean = str.trim()
                        if (clean.isNotBlank()) {
                            val matched = matchMedicine(clean)
                            rawItems.add(
                                ScannedPrescriptionItem(
                                    medicineName = matched?.name ?: clean,
                                    quantity = 1,
                                    posology = "1 prise selon ordonnance",
                                    matchedMedicine = matched
                                )
                            )
                        }
                    }
                }
            }

            // Si aucun médicament explicite trouvé, chercher dans le texte brut du JSON
            if (rawItems.isEmpty()) {
                val matchedList = findMedicinesInCatalog(jsonStr)
                if (matchedList.isNotEmpty()) {
                    rawItems.addAll(matchedList.map { ScannedPrescriptionItem(it.name, 1, "1 prise selon ordonnance", it) })
                }
            }

            if (rawItems.isEmpty()) {
                // Fallback générique pour ne pas bloquer l'utilisateur
                val defaultMed = InitialData.medicines.firstOrNull()
                rawItems.add(
                    ScannedPrescriptionItem(
                        medicineName = defaultMed?.name ?: "Paracétamol 1000 mg",
                        quantity = 1,
                        posology = "1 comprimé en cas de besoin",
                        matchedMedicine = defaultMed
                    )
                )
            }

            ScannedPrescriptionData(
                doctorName = doctor,
                doctorSpecialty = specialty,
                patientName = patient,
                prescriptionDate = date,
                pharmacyId = pharmacyId,
                pharmacyName = pharmacyName,
                pharmacyRegion = pharmacyRegion,
                items = rawItems,
                notes = notes,
                rawQrContent = jsonStr,
                verificationCode = code
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseStructuredText(text: String): ScannedPrescriptionData {
        var doctor = "Dr. Médecin Prescripteur"
        var patient = "Patient(e)"
        var date = currentDateFormatted
        var notes = "Ordonnance scannée via QR Code caméra."
        val items = mutableListOf<ScannedPrescriptionItem>()

        val lines = text.lines()
        for (line in lines) {
            val lower = line.lowercase()
            when {
                lower.startsWith("dr:") || lower.startsWith("docteur:") || lower.startsWith("medecin:") || lower.startsWith("médecin:") -> {
                    doctor = line.substringAfter(":").trim()
                }
                lower.startsWith("patient:") || lower.startsWith("nom:") -> {
                    patient = line.substringAfter(":").trim()
                }
                lower.startsWith("date:") -> {
                    date = line.substringAfter(":").trim()
                }
                lower.startsWith("notes:") || lower.startsWith("posologie:") || lower.startsWith("avis:") -> {
                    notes = line.substringAfter(":").trim()
                }
            }
        }

        // Chercher tous les médicaments du catalogue mentionnés dans le texte
        val catalogMatches = findMedicinesInCatalog(text)
        if (catalogMatches.isNotEmpty()) {
            for (med in catalogMatches) {
                items.add(
                    ScannedPrescriptionItem(
                        medicineName = med.name,
                        quantity = 1,
                        posology = "1 prise selon prescription médicale",
                        matchedMedicine = med
                    )
                )
            }
        } else {
            // Découpage par tirets ou lignes ou virgules
            val candidateLines = lines.filter {
                it.trim().startsWith("-") || it.trim().startsWith("•") || it.contains("x", ignoreCase = true) || it.contains("mg", ignoreCase = true)
            }

            if (candidateLines.isNotEmpty()) {
                for (cand in candidateLines) {
                    val clean = cand.trim().removePrefix("-").removePrefix("•").trim()
                    val qty = extractQuantity(clean)
                    val medName = clean.replace(Regex("x\\s*\\d+", RegexOption.IGNORE_CASE), "").trim()
                    val matched = matchMedicine(medName)
                    items.add(
                        ScannedPrescriptionItem(
                            medicineName = matched?.name ?: medName,
                            quantity = qty,
                            posology = "Selon avis médical",
                            matchedMedicine = matched
                        )
                    )
                }
            } else {
                // Fallback avec recherche plus souple ou premier médicament
                val words = text.split(",", ";", "|")
                for (w in words) {
                    val trimmedWord = w.trim()
                    if (trimmedWord.length > 3 && !trimmedWord.startsWith("http")) {
                        val matched = matchMedicine(trimmedWord)
                        if (matched != null && items.none { it.medicineName == matched.name }) {
                            items.add(
                                ScannedPrescriptionItem(
                                    medicineName = matched.name,
                                    quantity = 1,
                                    posology = "Selon ordonnance",
                                    matchedMedicine = matched
                                )
                            )
                        }
                    }
                }
            }
        }

        if (items.isEmpty()) {
            val fallbackMed = InitialData.medicines.find { it.requiresPrescription } ?: InitialData.medicines.first()
            items.add(
                ScannedPrescriptionItem(
                    medicineName = fallbackMed.name,
                    quantity = 1,
                    posology = "1 prise selon ordonnance médicale",
                    matchedMedicine = fallbackMed
                )
            )
        }

        return ScannedPrescriptionData(
            doctorName = doctor,
            doctorSpecialty = "Médecin Généraliste • Dakar",
            patientName = patient,
            prescriptionDate = date,
            pharmacyId = null,
            pharmacyName = null,
            pharmacyRegion = "Dakar",
            items = items,
            notes = notes,
            rawQrContent = text,
            verificationCode = "ORD-SN-${(1000..9999).random()}"
        )
    }

    private fun extractQuantity(str: String): Int {
        val match = Regex("x\\s*(\\d+)", RegexOption.IGNORE_CASE).find(str)
        return match?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 1
    }

    /**
     * Recherche le médicament le plus proche dans le catalogue.
     */
    fun matchMedicine(query: String): Medicine? {
        val clean = query.trim().lowercase()
        // 1. Correspondance exacte
        InitialData.medicines.find { it.name.lowercase() == clean }?.let { return it }

        // 2. Contient le nom
        InitialData.medicines.find {
            clean.contains(it.name.lowercase()) || it.name.lowercase().contains(clean)
        }?.let { return it }

        // 3. Correspondance par DCI (principe actif)
        InitialData.medicines.find {
            it.dci.isNotBlank() && (clean.contains(it.dci.lowercase()) || it.dci.lowercase().contains(clean))
        }?.let { return it }

        // 4. Correspondance sur le premier mot significatif (ex: Doliprane, Amoxicilline, Spasfon)
        val firstWord = clean.split(" ", "-").firstOrNull()?.trim() ?: ""
        if (firstWord.length >= 4) {
            InitialData.medicines.find {
                it.name.lowercase().startsWith(firstWord)
            }?.let { return it }
        }

        return null
    }

    private fun findMedicinesInCatalog(text: String): List<Medicine> {
        val lowerText = text.lowercase()
        val found = mutableListOf<Medicine>()
        for (med in InitialData.medicines) {
            val medNameLower = med.name.lowercase()
            val firstToken = medNameLower.split(" ").firstOrNull() ?: ""
            if (firstToken.length >= 5 && lowerText.contains(firstToken)) {
                if (found.none { it.id == med.id }) {
                    found.add(med)
                }
            } else if (lowerText.contains(medNameLower)) {
                if (found.none { it.id == med.id }) {
                    found.add(med)
                }
            }
        }
        return found
    }

    /**
     * Préréglages de test réalistes avec contextes médicaux au Sénégal.
     */
    val demoPresets: List<DemoPrescriptionPreset> by lazy {
        listOf(
            DemoPrescriptionPreset(
                id = "preset_fever",
                title = "Fièvre & Maux de tête",
                subtitle = "Doliprane 1000 mg (x2) • Vitamine C 1000 mg • Spasfon",
                doctor = "Dr. Aminata Diallo (Hôpital Principal de Dakar)",
                jsonPayload = JSONObject().apply {
                    put("type", "prescription")
                    put("code", "ORD-SN-7842")
                    put("doctor", "Dr. Aminata Diallo")
                    put("specialty", "Médecine Générale • Hôpital Principal de Dakar")
                    put("patient", "Mamadou Dramé")
                    put("date", currentDateFormatted)
                    put("pharmacyName", "Grande Pharmacie Dakaroise")
                    put("pharmacyRegion", "Dakar")
                    put("notes", "1 comprimé toutes les 8h si fièvre. Bien s'hydrater.")
                    put("medicines", JSONArray().apply {
                        put(JSONObject().apply {
                            put("name", "Doliprane 1000 mg")
                            put("qty", 2)
                            put("posology", "1 comprimé toutes les 8 heures si douleur/fièvre")
                        })
                        put(JSONObject().apply {
                            put("name", "Vitamine C 1000 mg")
                            put("qty", 1)
                            put("posology", "1 comprimé effervescent le matin dans un verre d'eau")
                        })
                        put(JSONObject().apply {
                            put("name", "Spasfon 80 mg")
                            put("qty", 1)
                            put("posology", "2 comprimés par jour en cas de crampes digestives")
                        })
                    })
                }.toString()
            ),
            DemoPrescriptionPreset(
                id = "preset_infection",
                title = "Infection respiratoire / Bronchite",
                subtitle = "Amoxicilline 500 mg (x2) • Sirop Toplexil",
                doctor = "Dr. Cheikh Anta Ndiaye (Polyclinique de la Madeleine)",
                jsonPayload = JSONObject().apply {
                    put("type", "prescription")
                    put("code", "ORD-SN-9021")
                    put("doctor", "Dr. Cheikh Anta Ndiaye")
                    put("specialty", "Pneumologie • Polyclinique de la Madeleine")
                    put("patient", "Aissatou Ba")
                    put("date", currentDateFormatted)
                    put("pharmacyName", "Pharmacie Guigon")
                    put("pharmacyRegion", "Dakar")
                    put("notes", "Traitement antibiotique de 6 jours à terminer impérativement.")
                    put("medicines", JSONArray().apply {
                        put(JSONObject().apply {
                            put("name", "Amoxicilline 500 mg")
                            put("qty", 2)
                            put("posology", "1 gélule 3 fois par jour au milieu des repas pendant 6 jours")
                        })
                        put(JSONObject().apply {
                            put("name", "Sirop Toplexil")
                            put("qty", 1)
                            put("posology", "1 cuillère mesure le soir au coucher en cas de toux sèche")
                        })
                    })
                }.toString()
            ),
            DemoPrescriptionPreset(
                id = "preset_palu",
                title = "Paludisme & Traitement d'urgence",
                subtitle = "Coartem 20/120 mg • Paracétamol Biogaran 500 mg",
                doctor = "Dr. Ousmane Kane (Clinique de la Paix - Saint-Louis)",
                jsonPayload = JSONObject().apply {
                    put("type", "prescription")
                    put("code", "ORD-SN-4139")
                    put("doctor", "Dr. Ousmane Kane")
                    put("specialty", "Infectiologie • Centre de Santé")
                    put("patient", "Ibrahima Sow")
                    put("date", currentDateFormatted)
                    put("pharmacyName", "Pharmacie de la Paix")
                    put("pharmacyRegion", "Saint-Louis")
                    put("notes", "Test TDR Paludisme positif. Respecter rigoureusement les 6 prises.")
                    put("medicines", JSONArray().apply {
                        put(JSONObject().apply {
                            put("name", "Coartem 20/120 mg")
                            put("qty", 1)
                            put("posology", "4 comprimés à H0, H8, puis 4 cp matin et soir pendant 2 jours")
                        })
                        put(JSONObject().apply {
                            put("name", "Paracétamol Biogaran 500 mg")
                            put("qty", 1)
                            put("posology", "1 gélule toutes les 6 heures pour faire tomber la température")
                        })
                    })
                }.toString()
            ),
            DemoPrescriptionPreset(
                id = "preset_barcode_doliprane",
                title = "Boîte : Doliprane 1000 mg",
                subtitle = "EAN-13 : 3400936087796 • Sanofi (Douleurs/Fièvre)",
                doctor = "Boîte Médicament (Sanofi)",
                jsonPayload = "3400936087796",
                isBarcode = true
            ),
            DemoPrescriptionPreset(
                id = "preset_barcode_amox",
                title = "Boîte : Amoxicilline 500 mg",
                subtitle = "EAN-13 : 3400936359053 • Biogaran (Antibiotique)",
                doctor = "Boîte Médicament (Biogaran)",
                jsonPayload = "3400936359053",
                isBarcode = true
            ),
            DemoPrescriptionPreset(
                id = "preset_barcode_spasfon",
                title = "Boîte : Spasfon 80 mg",
                subtitle = "EAN-13 : 3400936881738 • Teva (Antispasmodique)",
                doctor = "Boîte Médicament (Teva)",
                jsonPayload = "3400936881738",
                isBarcode = true
            ),
            DemoPrescriptionPreset(
                id = "preset_barcode_ibuprofene",
                title = "Boîte : Ibuprofène 400 mg",
                subtitle = "EAN-13 : 3400930024479 • Sandoz (Anti-inflammatoire)",
                doctor = "Boîte Médicament (Sandoz)",
                jsonPayload = "3400930024479",
                isBarcode = true
            )
        )
    }
}
