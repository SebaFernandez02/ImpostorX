# ImpostorX (Android · Jetpack Compose)

Juego social tipo “el impostor” pensado para jugar pasando el celular. Está hecho con Kotlin + Jetpack Compose, navegación declarativa y estado centralizado en un ViewModel. Las palabras se leen de JSON en assets/ por categoría y se barajan en cada partida.

### 📱 Flujo de pantallas

Home → “Toca para continuar”.

Nombres (Room) → agrega jugadores (lista editable).

Categorías → carrusel deslizable (Random, Fútbol, Deportes, Artistas, Geografía, Cine).

Impostores → elige cantidad (chips + slider + “Random”).

Reveal (revelado por jugador)

“Pasar el celular a: Jugador X” → tap

Muestra palabra (no impostor) o *“Sos el Impostor” → tap

Pasa al siguiente jugador hasta terminar.

Round Ready → “¡Que empiece la ronda!”

Volver a jugar: nueva partida con misma categoría, jugadores e impostores.

←: vuelve a Impostores (para ajustar cantidad).

### 🧩 Tecnologías

Kotlin 1.9.24

Jetpack Compose (BOM 2024.06.00) + Material 3

Navigation Compose 2.7.7

ViewModel (lifecycle 2.8.x)

Gradle 8.7 · AGP 8.5.0 (Android Studio Koala)

minSdk 23 · target/compileSdk 34 · JDK 17

### 🗂️ Estructura de proyecto
app/  
 ├─ src/main/assets/palabras/       # JSON por categoría  
 │    ├─ deportistas.json           # Deportes  
 │    ├─ futbolistas.json           # Fútbol  
 │    ├─ artistas.json              # Artistas  
 │    ├─ paises.json                # Geografía  
 │    ├─ peliculas.json             # Cine  
 │    └─ random.json                # Random (unión de todo)  
 └─ src/main/java/com/example/impostorx/  
      ├─ data/  
      │   └─ WordsRepository.kt     # Carga/parseo JSON por categoría  
      ├─ logic/  
      │   └─ GameViewModel.kt        

Tú dijiste:
pero hacemelo formato .md para poder pegarlo y que quede bien, estaba bien lo que estabas haciendo pero hacelo en formato .md
ChatGPT Plus
Pensó durante 27s
ImpostorX (Android · Jetpack Compose)

Juego social tipo “el impostor” pensado para jugar pasando el celular. Hecho con Kotlin + Jetpack Compose, navegación declarativa y estado centralizado en un ViewModel. Las palabras se leen de JSON en assets/ por categoría y se barajan en cada partida.

### ✨ Características

Flujo guiado: Home → Nombres → Categorías → Impostores → Revelado → Listo para jugar

Carrusel de categorías (Random, Fútbol, Deportes, Artistas, Geografía, Cine)

Selección de cantidad de impostores (chips + slider + opción Random)

Revelado privado por jugador (palabra o “Sos el Impostor”)

Volver a jugar: remezcla palabras conservando jugadores/categoría/impostores

Estado global en GameViewModel compartido en todo el NavHost

### 🧱 Tech stack

Kotlin 1.9.24 · JDK 17

Jetpack Compose (BOM 2024.06.00) + Material 3

Navigation Compose 2.7.7

Lifecycle/ViewModel 2.8.x

Gradle 8.7 · AGP 8.5.0 (Android Studio Koala)

minSdk 23 · compile/targetSdk 34

### 🗂️ Estructura
app/
 ├─ src/main/assets/palabras/       # JSON por categoría
 │    ├─ deportistas.json           # Deportes
 │    ├─ futbolistas.json           # Fútbol
 │    ├─ artistas.json              # Artistas
 │    ├─ paises.json                # Geografía
 │    ├─ peliculas.json             # Cine (nuevo)
 │    └─ random.json                # Random (unión de todos)
 └─ src/main/java/com/example/impostorx/
      ├─ data/
      │   └─ WordsRepository.kt     # Carga/parseo JSON por categoría
      ├─ logic/
      │   └─ GameViewModel.kt       # Estado global de partida/ronda
      └─ ui/
          ├─ ImpostorApp.kt         # NavHost + rutas
          └─ screens/
              ├─ ImpostorHome.kt
              ├─ ImpostorRoomScreen.kt
              ├─ CategoriesScreen.kt
              ├─ ImpostorCountScreen.kt
              ├─ RevealRoundScreen.kt
              └─ RoundReadyScreen.kt

### 🔀 Flujo de pantallas

Home – “Toca para continuar”.

Nombres (Room) – agrega jugadores (lista editable, borrar individual/todos).

Categorías – carrusel deslizable con tarjetas clickeables.

Impostores – chips 1..N/2 + Random, slider, botón Listo.

Reveal – por cada jugador:

“Pasar el celular a: Nombre” → tap

Muestra palabra (no impostor) o “Sos el Impostor” → tap

Continúa con el siguiente jugador hasta terminar.

Flecha ←: vuelve a Impostores (mismos jugadores/categoría).

Round Ready – “¡Que empiece la ronda!”

Volver a jugar: nueva partida (mismos jugadores/categoría/impostores, palabras remezcladas).

Flecha ←: vuelve a Impostores.

### 📦 Datos (JSON en assets/)

Colocá los archivos en app/src/main/assets/palabras/:

deportistas.json (Deportes)

futbolistas.json (Futbol)

artistas.json (Artistas)

paises.json (Geografia)

peliculas.json (Cine)

random.json (Random — combinación de todos)

Formatos aceptados por WordsRepository.parseWords:

["Lionel Messi", "Serena Williams", "Roger Federer"]

{"palabras": ["Argentina", "Brasil", "Uruguay"]}

[{"nombre":"Shakira"}, {"nombre":"Bad Bunny"}, {"nombre":"Dua Lipa"}]


Asegurate de guardar en UTF-8. Los slugs de categorías deben coincidir con el enum:
Random, Futbol, Deportes, Artistas, Geografia, Cine.

### 🧠 Estado y lógica

GameViewModel mantiene: players, category, impostors, seed, palabras barajadas y la ronda (fase ASSIGN/REVEAL, índice actual, set de impostores).

startMatch(context) carga y baraja palabras (respectando categoría).

startRound() elige una palabra para la ronda y asigna impostores aleatorios.

onTapNext() alterna Asignar → Revelar → siguiente jugador.

reshuffleForNewMatch() + startMatch() para Volver a jugar.

El GameViewModel se crea una vez en ImpostorApp y se pasa por parámetro a las pantallas (no usar viewModel() dentro de cada pantalla).

### ▶️ Cómo ejecutar

Android Studio (Koala) → Sync Project → Run en emulador/dispositivo.

CLI:

./gradlew clean build         # compila
./gradlew installDebug        # instala en un dispositivo conectado

### 🔧 Configuración (resumen de versiones)

plugins { id("com.android.application") version "8.5.0"; kotlin "1.9.24" }

compileSdk = 34 · targetSdk = 34 · minSdk = 23

Compose BOM 2024.06.00 · activity-ktx/activity-compose 1.9.3 · core-ktx 1.13.1

Navigation Compose 2.7.7 · Lifecycle 2.8.x

### 🗺️ Rutas de navegación
Home
Room
Categories/{total}
Impostors/{total}/{category}
Reveal
RoundReady

### ✅ TODO / Ideas futuras

Animaciones (fade/slide) en el revelado e indicadores de progreso.

Temporizador y fase de discusión/votación.

Persistencia ligera (última partida) y estadísticas.

Multi-idioma.

### 📄 Licencia

Libre uso educativo/demostrativo.
