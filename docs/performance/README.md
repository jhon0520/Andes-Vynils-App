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

Una prueba del script en el emulador API 30 sin ventana (render por software) dio 95,7 % de frames lentos y 0 KB de memoria gráfica en HU01. Esos números reflejan el emulador, no la app, y por eso no entran al reporte. El emulador sirve para verificar que el script funciona y para la prueba obligatoria en API 21.
