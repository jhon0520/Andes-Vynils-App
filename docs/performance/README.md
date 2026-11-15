# Profiling de las HU (sprint 2)

Guía para medir el consumo de recursos de las HU en **3 dispositivos físicos** con el mismo método. Por qué no se usan emuladores ni `docker-android`: ver `docs/plans/sprint2-rendimiento.md` §5. Qué se optimizó y cómo se midió: `docs/performance/micro-optimizaciones.md`.

## 1. Preparar el teléfono

1. Activar *Opciones de desarrollador* → *Depuración USB* y conectar el teléfono (`adb devices` debe listarlo).
2. Backend estable (Render tarda en arrancar cuando está inactivo):
   - Local (recomendado): levantar BackVynils (`./scripts/e2e-local.sh --skip-tests`, o `docker compose up -d` en `../BackVynils`), luego `adb reverse tcp:3000 tcp:3000` y `api.base.url=http://localhost:3000/` en `local.properties`.
   - Render: abrir `https://backvynils-q6yc.onrender.com/albums` en el navegador antes de cada serie para "despertarlo".
3. Instalar el build de profiling (R8 activo, sin logging HTTP, `profileable`):
   ```bash
   ./gradlew installProfiling
   ```
   En Android 9 o anterior el Profiler de Android Studio no se conecta a un build no depurable: usar `./gradlew installDebug` solo para las capturas del Profiler en ese teléfono y anotarlo como limitación. El script de §3 funciona con los dos builds.
4. Batería > 50 %, sin ahorro de energía, Wi-Fi encendido, otras apps cerradas, animaciones del sistema normales.

## 2. Recorridos

Cada recorrido se repite **3 veces** por dispositivo y se reporta la mediana. Antes de cada repetición: cerrar la app (`adb shell am force-stop com.dev.wine.music.app`) y abrirla otra vez.

| HU | Recorrido medido |
|---|---|
| Arranque | `./scripts/perf-capture.sh <dispositivo> startup` (3 arranques en frío automáticos) |
| HU01 Catálogo | Visitante → esperar el catálogo → *scroll* hasta el final y de vuelta → escribir «a» en la búsqueda y borrarla → filtrar por Salsa → Todos |
| HU02 Detalle | Desde el catálogo abrir 3 álbumes distintos, volviendo entre cada uno; abrir otra vez el primero (debe salir de caché) |
| HU03 Artistas | Pestaña Artistas → *scroll* → Músicos → Bandas → Todos → ir a Álbumes y volver (sin peticiones nuevas) |
| HU05 Coleccionistas | Pestaña Coleccionistas → *scroll* → ir a Álbumes y volver |
| 5.ª HU | Según la HU entregada (HU04 o HU06): abrir 3 detalles |

## 3. Capturar las métricas

```bash
./scripts/perf-capture.sh <dispositivo> startup
./scripts/perf-capture.sh <dispositivo> HU01      # abre la app, ejecuta el comando, haz el recorrido y pulsa Enter
./scripts/perf-capture.sh <dispositivo> HU01 -s SERIAL   # si hay varios dispositivos conectados
```

### Modo automático (sin tocar el dispositivo)

`scripts/perf-walkthrough.sh` hace los recorridos de §2 con `adb` y `uiautomator`, buscando los elementos por `testTag` (`MainScreen` activa `testTagsAsResourceId`). Así cada repetición es idéntica en cualquier teléfono o emulador:

```bash
./scripts/perf-capture.sh <dispositivo> HU01 --auto   # reinicia la app, hace el recorrido y mide
./scripts/perf-run-all.sh <dispositivo>               # arranque + HU01–HU05 × 3 y tabla de medianas
./scripts/perf-run-all.sh <dispositivo> -s SERIAL
```

`perf-run-all.sh` deja además `medians-*.md` con la mediana de cada métrica por HU, lista para pegar en la wiki. Cada `uiautomator dump` consume algo de CPU del dispositivo; como está en todas las corridas, los resultados siguen siendo comparables entre sí, pero la CPU medida incluye ese costo. El Profiler de Android Studio puede quedar conectado mientras corre el modo automático para tomar las capturas de §3.

`<dispositivo>` es una etiqueta corta sin espacios, p. ej. `samsung-a10`. El script guarda en `docs/performance/results/<dispositivo>/`:

| Archivo | Contenido |
|---|---|
| `device.txt` | Fabricante, modelo, Android/API, ABI, RAM, si el build es depurable |
| `startup-*.txt` | `TotalTime` de 3 arranques en frío y la mediana |
| `<HU>-*-summary.txt` | Frames dibujados, % de frames lentos (*janky*), percentil 90/95 del frame, PSS total, heap Java, heap nativo, gráficos, CPU media del proceso, ANR |
| `<HU>-*-gfxinfo.txt`, `<HU>-*-meminfo.txt` | Salida completa de `dumpsys gfxinfo` y `dumpsys meminfo` |

Con el **Android Studio Profiler** (*View → Tool Windows → Profiler*, seleccionar el proceso `com.dev.wine.music.app`) tomar en cada HU al menos:
- **CPU**: *System Trace* del recorrido; captura del hilo `main` (no debe tener bloques largos de red o JSON).
- **Memory**: gráfica durante el recorrido y un *heap dump* al final; filtro *Show activity/fragment leaks* = 0.
- **Network Inspector**: lista de peticiones; la segunda visita a una pantalla no debe generar peticiones.
- **Energy** (Android 8+): captura cualitativa.

Guardar las capturas como `docs/performance/results/<dispositivo>/<HU>-<métrica>.png`.

## 4. Plantilla del reporte (wiki → Sprint 2 → Profiling)

**Dispositivos**

| Etiqueta | Modelo | Android (API) | RAM | Quién midió | Fecha | Backend |
|---|---|---|---|---|---|---|
| | | | | | | local / Render |

**Resultados por HU** (una tabla por HU, mediana de 3 repeticiones)

| Métrica | Dispositivo 1 | Dispositivo 2 | Dispositivo 3 |
|---|---|---|---|
| Arranque en frío (ms) | | | |
| PSS total (MB) | | | |
| Heap Java (MB) | | | |
| CPU media (%) | | | |
| Frames lentos (%) | | | |
| Frame p90 (ms) | | | |
| Peticiones 1.ª visita / 2.ª visita | | | |
| ANR | 0 | 0 | 0 |

Después de las tablas, una conclusión por HU, la comparación antes/después en el dispositivo de referencia (ver `micro-optimizaciones.md`), los hallazgos registrados como issues (`bug`, `performance`) y las limitaciones.

## 5. Advertencia sobre emuladores

### Correr la serie en un emulador con el backend local

1. `local.properties`: `api.base.url=http://10.0.2.2:3000/` (el emulador ve el `localhost` del Mac en `10.0.2.2`). Recordar volver a la URL anterior para teléfonos físicos.
2. Backend arriba: `curl http://localhost:3000/albums` debe responder 200.
3. Emulador con GPU del host y ventana (sin ventana usa render por software): `emulator -avd Pixel_3a_API_30_x86 -gpu host -no-snapshot-load -no-audio -no-boot-anim &`. API 29 o superior para que el Profiler se conecte al build `profileable`.
4. `./gradlew installProfiling` y `./scripts/perf-run-all.sh emulator-api30`.


Una prueba del script en el emulador API 30 sin ventana (render por software) dio 95,7 % de frames lentos y 0 KB de memoria gráfica en HU01. Esos números reflejan el emulador, no la app, y por eso no entran al reporte. El emulador sirve para verificar que el script funciona y para la prueba obligatoria en API 21.
