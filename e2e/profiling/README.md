# Perfilamiento en dispositivos físicos

Mide el consumo de la app en los recorridos de las HU (P-ONB y P-HU01 a P-HU08) en un teléfono conectado por USB. Kraken maneja la UI con los mismos pasos de `e2e/kraken`, y los pasos de `features/mobile/step_definitions/profiling.js` leen con `adb` lo que consume la app:

| Métrica | Fuente |
|---|---|
| Arranque en frío | `am start -S -W` (TotalTime, 5 veces) |
| Tiempo hasta ver cada pantalla | Desde el toque hasta que Appium ve el elemento (incluye la ida y vuelta de Appium, que se reporta aparte) |
| CPU promedio y pico, hilo `main` frente a los demás | `/proc/<pid>/stat` y `/proc/<pid>/task/<pid>/stat`, muestreo cada 0,5 s |
| Memoria (PSS, Java, nativa, gráficos), RSS pico, hilos, Activities vivas | `dumpsys meminfo` y `/proc/<pid>/status` |
| Frames lentos (> 16 ms), congelados (> 700 ms), jank, p90 y p99 | `dumpsys gfxinfo` |
| Peticiones a la API (total y desde cada punto de control) | Log de OkHttp en logcat (solo builds debug; las portadas de Coil no se cuentan) |
| ANR y cierres | `logcat -b events` (`am_anr`) y `logcat -b crash` |

## Cómo correrlo

Requisitos: el backend BackVynils arriba en `localhost:3000` (con `api.base.url=http://localhost:3000/` en `local.properties`), `adb` en el `PATH`, Node 18+ y **un solo** teléfono conectado con la depuración USB activa.

```bash
export PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
e2e/profiling/build-apks.sh            # apk/antes.apk (develop @ 8921089) y apk/despues.apk (rama actual)
e2e/profiling/run-profiling.sh despues # 3 corridas; para todos los dispositivos
e2e/profiling/run-profiling.sh antes   # solo en el dispositivo de gama más baja
```

Cada escenario deja `results/<antes|despues>/<modelo>/<escenario>-run<N>.json`, y `summarize.js` arma `results/summary.md` con la mediana de las corridas y la tabla antes/después. Los resultados se suben al repositorio; los datos de la wiki ([SP02 · Perfilamiento](../../../wiki/SP02‐Perfilamiento)) salen de ese resumen.

Durante la corrida se ponen las animaciones a velocidad normal y la pantalla siempre encendida; al terminar se restauran los valores del teléfono.

## Límites

- Es un build **debug** (el mismo para antes y después): los tiempos absolutos son peores que en un release, pero la comparación es justa.
- El APK «antes» (8921089) no tiene detalle de artista ni de coleccionista, ni creación de álbumes o tracks: P-HU04 y P-HU06 a P-HU08 se marcan como omitidos en esa versión.
- P-HU07 y P-HU08 escriben en el backend: cada corrida crea un álbum y agrega un track al álbum semilla. Córrelos contra el backend local, no contra el de Render.
- Para las asignaciones por tipo de objeto y el *System Trace* se usa el Profiler de Android Studio; este script cubre lo que se puede medir igual en los 3 teléfonos.
