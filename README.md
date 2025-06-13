# EMFAD App

## 📱 Überblick
Die EMFAD App ist eine fortschrittliche Anwendung für die Analyse und Visualisierung von elektromagnetischen Messungen. Sie unterstützt verschiedene Messmodi und bietet umfangreiche Analysefunktionen.

## 🔬 Messmodi

### 1. B-A Vertikal
- **Beschreibung**: Vertikale Messung mit B-A-Konfiguration
- **Anwendung**: Tiefenmessung, vertikale Strukturen
- **Kalibrierung**: Automatische Kalibrierung für vertikale Messungen

### 2. A-B Horizontal
- **Beschreibung**: Horizontale Messung mit A-B-Konfiguration
- **Anwendung**: Horizontale Strukturen, Flächenmessung
- **Kalibrierung**: Automatische Kalibrierung für horizontale Messungen

### 3. Antenne A
- **Beschreibung**: Einzelantennen-Messung
- **Anwendung**: Schnelle Vorortmessung, grobe Orientierung
- **Kalibrierung**: Vereinfachte Kalibrierung für Einzelantennen

### 4. Tiefe Pro
- **Beschreibung**: Professionelle Tiefenbestimmung
- **Anwendung**: Präzise Tiefenmessung, Schichtanalyse
- **Kalibrierung**: Hz/Hx-Messmethode mit EMTOMO LDA Software

## 🔍 Materialanalyse

### 💎 Kristalline Materialien & Edelsteine

#### Kristalline Nichtleiter
Diese Materialien sind nicht leitfähig, erzeugen jedoch messbare Anomalien durch:
- Dichtekontrast
- Kristallstruktur (Anisotropie)
- Resonanzphänomene in elektromagnetischen Feldern

| Material       | Dichte (g/cm³) | Leitfähigkeit (S/m) | Permittivität (εr) | Typische Umgebung           |
| -------------- | -------------- | ------------------- | ------------------ | --------------------------- |
| Rubin (Korund) | 3.9–4.1        | < 10⁻¹⁰             | 9.3–10             | Pegmatite, Quarzgänge       |
| Smaragd        | 2.7–2.8        | < 10⁻¹⁰             | 6–8                | Beryllhaltige Adern         |
| Diamant        | 3.5–3.6        | < 10⁻¹³             | 5.5–7.0            | Kimberlite, Vulkanröhren    |
| Turmalin       | 3.0–3.3        | < 10⁻⁹              | 12–15              | Quarz-Pegmatite             |
| Quarz          | 2.6–2.7        | ≈ 0                 | 4–5                | Kristalline Gesteine, Gänge |

#### Erkennungsansatz
- **Form**: Kompakt, oval/rund, hochsymmetrisch
- **Tiefe**: Häufig > 1 m
- **Leitfähigkeit**: ≈ 0 S/m
- **Begleitmaterialien**: Pyrit, Quarz, Feldspat
- **Signalverhalten**: Schwache, aber scharf abgegrenzte Impedanzänderung

### Natürliche Adern vs. Künstliche Strukturen

#### Natürliche Adern
- **Charakteristika**:
  - Hohe Leitfähigkeit (> 10⁴ S/m)
  - Langgestreckt (Aspektverhältnis > 10)
  - Unsymmetrisch (Symmetrie < 0.3)
  - Tief (> 1.5m)
  - Typische Formen: Ader, Spalt
- **Erkennbare Adern**:
  - Goldader
  - Silberader
  - Kupferader
  - Quarzader
  - Pyritader
  - Magnetitader

#### Künstliche Strukturen
- **Charakteristika**:
  - Extrem niedrige oder hohe Leitfähigkeit
  - Kompakt (Aspektverhältnis < 3)
  - Symmetrisch (Symmetrie > 0.7)
  - Flacher (< 10m)
  - Typische Formen: Hohlraum, Schicht
- **Erkennbare Strukturen**:
  - Tunnel
  - Kammer
  - Brunnen
  - Fundament
  - Mauer
  - Drainage

### Vergleichstabelle: Echte Ader vs. Gegrabener Schacht

| Kriterium            | Natürliche Ader             | Menschengemachte Struktur         |
| -------------------- | --------------------------- | --------------------------------- |
| Leitfähigkeitsprofil | Kontinuierlich mit Gradient | Sprunghaft, evtl. scharfe Grenze  |
| Form                 | Unregelmäßig, langgezogen   | Rechteckig, symmetrisch           |
| Tiefe                | Tiefer (>1.5 m)             | Häufig flacher oder schichtweise  |
| Symmetrie            | Gering (<0.3)               | Hoch (>0.7)                       |
| Magnetfeldgradient   | Unruhig, gestreckt          | Klar begrenzt, gleichmäßig        |
| Schichtung           | Natürlich mit Übergängen    | Schichtstörung, oft Hohlraum oben |

### Erkennungskriterien

#### 1. Geometrische Eigenschaften
- **Aspektverhältnis**:
  - Natürlich: > 10 (langgestreckt)
  - Künstlich: < 3 (kompakt)
- **Symmetrie**:
  - Natürlich: < 0.3 (unsymmetrisch)
  - Künstlich: > 0.7 (symmetrisch)

#### 2. Physikalische Eigenschaften
- **Leitfähigkeit**:
  - Natürlich: > 10⁴ S/m
  - Künstlich: < 10⁻⁹ S/m oder > 10⁶ S/m
- **Permittivität**:
  - Natürlich: Variabel, typisch 1-10
  - Künstlich: Extrem (Luft ≈ 1, Metall ≈ 1)
- **Permeabilität**:
  - Natürlich: Nahe 1
  - Künstlich: Variabel

#### 3. Magnetfeld-Gradienten
- **Natürlich**:
  - Verzerrt, unsymmetrisch
  - Schwache Störungen
- **Künstlich**:
  - Symmetrisch
  - Klare Abgrenzungen

#### 4. Tiefe und Form
- **Natürlich**:
  - Tief > 1.5m
  - Unregelmäßige Form
- **Künstlich**:
  - Flach < 10m
  - Regelmäßige Form

### Zuverlässigkeitsberechnung

#### Zuverlässigkeits-Skala
- **Score 0-1**: Gewichtete Summe aller Kriterien
- **Klassifikation**:
  - > 0.75: Hohe Wahrscheinlichkeit (🟢)
  - 0.5-0.75: Mittlere Wahrscheinlichkeit (🟡)
  - < 0.5: Geringe Wahrscheinlichkeit (🔴)

#### Für Adern
1. Leitfähigkeit (20%)
2. Aspektverhältnis (20%)
3. Symmetrie (20%)
4. Tiefe (20%)
5. Anomalieform (20%)

#### Für Strukturen
1. Leitfähigkeit (20%)
2. Aspektverhältnis (20%)
3. Symmetrie (20%)
4. Tiefe (20%)
5. Anomalieform (20%)

### Schichtanalyse

#### Typisches Schichtprofil
```plaintext
1. Humus/Erde (0–20 cm)
2. Lehm (20–70 cm)
3. Ton (70–150 cm)
4. Anomalie (ab 150 cm, Tiefe: 180 cm)
   → Typ: Hohlraum mit leitfähigem Objekt
   → Form: Rechteck
```

#### Schichtparameter
- Material
- Tiefe
- Dicke
- Anomalieposition
- Übergangstyp

## 🛠️ Technische Implementierung

### Architektur
Die App folgt der **MVVM (Model-View-ViewModel)** Architektur, einem Architekturmuster, das die Entwicklung von robusten und wartbaren Anwendungen auf Android erleichtert. MVVM fördert die Trennung von Belangen:

- **Model**: Repräsentiert die Daten und die Geschäftslogik. In dieser App beinhalten die Models die Datenstrukturen für Messungen, Profile, Sitzungen und Einstellungen sowie Logik für Datenverarbeitung.
- **View**: Die Benutzeroberfläche (UI). In dieser App wird die UI mit **Jetpack Compose** erstellt. Die Views sind passiv und zeigen nur die Daten an, die ihnen vom ViewModel bereitgestellt werden, und leiten Benutzerinteraktionen an das ViewModel weiter.
- **ViewModel**: Hält UI-Zustandsdaten bereit und führt Logik aus, die durch Benutzerinteraktionen oder andere Ereignisse ausgelöst wird. Das ViewModel interagiert mit den Models und Services, um Daten abzurufen oder zu verarbeiten, und stellt die aufbereiteten Daten für die Views bereit.

Zusätzlich zu den Kern-MVVM-Komponenten verwendet die App **Services** zur Kapselung spezifischer Funktionalitäten, die über die reine UI-Logik hinausgehen und oft Hintergrundoperationen oder die Interaktion mit dem Android-System beinhalten.

### Hauptkomponenten

1.  **Models (`com.emfad.app.Models.*`)**: Datentypen (z.B. `MeasurementData`, `MeasurementSession`, `MeasurementSettings`) und zugehörige Logik.
2.  **Services (`com.emfad.app.Services.*`)**:
    -   `BluetoothService`: Verwaltet die Bluetooth (BLE) Verbindung und den Datenaustausch mit dem EMFAD-Gerät.
    -   `LocationService`: Stellt GPS-Standortdaten bereit.
    -   `MeasurementService`: Verwaltet den Lebenszyklus von Messsitzungen und -profilen, fügt Messdaten hinzu und führt Datenverarbeitungsfunktionen (Filterung, Statistik) aus.
3.  **ViewModels (`com.emfad.app.ViewModels.*`)**:
    -   `MainViewModel`: Das zentrale ViewModel, das den Zustand der Haupt-UI verwaltet, Benutzerinteraktionen verarbeitet und mit den Services interagiert.
4.  **Views (`com.emfad.app.Views.*`)**: Jetpack Compose Composable Functions, die die Benutzeroberfläche darstellen (z.B. `MainScreen`, `SettingsDialog`). Sie beobachten die Zustandsdaten des ViewModels und aktualisieren sich entsprechend.

Diese Struktur gewährleistet eine klare Trennung der Zuständigkeiten und erleichtert die Testbarkeit, Wartung und Erweiterung der App.

### Physikalische Modelle
- Komplexe Impedanz
- Magnetfeld-Gradienten
- Schichtanalyse
- Anomalieerkennung

## 📊 Benutzeroberfläche

### Hauptansicht
- Messung
- Kalibrierung
- Materialanalyse
- 3D-Visualisierung

### Analyseansicht
- Materialtyp
- Tiefe
- Größe
- Zuverlässigkeit
- Schichtanalyse
- Ader/Struktur-Erkennung

## 🔄 Kalibrierung

### Automatische Kalibrierung
- B-A Vertikal
- A-B Horizontal
- Antenne A
- Tiefe Pro

### Manuelle Kalibrierung
- Kalibrierungspunkte hinzufügen
- Kalibrierungsfaktoren anpassen
- Qualität überwachen

## 📈 Messung

### Durchführung
1. Messmodus wählen
2. Kalibrierung durchführen
3. Messung starten
4. Ergebnisse analysieren

### Analyse
- Materialerkennung
- Tiefenbestimmung
- Schichtanalyse
- Ader/Struktur-Erkennung

## 🚀 Entwicklung

### Abhängigkeiten
- Kotlin 1.8.0
- Jetpack Compose 1.4.0
- Material 3
- Kotlin Coroutines
- Kotlin Flow

### Projektstruktur
```
app/
├── models/
│   ├── MeasurementMode.kt
│   ├── MaterialProperties.kt
│   └── MaterialPhysicsAnalyzer.kt
├── views/
│   ├── MainView.kt
│   └── MaterialCalibrationView.kt
└── viewmodels/
    └── MainViewModel.kt
```

## 🔜 Geplante Funktionen
1. Persistente Speicherung von Kalibrierungsdaten
2. Export/Import von Kalibrierungsprofilen
3. Erweiterte 3D-Visualisierung
4. KI-gestützte Materialerkennung
5. Verbesserte Ader/Struktur-Erkennung
6. Integration mit GPS-Daten
7. Offline-Modus
8. Mehrsprachige Unterstützung

## 📱 Hardware-Spezifikationen

### EMFAD UG DS WL Gerät
| Merkmal            | Wert                  |
| ------------------ | --------------------- |
| Frequenzbereich    | 100 Hz – 20 kHz       |
| Antennentyp        | Ferrit, Koaxial       |
| Datenschnittstelle | BLE / USB             |
| Abtastrate         | 200 Samples/s         |
| Stromversorgung    | 3.7V Li-Ion Akku      |
| Software           | EMTOMO LDA, EMFAD App |

## 📚 Beispiel-Erkennungsfälle

### Goldader vs. Tunnel
```plaintext
Messung:
- Position: 48.1234°N, 11.5678°E
- Tiefe: 2.5m
- Leitfähigkeit: 4.1e7 S/m
- Form: Länglich, unsymmetrisch
- Aspektverhältnis: 15.3

Analyse:
- Typ: Natürliche Goldader
- Zuverlässigkeit: 0.92 (🟢)
- Begleitmaterialien: Quarz, Pyrit
- Schichtung: Natürlich mit Übergängen
```

### Smaragd in Quarzader
```plaintext
Messung:
- Position: 48.2345°N, 11.6789°E
- Tiefe: 1.8m
- Leitfähigkeit: < 10⁻¹⁰ S/m
- Dichte: 2.75 g/cm³
- Permittivität: 7.2

Analyse:
- Typ: Smaragd in Quarzader
- Zuverlässigkeit: 0.85 (🟢)
- Begleitmaterialien: Quarz, Beryll
- Kristallstruktur: Monoklin
```

## 🔬 Physikalische Modelle & Algorithmen

### 1. Kristall-Erkennungsmodus (Low-Signal-Filtering)

#### Ziel
Erkennung schwacher Impedanzanomalien typischer kristalliner Materialien (z.B. Rubin, Quarz, Smaragd).

#### Physikalisches Modell
Kristalle sind **nichtleitend**, erzeugen aber durch ihre **geordnete Struktur** eine messbare Änderung der **lokalen Permittivität** und des **elektrischen Feldgradienten**.

#### Mathematischer Ansatz

##### Komplexe Permittivität:
$$
\varepsilon^* = \varepsilon' - j \cdot \varepsilon''
$$
- $\varepsilon'$: reelle Permittivität (Feldspeicherung)
- $\varepsilon''$: Verlustanteil (Verlustfaktor)

##### Detektion durch Impedanzänderung:
$$
\Delta Z(f) = Z_0 \cdot \left( \sqrt{\frac{\mu \cdot \varepsilon^*_{\text{Lokal}}}{\mu_0 \cdot \varepsilon_0}} - 1 \right)
$$

##### Signalfilterung:
- Adaptive Schwellenwertfunktion:
$$
\Delta Z_{\text{min}} = \alpha \cdot \sigma_{\text{Umgebung}}, \quad \alpha \in [1.5, 2.5]
$$
- Medianfilter oder Butterworth-Filter 2. Ordnung

### 2. Differenzanalyse mehrerer Messpunkte (Clustervergleich)

#### Ziel
Vergleich mehrerer Messpunkte zur Erkennung von **räumlicher Konsistenz** und **Clusterbildung**.

#### Mathematischer Ansatz

##### Feature-Vektor je Punkt:
$$
\mathbf{x}_i = \begin{bmatrix}
Z_i, \ \sigma_i, \ \varepsilon_i, \ \mu_i, \ d_i
\end{bmatrix}
$$

##### Distanzmatrix:
$$
D_{ij} = \| \mathbf{x}_i - \mathbf{x}_j \|
$$

##### Clustering:
$$
\text{Cluster:} \quad \mathcal{C} = \left\{ \mathbf{x}_i \mid D_{ij} < \epsilon \right\}
$$

##### Anomalie:
$$
\text{Outlier} \iff D_{ij} > \mu_D + 2\sigma_D
$$

### 3. Einschluss-Erkennung (Objekt im Hohlraum)

#### Ziel
Erkennung eines **dichten Objekts** innerhalb eines **nichtleitenden Hohlraums**.

#### Physikalisches Modell

##### Mehrschicht-Modell:
- Außenschicht (Luft):
  - $\varepsilon_r \approx 1$
  - $\sigma \approx 0$
- Einschluss (Metall/Kristall):
  - $\varepsilon_r \gg 1$, $\sigma > 0$

##### Reflexions-/Transmissionseffekte:
$$
Z_{\text{eff}} = \frac{Z_1 + jZ_2 \cdot \tan(k_2 d)}{1 + j \frac{Z_2}{Z_1} \cdot \tan(k_2 d)}
$$

##### Einschlussbedingung:
$$
\Delta Z = \text{lokales Maximum bei gleichzeitiger Dip-Zone außen}
$$

### 4. Unterscheidung Edelmetall vs. Legierung

#### Ziel
Erkennen von echten Edelmetallen anhand ihres elektromagnetischen Verhaltens.

#### Physikalisches Modell

##### Frequenz-Scan:
$$
Z(f) = R(f) + jX(f) \propto \sqrt{\frac{\mu \cdot \omega}{2 \cdot \sigma}}
$$

##### Skin-Effekt-Tiefe:
$$
\delta = \sqrt{\frac{2}{\mu \cdot \sigma \cdot \omega}}
$$

- **Gold**: $\sigma \approx 4.1 \times 10^7 \,\text{S/m}$
- **Bronze**: $\sigma \approx (1 - 2) \times 10^6 \,\text{S/m}$

##### Analyse:
$$
Z(f)_{\text{Gold}} < Z(f)_{\text{Bronze}} \quad \text{bei steigender Frequenz}
$$

### Zusammenfassung der Methoden

| Funktion                 | Modell              | Hauptformel / Kriterium                               |
| ------------------------ | ------------------- | ----------------------------------------------------- |
| Kristall-Erkennung       | $\varepsilon^*$, ∆Z | $\Delta Z \propto \sqrt{\varepsilon^*}$               |
| Clustervergleich         | $\|x_i - x_j\|$     | $D_{ij} > \mu + 2\sigma \Rightarrow \text{Outlier}$   |
| Einschluss im Hohlraum   | Mehrschicht-Modell  | $Z_{\text{eff}} = f(Z_1, Z_2, k_2, d)$                |
| Edelmetall vs. Legierung | Skin-Effekt         | $\delta = \sqrt{2 / (\mu \cdot \sigma \cdot \omega)}$ |

## 🚀 Quickstart für Entwickler

### Voraussetzungen
- Android Studio (aktuelle Version empfohlen)
- Android SDK (mind. API 24)
- Kotlin 1.8+
- Internetzugang für Abhängigkeits-Download

### Projekt aufsetzen & starten
1. Repository klonen:
   ```bash
   git clone https://github.com/emfad/EMFAD_App_Top.git
   cd EMFAD_App_Top
   ```
2. In Android Studio öffnen: `Datei > Öffnen... > Verzeichnis auswählen`
3. Gradle-Sync abwarten (Abhängigkeiten werden automatisch geladen)
4. App auf ein Android-Gerät (API 24+) oder Emulator installieren und starten

### Wichtige Hinweise
- **Bluetooth**: Die App benötigt Bluetooth-Berechtigungen (Pairing, Scannen, Verbindung). Diese werden beim ersten Start abgefragt.
- **Standort**: Für Bluetooth-Scanning ab Android 12 ist Standortfreigabe erforderlich.
- **Speicher**: Für das Speichern/Exportieren von Messdaten werden Speicherrechte benötigt.
- **Gerätekopplung**: Das EMFAD-Gerät muss vor der Messung per Bluetooth gekoppelt werden (Einstellungen > Bluetooth).

## 🗂️ Datenstruktur (Beispiel)

### MeasurementData (JSON)
```json
{
  "id": "b1a2c3d4-5678-1234-9abc-def012345678",
  "timestamp": 1712345678901,
  "frequency": 70.0,
  "mode": "A_B",
  "orientation": "HORIZONTAL",
  "value": 0.123,
  "latitude": 48.123456,
  "longitude": 11.654321
}
```

## 🛠️ Erweiterung & Entwicklung

### Neue Messmodi hinzufügen
- `MeasurementMode`-Enum in `Models/MeasurementData.kt` erweitern
- Logik im `MeasurementService` und ggf. im ViewModel anpassen

### Neue Auswertungen/Visualisierungen
- Neue Analysefunktionen im `MeasurementService` implementieren
- UI-Komponenten in `Views/` ergänzen (z.B. neue Diagramme)

### Export/Import
- Export als CSV/JSON kann über Kotlin-Serialization oder Gson erfolgen
- Import analog, z.B. für Kalibrierungsprofile

## 🆘 Kontakt & Support
- **Entwickler:** EMFAD Geophysikalische Umwelttechnik
- **E-Mail:** info@emfad.de
- **Web:** https://www.emfad.de
- **Support:** Bitte Fehlerberichte und Featurewünsche per E-Mail oder GitHub-Issue melden 