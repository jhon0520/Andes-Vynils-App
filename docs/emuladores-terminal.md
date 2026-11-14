# Emuladores desde la terminal

Cómo prender, usar y apagar los emuladores de Android (AVD) sin abrir Android Studio. Sirve para correr las E2E (`docs/E2E-testing-guide.md`), probar cada release en API 21 y verificar el script de medición (`docs/performance/README.md`).

> Los emuladores **no** cuentan para el reporte de profiling: el curso exige 3 dispositivos físicos (ver `docs/plans/sprint2-rendimiento.md` §5).

## Requisitos

Los comandos `emulator` y `adb` vienen con el SDK de Android:

| Comando | Ruta en macOS |
|---|---|
| `emulator` | `~/Library/Android/sdk/emulator/emulator` |
| `adb` | `~/Library/Android/sdk/platform-tools/adb` |

Si la terminal no los encuentra, agrégalos al PATH en `~/.zshrc`:

```bash
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
```

## Ver los emuladores disponibles

```bash
emulator -list-avds
```

AVD creados hoy en la máquina del equipo: `Pixel_3a_API_30_x86`, `Pixel_8_API_28` y `Pixel_API_26`.

## Prender un emulador

```bash
emulator -avd Pixel_3a_API_30_x86        # con ventana; ocupa la terminal
emulator -avd Pixel_3a_API_30_x86 &      # deja la terminal libre
```

Opciones útiles:

| Comando | Para qué |
|---|---|
| `emulator -avd Pixel_8_API_28 -no-snapshot-load` | Arranque en frío, sin el estado guardado |
| `emulator -avd Pixel_API_26 -wipe-data` | Como recién instalado (borra apps y datos) |
| `emulator -avd Pixel_API_26 -no-window -no-audio -no-boot-anim` | Sin ventana, para pruebas automáticas |

## Comprobar que arrancó

```bash
adb devices                            # debe aparecer, p. ej., emulator-5554  device
adb shell getprop sys.boot_completed   # devuelve 1 cuando terminó de arrancar
```

Antes de correr las E2E, apaga las animaciones:

```bash
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

Desde el emulador, el backend local se alcanza en `http://10.0.2.2:3000/` (ver README, «Cambiar el dominio de la API»).

## Varios emuladores a la vez

Cada emulador toma su propio puerto y serial (`emulator-5554`, `emulator-5556`…). Para elegir uno:

```bash
adb -s emulator-5556 install -r app/build/outputs/apk/debug/app-debug.apk
ANDROID_SERIAL=emulator-5556 ./gradlew connectedDebugAndroidTest
./scripts/perf-capture.sh prueba HU01 -s emulator-5556
```

Con 16 GB de RAM conviene prender uno a la vez, sobre todo si se está midiendo rendimiento: varios en paralelo compiten por CPU y memoria.

## Apagar un emulador

```bash
adb -s emulator-5554 emu kill
```

## Crear el emulador API 21

El curso exige probar cada release en Android L (API 21, `minSdk = 21`). Hoy no hay un AVD con esa versión.

1. Instalar las herramientas de línea de comandos (no están instaladas): Android Studio → *Settings → Languages & Frameworks → Android SDK → SDK Tools* → marcar **Android SDK Command-line Tools (latest)** → *Apply*. Quedan en `~/Library/Android/sdk/cmdline-tools/latest/bin/`.
2. Descargar la imagen y crear el AVD:

   ```bash
   sdkmanager "system-images;android-21;google_apis;x86"
   avdmanager create avd -n Pixel_API_21 -k "system-images;android-21;google_apis;x86" -d pixel
   emulator -avd Pixel_API_21
   ```

Alternativa sin terminal: Android Studio → *Device Manager* → *Create Virtual Device* → Pixel → imagen **Lollipop (API 21) x86**.

## Problemas comunes

| Síntoma | Solución |
|---|---|
| `command not found: emulator` | Agregar el SDK al PATH (sección «Requisitos») o usar la ruta completa |
| `adb devices` muestra `offline` | `adb kill-server && adb start-server` |
| El emulador no arranca o va muy lento | Revisar que no haya otro emulador prendido; arrancar con `-no-snapshot-load` |
| `PANIC: Missing emulator engine program` | Ejecutar el `emulator` de `~/Library/Android/sdk/emulator/`, no el de `tools/` |
| La app no llega al backend local | Usar `http://10.0.2.2:3000/` en `local.properties`, o la IP actual del Mac (`ipconfig getifaddr en0`) |
