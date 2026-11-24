# Resultados de perfilamiento

Generado el 2026-11-24 01:20 UTC con `e2e/profiling/summarize.js`. Cada valor es la **mediana** de las corridas.

## Dispositivos

| Modelo | Android (API) | RAM | SoC | Núcleos · frec. máx. | Pantalla |
|---|---|---|---|---|---|
| HONOR HONOR 400 Smart (LGN-NX3) | 15 (API 35) | 5,4 GB | QTI SM6375 | 8 · 2,30 GHz | 720x1610 @ 320 dpi |
| unknown Android SDK built for x86 (Android SDK built for x86) | 11 (API 30) | 1,9 GB | ranchu | 6 · 0,00 GHz | 1080x2220 @ 440 dpi |
| Google Android SDK built for x86 (Android SDK built for x86) | 8.0.0 (API 26) | 3,0 GB | ranchu | 4 · — GHz | 1080x1920 @ 420 dpi |
| motorola moto e20 (moto e20) | 11 (API 30) | 1,8 GB | ums9230 | 8 · 1,61 GHz | 720x1600 @ 280 dpi |
| motorola moto g13 (moto g13) | 14 (API 34) | 3,7 GB | Mediatek MT6769 | 8 · 2,00 GHz | 720x1600 @ 280 dpi |
| Google Emulador Pixel (AVD Medium_Phone_API_36.1) (sdk_gphone64_arm64) | 16 (API 36) | 1,9 GB | AOSP ranchu | 1 · — GHz | 1080x2400 @ 420 dpi |

## LGN-NX3 · antes

### P-ONB (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.766 | 2.761 | 2.773 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.142 | 1.131 | 1.171 |
| Tiempo hasta ver «catalogo» (ms) | 1.889 | 1.414 | 1.954 |
| CPU promedio del recorrido (% del dispositivo) | 5,4 | 5,4 | 5,6 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,8 | 24,5 | 29,8 |
| Tiempo de CPU en el hilo `main` (ms) | 2.650 | 2.530 | 2.650 |
| Tiempo de CPU en los demás hilos (ms) | 2.280 | 2.260 | 2.300 |
| Memoria total PSS al final (MB) | 114,2 | 113,5 | 114,5 |
| Java/Kotlin heap al final (MB) | 16,4 | 16,2 | 16,8 |
| Native heap al final (MB) | 18,7 | 18,5 | 19,5 |
| Gráficos al final (MB) | 18,5 | 18,5 | 18,5 |
| RSS pico (MB) | 204,4 | 204,3 | 205,2 |
| Hilos vivos (pico) | 46 | 45 | 47 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 120 | 116 | 127 |
| Frames lentos (> 16 ms) | 29 | 23 | 30 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 12,6 | 11,7 | 14,7 |
| Frame p90 (ms) | 48 | 44 | 61 |
| Frame p99 (ms) | 300 | 250 | 300 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 45 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.487 | 1.464 | 1.642 |
| CPU promedio del recorrido (% del dispositivo) | 3,6 | 3,5 | 3,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 30,6 | 30,2 | 30,9 |
| Tiempo de CPU en el hilo `main` (ms) | 4.590 | 4.500 | 4.630 |
| Tiempo de CPU en los demás hilos (ms) | 3.740 | 3.600 | 3.790 |
| Memoria total PSS al final (MB) | 115,4 | 115,1 | 120,5 |
| Java/Kotlin heap al final (MB) | 16,8 | 16,6 | 22,2 |
| Native heap al final (MB) | 19,1 | 19,1 | 19,2 |
| Gráficos al final (MB) | 18,8 | 18,8 | 18,8 |
| RSS pico (MB) | 209,4 | 208,7 | 211,3 |
| Hilos vivos (pico) | 48 | 47 | 48 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 405 | 400 | 408 |
| Frames lentos (> 16 ms) | 71 | 63 | 79 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 4,0 | 3,9 | 4,5 |
| Frame p90 (ms) | 25 | 25 | 26 |
| Frame p99 (ms) | 150 | 109 | 150 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 50 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.037 | 1.030 | 1.385 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 783 | 733 | 811 |
| CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,9 | 5,0 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,4 | 21,8 | 33,1 |
| Tiempo de CPU en el hilo `main` (ms) | 4.460 | 4.380 | 4.680 |
| Tiempo de CPU en los demás hilos (ms) | 3.290 | 3.170 | 3.310 |
| Memoria total PSS al final (MB) | 115,4 | 114,5 | 116,2 |
| Java/Kotlin heap al final (MB) | 19,3 | 18,4 | 19,8 |
| Native heap al final (MB) | 15,8 | 15,7 | 16,3 |
| Gráficos al final (MB) | 19,3 | 19,0 | 19,4 |
| RSS pico (MB) | 208,7 | 208,5 | 210,2 |
| Hilos vivos (pico) | 49 | 49 | 51 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 218 | 209 | 236 |
| Frames lentos (> 16 ms) | 31 | 31 | 33 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 10,1 | 9,8 | 10,1 |
| Frame p90 (ms) | 29 | 22 | 31 |
| Frame p99 (ms) | 200 | 200 | 300 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 51 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 1.246 | 1.152 | 1.253 |
| Tiempo hasta ver «artistas-regreso» (ms) | 627 | 574 | 1.193 |
| CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,9 | 5,0 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,8 | 26,1 | 29,2 |
| Tiempo de CPU en el hilo `main` (ms) | 4.970 | 4.880 | 5.170 |
| Tiempo de CPU en los demás hilos (ms) | 3.200 | 3.180 | 3.210 |
| Memoria total PSS al final (MB) | 116,7 | 116,3 | 116,7 |
| Java/Kotlin heap al final (MB) | 21,6 | 21,6 | 22,2 |
| Native heap al final (MB) | 15,6 | 15,5 | 15,7 |
| Gráficos al final (MB) | 18,3 | 18,3 | 18,4 |
| RSS pico (MB) | 208,3 | 206,2 | 209,4 |
| Hilos vivos (pico) | 50 | 49 | 51 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 234 | 233 | 243 |
| Frames lentos (> 16 ms) | 33 | 31 | 39 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 10,3 | 10,3 | 13,7 |
| Frame p90 (ms) | 25 | 23 | 28 |
| Frame p99 (ms) | 200 | 200 | 250 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 49 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.105 | 1.052 | 1.319 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 997 | 660 | 1.100 |
| CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,8 | 5,0 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,2 | 27,7 | 31,4 |
| Tiempo de CPU en el hilo `main` (ms) | 4.080 | 3.950 | 4.110 |
| Tiempo de CPU en los demás hilos (ms) | 2.860 | 2.750 | 2.890 |
| Memoria total PSS al final (MB) | 113,1 | 112,3 | 113,4 |
| Java/Kotlin heap al final (MB) | 19,5 | 18,6 | 19,9 |
| Native heap al final (MB) | 15,1 | 14,9 | 15,1 |
| Gráficos al final (MB) | 18,3 | 18,3 | 18,3 |
| RSS pico (MB) | 208,2 | 207,3 | 210,5 |
| Hilos vivos (pico) | 45 | 44 | 51 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 173 | 168 | 174 |
| Frames lentos (> 16 ms) | 31 | 29 | 34 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 11,6 | 9,5 | 14,4 |
| Frame p90 (ms) | 32 | 32 | 42 |
| Frame p99 (ms) | 250 | 250 | 300 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 48 ms por consulta).

## LGN-NX3 · despues

### P-ONB (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.702 | 2.696 | 2.704 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.118 | 1.113 | 1.168 |
| Tiempo hasta ver «catalogo» (ms) | 1.413 | 1.296 | 1.467 |
| CPU promedio del recorrido (% del dispositivo) | 5,5 | 5,4 | 5,5 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 25,5 | 21,2 | 29,4 |
| Tiempo de CPU en el hilo `main` (ms) | 2.290 | 2.220 | 2.320 |
| Tiempo de CPU en los demás hilos (ms) | 2.070 | 2.060 | 2.120 |
| Memoria total PSS al final (MB) | 110,8 | 110,7 | 111,6 |
| Java/Kotlin heap al final (MB) | 16,7 | 16,1 | 17,0 |
| Native heap al final (MB) | 19,1 | 18,7 | 19,3 |
| Gráficos al final (MB) | 18,5 | 18,4 | 18,5 |
| RSS pico (MB) | 207,3 | 206,4 | 208,7 |
| Hilos vivos (pico) | 54 | 53 | 55 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 118 | 118 | 127 |
| Frames lentos (> 16 ms) | 49 | 42 | 62 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 14,4 | 13,4 | 17,0 |
| Frame p90 (ms) | 44 | 42 | 57 |
| Frame p99 (ms) | 250 | 250 | 250 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 26 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.833 | 1.628 | 2.291 |
| CPU promedio del recorrido (% del dispositivo) | 3,7 | 3,5 | 3,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 27,3 | 23,2 | 28,7 |
| Tiempo de CPU en el hilo `main` (ms) | 4.480 | 4.400 | 4.770 |
| Tiempo de CPU en los demás hilos (ms) | 3.850 | 3.820 | 3.950 |
| Memoria total PSS al final (MB) | 110,5 | 105,9 | 112,6 |
| Java/Kotlin heap al final (MB) | 16,3 | 15,3 | 21,9 |
| Native heap al final (MB) | 15,2 | 15,1 | 18,6 |
| Gráficos al final (MB) | 18,7 | 18,7 | 18,8 |
| RSS pico (MB) | 210,6 | 209,2 | 215,8 |
| Hilos vivos (pico) | 48 | 48 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 432 | 411 | 458 |
| Frames lentos (> 16 ms) | 68 | 50 | 81 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 3,7 | 3,1 | 4,9 |
| Frame p90 (ms) | 24 | 23 | 26 |
| Frame p99 (ms) | 150 | 89 | 150 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 40 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.510 | 1.063 | 2.324 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 847 | 812 | 909 |
| CPU promedio del recorrido (% del dispositivo) | 4,7 | 4,6 | 5,2 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,8 | 28,6 | 31,2 |
| Tiempo de CPU en el hilo `main` (ms) | 4.610 | 4.400 | 5.070 |
| Tiempo de CPU en los demás hilos (ms) | 3.310 | 3.060 | 3.600 |
| Memoria total PSS al final (MB) | 112,8 | 112,1 | 113,1 |
| Java/Kotlin heap al final (MB) | 19,4 | 19,1 | 19,5 |
| Native heap al final (MB) | 16,2 | 16,2 | 16,6 |
| Gráficos al final (MB) | 19,3 | 19,3 | 19,4 |
| RSS pico (MB) | 211,2 | 210,6 | 212,0 |
| Hilos vivos (pico) | 52 | 51 | 54 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 219 | 207 | 253 |
| Frames lentos (> 16 ms) | 28 | 27 | 33 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 9,5 | 8,7 | 14,0 |
| Frame p90 (ms) | 22 | 18 | 48 |
| Frame p99 (ms) | 200 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 50 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 1.259 | 1.177 | 1.304 |
| Tiempo hasta ver «artistas-regreso» (ms) | 681 | 653 | 1.208 |
| CPU promedio del recorrido (% del dispositivo) | 4,9 | 4,7 | 5,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,4 | 20,3 | 27,5 |
| Tiempo de CPU en el hilo `main` (ms) | 5.070 | 5.000 | 5.140 |
| Tiempo de CPU en los demás hilos (ms) | 3.230 | 3.180 | 3.290 |
| Memoria total PSS al final (MB) | 108,7 | 106,6 | 112,4 |
| Java/Kotlin heap al final (MB) | 16,5 | 15,6 | 21,2 |
| Native heap al final (MB) | 15,7 | 15,2 | 16,3 |
| Gráficos al final (MB) | 18,4 | 18,3 | 18,4 |
| RSS pico (MB) | 209,9 | 206,8 | 210,4 |
| Hilos vivos (pico) | 53 | 51 | 62 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 248 | 236 | 250 |
| Frames lentos (> 16 ms) | 32 | 31 | 32 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 10,8 | 8,9 | 11,4 |
| Frame p90 (ms) | 24 | 24 | 27 |
| Frame p99 (ms) | 200 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 68 ms por consulta).

### P-HU04 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-musico» (ms) | 935 | 923 | 975 |
| Tiempo hasta ver «album-desde-artista» (ms) | 640 | 610 | 709 |
| Tiempo hasta ver «detalle-banda» (ms) | 829 | 784 | 1.221 |
| CPU promedio del recorrido (% del dispositivo) | 4,8 | 4,7 | 4,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 29,3 | 27,8 | 29,8 |
| Tiempo de CPU en el hilo `main` (ms) | 5.790 | 5.780 | 5.830 |
| Tiempo de CPU en los demás hilos (ms) | 4.010 | 3.950 | 4.060 |
| Memoria total PSS al final (MB) | 118,6 | 111,2 | 118,8 |
| Java/Kotlin heap al final (MB) | 22,0 | 16,3 | 22,0 |
| Native heap al final (MB) | 19,3 | 17,3 | 19,6 |
| Gráficos al final (MB) | 19,5 | 19,5 | 19,5 |
| RSS pico (MB) | 213,7 | 213,3 | 215,7 |
| Hilos vivos (pico) | 51 | 49 | 52 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 328 | 324 | 337 |
| Frames lentos (> 16 ms) | 37 | 34 | 74 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 9,8 | 9,5 | 10,2 |
| Frame p90 (ms) | 18 | 18 | 32 |
| Frame p99 (ms) | 150 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 8 | 8 | 8 |
| Peticiones a la API desde «detalle-musico» | 2 | 2 | 2 |
| Peticiones a la API desde «album-desde-artista» | 2 | 2 | 2 |
| Peticiones a la API desde «detalle-banda» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /musicians` ×1,0, `GET /bands` ×1,0, `GET /musicians/100` ×1,0, `GET /performerprizes` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0, `GET /bands/101` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 67 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.196 | 1.054 | 1.198 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 711 | 608 | 1.239 |
| CPU promedio del recorrido (% del dispositivo) | 4,8 | 4,7 | 4,9 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,1 | 24,7 | 29,6 |
| Tiempo de CPU en el hilo `main` (ms) | 4.640 | 4.440 | 4.710 |
| Tiempo de CPU en los demás hilos (ms) | 3.150 | 3.080 | 3.170 |
| Memoria total PSS al final (MB) | 112,2 | 111,4 | 112,8 |
| Java/Kotlin heap al final (MB) | 20,4 | 20,3 | 21,2 |
| Native heap al final (MB) | 15,9 | 15,5 | 16,2 |
| Gráficos al final (MB) | 18,4 | 18,3 | 18,4 |
| RSS pico (MB) | 210,5 | 210,4 | 211,2 |
| Hilos vivos (pico) | 52 | 52 | 53 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 195 | 190 | 200 |
| Frames lentos (> 16 ms) | 39 | 29 | 43 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 11,8 | 10,0 | 12,6 |
| Frame p90 (ms) | 44 | 27 | 44 |
| Frame p99 (ms) | 250 | 200 | 250 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «buscar» | 0 | 0 | 0 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 64 ms por consulta).

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.065 | 1.021 | 1.065 |
| Tiempo hasta ver «artista-favorito» (ms) | 811 | 674 | 888 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 877 | 855 | 895 |
| CPU promedio del recorrido (% del dispositivo) | 4,6 | 4,3 | 4,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,4 | 22,1 | 29,3 |
| Tiempo de CPU en el hilo `main` (ms) | 6.470 | 6.400 | 6.620 |
| Tiempo de CPU en los demás hilos (ms) | 4.160 | 4.010 | 4.300 |
| Memoria total PSS al final (MB) | 113,9 | 113,3 | 121,2 |
| Java/Kotlin heap al final (MB) | 18,1 | 17,0 | 22,5 |
| Native heap al final (MB) | 16,9 | 16,3 | 19,6 |
| Gráficos al final (MB) | 19,6 | 19,5 | 19,9 |
| RSS pico (MB) | 213,8 | 212,0 | 215,0 |
| Hilos vivos (pico) | 52 | 51 | 55 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 314 | 314 | 322 |
| Frames lentos (> 16 ms) | 47 | 43 | 52 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 10,6 | 10,2 | 10,8 |
| Frame p90 (ms) | 26 | 22 | 26 |
| Frame p99 (ms) | 150 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100/albums` ×2,0, `GET /collectors/100` ×2,0, `GET /musicians/100` ×1,0, `GET /bands` ×1,0, `GET /performerprizes` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 57 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 966 | 895 | 1.151 |
| Tiempo hasta ver «album-creado» (ms) | 2.095 | 2.029 | 2.766 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.614 | 1.533 | 1.639 |
| CPU promedio del recorrido (% del dispositivo) | 5,2 | 5,0 | 5,2 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 22,7 | 19,1 | 27,3 |
| Tiempo de CPU en el hilo `main` (ms) | 9.810 | 9.740 | 10.080 |
| Tiempo de CPU en los demás hilos (ms) | 5.620 | 5.430 | 5.660 |
| Memoria total PSS al final (MB) | 115,1 | 114,6 | 115,5 |
| Java/Kotlin heap al final (MB) | 17,6 | 17,1 | 18,7 |
| Native heap al final (MB) | 17,8 | 17,6 | 18,1 |
| Gráficos al final (MB) | 18,4 | 18,1 | 18,5 |
| RSS pico (MB) | 215,1 | 215,0 | 215,6 |
| Hilos vivos (pico) | 54 | 54 | 55 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 533 | 504 | 535 |
| Frames lentos (> 16 ms) | 203 | 138 | 236 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 14,8 | 14,4 | 15,9 |
| Frame p90 (ms) | 46 | 44 | 48 |
| Frame p99 (ms) | 250 | 250 | 250 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/1` ×0,3, `GET /albums/2` ×0,3, `GET /albums/3` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 50 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 1.070 | 907 | 1.076 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 1.993 | 1.983 | 1.997 |
| Tiempo hasta ver «track-guardado» (ms) | 1.314 | 1.291 | 1.360 |
| CPU promedio del recorrido (% del dispositivo) | 4,5 | 4,4 | 4,6 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 22,6 | 22,3 | 22,9 |
| Tiempo de CPU en el hilo `main` (ms) | 7.240 | 7.010 | 7.480 |
| Tiempo de CPU en los demás hilos (ms) | 4.500 | 4.400 | 4.690 |
| Memoria total PSS al final (MB) | 118,9 | 118,7 | 119,4 |
| Java/Kotlin heap al final (MB) | 21,0 | 20,8 | 21,1 |
| Native heap al final (MB) | 17,7 | 17,6 | 17,7 |
| Gráficos al final (MB) | 19,8 | 19,8 | 19,8 |
| RSS pico (MB) | 216,2 | 216,1 | 216,9 |
| Hilos vivos (pico) | 54 | 54 | 58 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 413 | 408 | 420 |
| Frames lentos (> 16 ms) | 141 | 58 | 214 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 8,3 | 7,9 | 9,0 |
| Frame p90 (ms) | 26 | 24 | 27 |
| Frame p99 (ms) | 200 | 200 | 200 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 70 ms por consulta).

## LGN-NX3 · antes vs. después

| Escenario | Métrica | Antes | Después | Cambio |
|---|---|---|---|---|
| P-ONB | Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.766 | 2.702 | -2 % |
| P-ONB | Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.142 | 1.118 | -2 % |
| P-ONB | Tiempo hasta ver «catalogo» (ms) | 1.889 | 1.413 | -25 % |
| P-ONB | CPU promedio del recorrido (% del dispositivo) | 5,4 | 5,5 | +2 % |
| P-ONB | CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,8 | 25,5 | +3 % |
| P-ONB | Tiempo de CPU en el hilo `main` (ms) | 2.650 | 2.290 | -14 % |
| P-ONB | Tiempo de CPU en los demás hilos (ms) | 2.280 | 2.070 | -9 % |
| P-ONB | Memoria total PSS al final (MB) | 114,2 | 110,8 | -3 % |
| P-ONB | Java/Kotlin heap al final (MB) | 16,4 | 16,7 | +2 % |
| P-ONB | Native heap al final (MB) | 18,7 | 19,1 | +2 % |
| P-ONB | Gráficos al final (MB) | 18,5 | 18,5 | -0 % |
| P-ONB | RSS pico (MB) | 204,4 | 207,3 | +1 % |
| P-ONB | Hilos vivos (pico) | 46 | 54 | +17 % |
| P-ONB | Activities vivas al final | 1 | 1 | +0 % |
| P-ONB | Frames dibujados | 120 | 118 | -2 % |
| P-ONB | Frames lentos (> 16 ms) | 29 | 49 | +69 % |
| P-ONB | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-ONB | Frames con jank (%) | 12,6 | 14,4 | +14 % |
| P-ONB | Frame p90 (ms) | 48 | 44 | -8 % |
| P-ONB | Frame p99 (ms) | 300 | 250 | -17 % |
| P-ONB | Peticiones a la API en todo el recorrido | 2 | 2 | +0 % |
| P-ONB | ANR | 0 | 0 | = |
| P-ONB | Cierres inesperados | 0 | 0 | = |
| P-HU01 | Tiempo hasta ver «catalogo» (ms) | 1.487 | 1.833 | +23 % |
| P-HU01 | CPU promedio del recorrido (% del dispositivo) | 3,6 | 3,7 | +3 % |
| P-HU01 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 30,6 | 27,3 | -11 % |
| P-HU01 | Tiempo de CPU en el hilo `main` (ms) | 4.590 | 4.480 | -2 % |
| P-HU01 | Tiempo de CPU en los demás hilos (ms) | 3.740 | 3.850 | +3 % |
| P-HU01 | Memoria total PSS al final (MB) | 115,4 | 110,5 | -4 % |
| P-HU01 | Java/Kotlin heap al final (MB) | 16,8 | 16,3 | -3 % |
| P-HU01 | Native heap al final (MB) | 19,1 | 15,2 | -20 % |
| P-HU01 | Gráficos al final (MB) | 18,8 | 18,7 | -0 % |
| P-HU01 | RSS pico (MB) | 209,4 | 210,6 | +1 % |
| P-HU01 | Hilos vivos (pico) | 48 | 48 | +0 % |
| P-HU01 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU01 | Frames dibujados | 405 | 432 | +7 % |
| P-HU01 | Frames lentos (> 16 ms) | 71 | 68 | -4 % |
| P-HU01 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU01 | Frames con jank (%) | 4,0 | 3,7 | -6 % |
| P-HU01 | Frame p90 (ms) | 25 | 24 | -4 % |
| P-HU01 | Frame p99 (ms) | 150 | 150 | +0 % |
| P-HU01 | Peticiones a la API en todo el recorrido | 1 | 1 | +0 % |
| P-HU01 | Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | = |
| P-HU01 | ANR | 0 | 0 | = |
| P-HU01 | Cierres inesperados | 0 | 0 | = |
| P-HU02 | Tiempo hasta ver «detalle-primera-vez» (ms) | 1.037 | 1.510 | +46 % |
| P-HU02 | Tiempo hasta ver «detalle-segunda-vez» (ms) | 783 | 847 | +8 % |
| P-HU02 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,7 | -6 % |
| P-HU02 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,4 | 28,8 | +1 % |
| P-HU02 | Tiempo de CPU en el hilo `main` (ms) | 4.460 | 4.610 | +3 % |
| P-HU02 | Tiempo de CPU en los demás hilos (ms) | 3.290 | 3.310 | +1 % |
| P-HU02 | Memoria total PSS al final (MB) | 115,4 | 112,8 | -2 % |
| P-HU02 | Java/Kotlin heap al final (MB) | 19,3 | 19,4 | +1 % |
| P-HU02 | Native heap al final (MB) | 15,8 | 16,2 | +3 % |
| P-HU02 | Gráficos al final (MB) | 19,3 | 19,3 | +0 % |
| P-HU02 | RSS pico (MB) | 208,7 | 211,2 | +1 % |
| P-HU02 | Hilos vivos (pico) | 49 | 52 | +6 % |
| P-HU02 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU02 | Frames dibujados | 218 | 219 | +0 % |
| P-HU02 | Frames lentos (> 16 ms) | 31 | 28 | -10 % |
| P-HU02 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU02 | Frames con jank (%) | 10,1 | 9,5 | -6 % |
| P-HU02 | Frame p90 (ms) | 29 | 22 | -24 % |
| P-HU02 | Frame p99 (ms) | 200 | 200 | +0 % |
| P-HU02 | Peticiones a la API en todo el recorrido | 3 | 3 | +0 % |
| P-HU02 | Peticiones a la API desde «primera-apertura» | 2 | 2 | +0 % |
| P-HU02 | Peticiones a la API desde «segunda-apertura» | 0 | 0 | = |
| P-HU02 | ANR | 0 | 0 | = |
| P-HU02 | Cierres inesperados | 0 | 0 | = |
| P-HU03 | Tiempo hasta ver «artistas-primera-vez» (ms) | 1.246 | 1.259 | +1 % |
| P-HU03 | Tiempo hasta ver «artistas-regreso» (ms) | 627 | 681 | +9 % |
| P-HU03 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,9 | -2 % |
| P-HU03 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,8 | 24,4 | -15 % |
| P-HU03 | Tiempo de CPU en el hilo `main` (ms) | 4.970 | 5.070 | +2 % |
| P-HU03 | Tiempo de CPU en los demás hilos (ms) | 3.200 | 3.230 | +1 % |
| P-HU03 | Memoria total PSS al final (MB) | 116,7 | 108,7 | -7 % |
| P-HU03 | Java/Kotlin heap al final (MB) | 21,6 | 16,5 | -24 % |
| P-HU03 | Native heap al final (MB) | 15,6 | 15,7 | +0 % |
| P-HU03 | Gráficos al final (MB) | 18,3 | 18,4 | +0 % |
| P-HU03 | RSS pico (MB) | 208,3 | 209,9 | +1 % |
| P-HU03 | Hilos vivos (pico) | 50 | 53 | +6 % |
| P-HU03 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU03 | Frames dibujados | 234 | 248 | +6 % |
| P-HU03 | Frames lentos (> 16 ms) | 33 | 32 | -3 % |
| P-HU03 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU03 | Frames con jank (%) | 10,3 | 10,8 | +5 % |
| P-HU03 | Frame p90 (ms) | 25 | 24 | -4 % |
| P-HU03 | Frame p99 (ms) | 200 | 200 | +0 % |
| P-HU03 | Peticiones a la API en todo el recorrido | 3 | 3 | +0 % |
| P-HU03 | Peticiones a la API desde «primera-apertura» | 2 | 2 | +0 % |
| P-HU03 | Peticiones a la API desde «regreso» | 0 | 0 | = |
| P-HU03 | ANR | 0 | 0 | = |
| P-HU03 | Cierres inesperados | 0 | 0 | = |
| P-HU05 | Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.105 | 1.196 | +8 % |
| P-HU05 | Tiempo hasta ver «coleccionistas-regreso» (ms) | 997 | 711 | -29 % |
| P-HU05 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,8 | -4 % |
| P-HU05 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,2 | 28,1 | -0 % |
| P-HU05 | Tiempo de CPU en el hilo `main` (ms) | 4.080 | 4.640 | +14 % |
| P-HU05 | Tiempo de CPU en los demás hilos (ms) | 2.860 | 3.150 | +10 % |
| P-HU05 | Memoria total PSS al final (MB) | 113,1 | 112,2 | -1 % |
| P-HU05 | Java/Kotlin heap al final (MB) | 19,5 | 20,4 | +5 % |
| P-HU05 | Native heap al final (MB) | 15,1 | 15,9 | +5 % |
| P-HU05 | Gráficos al final (MB) | 18,3 | 18,4 | +0 % |
| P-HU05 | RSS pico (MB) | 208,2 | 210,5 | +1 % |
| P-HU05 | Hilos vivos (pico) | 45 | 52 | +16 % |
| P-HU05 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU05 | Frames dibujados | 173 | 195 | +13 % |
| P-HU05 | Frames lentos (> 16 ms) | 31 | 39 | +26 % |
| P-HU05 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU05 | Frames con jank (%) | 11,6 | 11,8 | +2 % |
| P-HU05 | Frame p90 (ms) | 32 | 44 | +38 % |
| P-HU05 | Frame p99 (ms) | 250 | 250 | +0 % |
| P-HU05 | Peticiones a la API en todo el recorrido | 2 | 2 | +0 % |
| P-HU05 | Peticiones a la API desde «primera-apertura» | 1 | 1 | +0 % |
| P-HU05 | Peticiones a la API desde «regreso» | 0 | 0 | = |
| P-HU05 | ANR | 0 | 0 | = |
| P-HU05 | Cierres inesperados | 0 | 0 | = |

## Android-SDK-built-for-x86 · despues

### P-ONB (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.632 | 2.597 | 2.639 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.252 | 1.249 | 1.256 |
| Tiempo hasta ver «catalogo» (ms) | 1.028 | 1.009 | 1.033 |
| CPU promedio del recorrido (% del dispositivo) | 10,5 | 10,2 | 10,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 34,1 | 31,5 | 35,2 |
| Tiempo de CPU en el hilo `main` (ms) | 1.720 | 1.710 | 1.770 |
| Tiempo de CPU en los demás hilos (ms) | 3.570 | 3.460 | 3.720 |
| Memoria total PSS al final (MB) | 87,1 | 87,0 | 87,1 |
| Java/Kotlin heap al final (MB) | 20,5 | 20,4 | 20,5 |
| Native heap al final (MB) | 25,1 | 25,1 | 25,2 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 190,6 | 190,3 | 191,1 |
| Hilos vivos (pico) | 37 | 35 | 37 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 116 | 113 | 116 |
| Frames lentos (> 16 ms) | 95 | 94 | 105 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 81,9 | 81,0 | 92,9 |
| Frame p90 (ms) | 53 | 48 | 57 |
| Frame p99 (ms) | 250 | 200 | 300 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 78 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.457 | 1.430 | 1.544 |
| CPU promedio del recorrido (% del dispositivo) | 6,3 | 6,3 | 7,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 33,2 | 32,3 | 33,5 |
| Tiempo de CPU en el hilo `main` (ms) | 3.280 | 3.170 | 3.290 |
| Tiempo de CPU en los demás hilos (ms) | 7.380 | 7.170 | 8.950 |
| Memoria total PSS al final (MB) | 101,4 | 93,7 | 105,6 |
| Java/Kotlin heap al final (MB) | 28,6 | 28,0 | 28,6 |
| Native heap al final (MB) | 30,6 | 23,6 | 34,6 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 204,2 | 196,7 | 207,0 |
| Hilos vivos (pico) | 37 | 36 | 39 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 513 | 509 | 529 |
| Frames lentos (> 16 ms) | 391 | 319 | 426 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 74,9 | 62,9 | 83,4 |
| Frame p90 (ms) | 24 | 22 | 25 |
| Frame p99 (ms) | 89 | 69 | 150 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 117 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 952 | 932 | 1.020 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 796 | 777 | 800 |
| CPU promedio del recorrido (% del dispositivo) | 8,9 | 8,8 | 9,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 36,3 | 33,9 | 36,5 |
| Tiempo de CPU en el hilo `main` (ms) | 2.750 | 2.580 | 3.090 |
| Tiempo de CPU en los demás hilos (ms) | 5.920 | 5.780 | 6.100 |
| Memoria total PSS al final (MB) | 99,1 | 99,1 | 101,2 |
| Java/Kotlin heap al final (MB) | 29,4 | 29,1 | 29,4 |
| Native heap al final (MB) | 27,3 | 27,3 | 29,5 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 201,8 | 201,7 | 203,5 |
| Hilos vivos (pico) | 38 | 38 | 38 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 201 | 199 | 205 |
| Frames lentos (> 16 ms) | 124 | 112 | 165 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 62,3 | 55,7 | 81,0 |
| Frame p90 (ms) | 53 | 48 | 61 |
| Frame p99 (ms) | 250 | 200 | 350 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 83 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 1.108 | 1.101 | 1.244 |
| Tiempo hasta ver «artistas-regreso» (ms) | 756 | 723 | 847 |
| CPU promedio del recorrido (% del dispositivo) | 9,9 | 9,4 | 10,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 36,7 | 35,4 | 42,1 |
| Tiempo de CPU en el hilo `main` (ms) | 3.060 | 2.980 | 3.150 |
| Tiempo de CPU en los demás hilos (ms) | 6.780 | 6.280 | 6.850 |
| Memoria total PSS al final (MB) | 87,3 | 80,9 | 91,8 |
| Java/Kotlin heap al final (MB) | 12,2 | 11,9 | 12,6 |
| Native heap al final (MB) | 32,9 | 25,8 | 37,0 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 210,7 | 203,9 | 217,3 |
| Hilos vivos (pico) | 41 | 40 | 47 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 261 | 256 | 262 |
| Frames lentos (> 16 ms) | 174 | 95 | 206 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 68,0 | 37,0 | 79,3 |
| Frame p90 (ms) | 42 | 28 | 53 |
| Frame p99 (ms) | 250 | 200 | 250 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 50 ms por consulta).

### P-HU04 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-musico» (ms) | 871 | 867 | 880 |
| Tiempo hasta ver «album-desde-artista» (ms) | 1.002 | 967 | 1.121 |
| Tiempo hasta ver «detalle-banda» (ms) | 751 | 748 | 797 |
| CPU promedio del recorrido (% del dispositivo) | 10,1 | 9,7 | 10,9 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 39,8 | 38,4 | 41,1 |
| Tiempo de CPU en el hilo `main` (ms) | 4.450 | 4.210 | 4.530 |
| Tiempo de CPU en los demás hilos (ms) | 9.900 | 9.450 | 11.090 |
| Memoria total PSS al final (MB) | 101,9 | 96,2 | 102,0 |
| Java/Kotlin heap al final (MB) | 15,7 | 13,5 | 15,9 |
| Native heap al final (MB) | 42,9 | 39,5 | 42,9 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 212,1 | 207,2 | 215,9 |
| Hilos vivos (pico) | 43 | 41 | 43 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 489 | 480 | 501 |
| Frames lentos (> 16 ms) | 337 | 332 | 439 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 69,6 | 69,3 | 88,2 |
| Frame p90 (ms) | 28 | 26 | 29 |
| Frame p99 (ms) | 250 | 250 | 300 |
| Peticiones a la API en todo el recorrido | 8 | 8 | 8 |
| Peticiones a la API desde «detalle-musico» | 2 | 2 | 2 |
| Peticiones a la API desde «album-desde-artista» | 2 | 2 | 2 |
| Peticiones a la API desde «detalle-banda» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0, `GET /performerprizes` ×1,0, `GET /musicians/100` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0, `GET /bands/101` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 36 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.000 | 983 | 1.187 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 694 | 693 | 710 |
| CPU promedio del recorrido (% del dispositivo) | 8,5 | 8,2 | 8,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 35,3 | 35,3 | 35,3 |
| Tiempo de CPU en el hilo `main` (ms) | 2.580 | 2.550 | 2.690 |
| Tiempo de CPU en los demás hilos (ms) | 5.030 | 4.760 | 5.210 |
| Memoria total PSS al final (MB) | 92,2 | 92,2 | 92,5 |
| Java/Kotlin heap al final (MB) | 27,1 | 26,9 | 27,5 |
| Native heap al final (MB) | 22,9 | 22,9 | 23,3 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 194,7 | 194,6 | 194,9 |
| Hilos vivos (pico) | 38 | 35 | 38 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 182 | 177 | 187 |
| Frames lentos (> 16 ms) | 141 | 128 | 157 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 78,0 | 68,5 | 88,7 |
| Frame p90 (ms) | 53 | 48 | 57 |
| Frame p99 (ms) | 300 | 300 | 300 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «buscar» | 0 | 0 | 0 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 52 ms por consulta).

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 902 | 872 | 908 |
| Tiempo hasta ver «artista-favorito» (ms) | 921 | 912 | 932 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 753 | 739 | 787 |
| CPU promedio del recorrido (% del dispositivo) | 8,1 | 8,0 | 8,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 35,9 | 35,8 | 37,2 |
| Tiempo de CPU en el hilo `main` (ms) | 3.280 | 3.270 | 3.310 |
| Tiempo de CPU en los demás hilos (ms) | 6.750 | 6.610 | 7.420 |
| Memoria total PSS al final (MB) | 84,6 | 83,0 | 92,4 |
| Java/Kotlin heap al final (MB) | 15,0 | 14,8 | 15,3 |
| Native heap al final (MB) | 26,7 | 25,7 | 34,8 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 205,6 | 205,6 | 212,6 |
| Hilos vivos (pico) | 41 | 40 | 41 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 300 | 290 | 305 |
| Frames lentos (> 16 ms) | 211 | 191 | 254 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 71,3 | 62,6 | 88,6 |
| Frame p90 (ms) | 36 | 36 | 46 |
| Frame p99 (ms) | 200 | 200 | 250 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100` ×2,0, `GET /collectors/100/albums` ×2,0, `GET /performerprizes` ×1,0, `GET /musicians/100` ×1,0, `GET /bands` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 66 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 710 | 695 | 796 |
| Tiempo hasta ver «album-creado» (ms) | 1.908 | 1.905 | 1.920 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.532 | 1.520 | 1.559 |
| CPU promedio del recorrido (% del dispositivo) | 8,9 | 8,5 | 9,2 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 32,4 | 30,6 | 33,2 |
| Tiempo de CPU en el hilo `main` (ms) | 4.710 | 4.610 | 4.790 |
| Tiempo de CPU en los demás hilos (ms) | 9.200 | 8.450 | 9.620 |
| Memoria total PSS al final (MB) | 80,4 | 80,4 | 80,8 |
| Java/Kotlin heap al final (MB) | 15,1 | 14,8 | 15,2 |
| Native heap al final (MB) | 22,3 | 22,1 | 22,3 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 199,6 | 198,7 | 200,1 |
| Hilos vivos (pico) | 38 | 38 | 39 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 533 | 524 | 543 |
| Frames lentos (> 16 ms) | 433 | 264 | 442 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 83,0 | 48,6 | 83,9 |
| Frame p90 (ms) | 46 | 44 | 48 |
| Frame p99 (ms) | 200 | 200 | 200 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/9` ×0,3, `GET /albums/10` ×0,3, `GET /albums/11` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 23 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 990 | 949 | 998 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 1.572 | 1.556 | 1.589 |
| Tiempo hasta ver «track-guardado» (ms) | 1.352 | 1.295 | 1.367 |
| CPU promedio del recorrido (% del dispositivo) | 8,1 | 7,7 | 9,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 32,4 | 30,0 | 33,2 |
| Tiempo de CPU en el hilo `main` (ms) | 4.620 | 4.050 | 4.950 |
| Tiempo de CPU en los demás hilos (ms) | 10.070 | 7.860 | 11.520 |
| Memoria total PSS al final (MB) | 84,5 | 83,4 | 90,0 |
| Java/Kotlin heap al final (MB) | 14,5 | 14,1 | 19,0 |
| Native heap al final (MB) | 26,7 | 26,3 | 27,8 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 204,9 | 204,3 | 205,2 |
| Hilos vivos (pico) | 39 | 38 | 41 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 693 | 429 | 873 |
| Frames lentos (> 16 ms) | 420 | 258 | 522 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 60,1 | 59,9 | 60,8 |
| Frame p90 (ms) | 32 | 25 | 32 |
| Frame p99 (ms) | 200 | 150 | 250 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 28 ms por consulta).

## Android-SDK-built-for-x86-API26 · despues

### P-ONB (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.243 | 2.228 | 2.255 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.319 | 1.261 | 1.319 |
| Tiempo hasta ver «catalogo» (ms) | 1.102 | 1.080 | 1.132 |
| CPU promedio del recorrido (% del dispositivo) | 12,6 | 12,2 | 14,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 41,0 | 38,5 | 42,2 |
| Tiempo de CPU en el hilo `main` (ms) | 1.740 | 1.730 | 1.760 |
| Tiempo de CPU en los demás hilos (ms) | 2.290 | 2.160 | 2.750 |
| Memoria total PSS al final (MB) | 46,8 | 45,8 | 55,4 |
| Java/Kotlin heap al final (MB) | 9,3 | 9,2 | 10,0 |
| Native heap al final (MB) | 7,0 | 6,9 | 15,5 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 122,0 | 121,1 | 129,4 |
| Hilos vivos (pico) | 30 | 28 | 37 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 134 | 127 | 151 |
| Frames lentos (> 16 ms) | 72 | 52 | 76 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 48,3 | 38,8 | 63,0 |
| Frame p90 (ms) | 40 | 30 | 46 |
| Frame p99 (ms) | 150 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 64 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.446 | 1.322 | 2.102 |
| CPU promedio del recorrido (% del dispositivo) | 8,0 | 5,1 | 8,9 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 56,1 | 52,8 | 68,0 |
| Tiempo de CPU en el hilo `main` (ms) | 3.270 | 3.020 | 3.590 |
| Tiempo de CPU en los demás hilos (ms) | 5.160 | 4.030 | 5.930 |
| Memoria total PSS al final (MB) | 51,2 | 50,1 | 51,7 |
| Java/Kotlin heap al final (MB) | 11,6 | 10,2 | 11,9 |
| Native heap al final (MB) | 8,6 | 7,7 | 11,5 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 132,7 | 126,4 | 133,8 |
| Hilos vivos (pico) | 37 | 35 | 43 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 485 | 146 | 508 |
| Frames lentos (> 16 ms) | 160 | 93 | 195 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 39,6 | 33,8 | 66,4 |
| Frame p90 (ms) | 25 | 24 | 77 |
| Frame p99 (ms) | 129 | 113 | 500 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 155 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 779 | 756 | 1.543 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 837 | 811 | 1.094 |
| CPU promedio del recorrido (% del dispositivo) | 12,4 | 11,7 | 12,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 55,5 | 50,0 | 66,4 |
| Tiempo de CPU en el hilo `main` (ms) | 2.930 | 2.900 | 4.970 |
| Tiempo de CPU en los demás hilos (ms) | 4.340 | 3.540 | 6.890 |
| Memoria total PSS al final (MB) | 51,6 | 49,8 | 52,4 |
| Java/Kotlin heap al final (MB) | 10,2 | 9,2 | 13,0 |
| Native heap al final (MB) | 9,2 | 8,3 | 9,7 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 132,8 | 127,5 | 134,0 |
| Hilos vivos (pico) | 38 | 38 | 41 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 202 | 156 | 234 |
| Frames lentos (> 16 ms) | 117 | 88 | 118 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 50,4 | 43,6 | 75,6 |
| Frame p90 (ms) | 46 | 38 | 133 |
| Frame p99 (ms) | 300 | 200 | 450 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 56 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 917 | 838 | 3.212 |
| Tiempo hasta ver «artistas-regreso» (ms) | 681 | 580 | 1.832 |
| CPU promedio del recorrido (% del dispositivo) | 13,2 | 11,7 | 18,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 56,4 | 51,3 | 80,0 |
| Tiempo de CPU en el hilo `main` (ms) | 3.360 | 3.120 | 9.520 |
| Tiempo de CPU en los demás hilos (ms) | 4.760 | 3.520 | 12.440 |
| Memoria total PSS al final (MB) | 49,0 | 48,9 | 50,0 |
| Java/Kotlin heap al final (MB) | 10,0 | 9,2 | 10,8 |
| Native heap al final (MB) | 8,6 | 7,6 | 9,0 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 129,9 | 126,2 | 130,7 |
| Hilos vivos (pico) | 40 | 38 | 42 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 276 | 124 | 327 |
| Frames lentos (> 16 ms) | 112 | 97 | 184 |
| Frames congelados (> 700 ms) | 0 | 0 | 7 |
| Frames con jank (%) | 56,9 | 37,7 | 90,3 |
| Frame p90 (ms) | 36 | 36 | 400 |
| Frame p99 (ms) | 200 | 150 | 850 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 70 ms por consulta).

### P-HU04 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-musico» (ms) | 929 | 916 | 989 |
| Tiempo hasta ver «album-desde-artista» (ms) | 682 | 672 | 756 |
| Tiempo hasta ver «detalle-banda» (ms) | 786 | 773 | 818 |
| CPU promedio del recorrido (% del dispositivo) | 10,7 | 10,7 | 11,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 51,8 | 44,4 | 53,8 |
| Tiempo de CPU en el hilo `main` (ms) | 5.020 | 4.990 | 5.170 |
| Tiempo de CPU en los demás hilos (ms) | 6.110 | 6.040 | 7.270 |
| Memoria total PSS al final (MB) | 54,0 | 53,3 | 54,6 |
| Java/Kotlin heap al final (MB) | 12,2 | 12,1 | 12,5 |
| Native heap al final (MB) | 9,1 | 8,5 | 9,5 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 129,9 | 128,8 | 135,2 |
| Hilos vivos (pico) | 47 | 40 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 760 | 740 | 799 |
| Frames lentos (> 16 ms) | 239 | 236 | 319 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 32,6 | 29,8 | 43,6 |
| Frame p90 (ms) | 27 | 25 | 29 |
| Frame p99 (ms) | 150 | 129 | 150 |
| Peticiones a la API en todo el recorrido | 8 | 8 | 8 |
| Peticiones a la API desde «detalle-musico» | 2 | 2 | 2 |
| Peticiones a la API desde «album-desde-artista» | 2 | 2 | 2 |
| Peticiones a la API desde «detalle-banda» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0, `GET /performerprizes` ×1,0, `GET /musicians/100` ×1,0, `GET /collectors` ×1,0, `GET /albums/100` ×1,0, `GET /bands/101` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 44 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.200 | 809 | 3.386 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 674 | 557 | 2.315 |
| CPU promedio del recorrido (% del dispositivo) | 11,8 | 11,0 | 17,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 51,4 | 48,9 | 71,6 |
| Tiempo de CPU en el hilo `main` (ms) | 2.960 | 2.790 | 11.040 |
| Tiempo de CPU en los demás hilos (ms) | 3.600 | 3.070 | 14.550 |
| Memoria total PSS al final (MB) | 49,3 | 48,9 | 50,7 |
| Java/Kotlin heap al final (MB) | 10,7 | 9,1 | 11,5 |
| Native heap al final (MB) | 8,5 | 7,9 | 8,9 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 127,8 | 124,2 | 131,3 |
| Hilos vivos (pico) | 35 | 31 | 37 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 171 | 61 | 186 |
| Frames lentos (> 16 ms) | 92 | 55 | 140 |
| Frames congelados (> 700 ms) | 0 | 0 | 11 |
| Frames con jank (%) | 82,5 | 50,0 | 90,2 |
| Frame p90 (ms) | 65 | 65 | 800 |
| Frame p99 (ms) | 300 | 250 | 1.650 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «buscar» | 0 | 0 | 0 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 67 ms por consulta).

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 992 | 985 | 1.746 |
| Tiempo hasta ver «artista-favorito» (ms) | 626 | 520 | 1.642 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 809 | 790 | 1.259 |
| CPU promedio del recorrido (% del dispositivo) | 10,7 | 10,1 | 15,2 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 50,5 | 49,5 | 76,7 |
| Tiempo de CPU en el hilo `main` (ms) | 3.490 | 3.470 | 9.110 |
| Tiempo de CPU en los demás hilos (ms) | 4.920 | 4.110 | 13.370 |
| Memoria total PSS al final (MB) | 52,5 | 52,1 | 53,8 |
| Java/Kotlin heap al final (MB) | 11,6 | 10,8 | 11,8 |
| Native heap al final (MB) | 10,4 | 9,3 | 10,4 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 136,2 | 127,9 | 137,6 |
| Hilos vivos (pico) | 38 | 38 | 40 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 288 | 161 | 295 |
| Frames lentos (> 16 ms) | 132 | 121 | 145 |
| Frames congelados (> 700 ms) | 0 | 0 | 3 |
| Frames con jank (%) | 50,9 | 42,7 | 83,2 |
| Frame p90 (ms) | 44 | 38 | 300 |
| Frame p99 (ms) | 200 | 150 | 750 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100` ×2,0, `GET /collectors/100/albums` ×2,0, `GET /bands` ×1,0, `GET /musicians/100` ×1,0, `GET /performerprizes` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 92 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 709 | 669 | 1.855 |
| Tiempo hasta ver «album-creado» (ms) | 1.997 | 1.945 | 3.505 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.821 | 1.783 | 2.607 |
| CPU promedio del recorrido (% del dispositivo) | 10,4 | 9,8 | 10,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 43,8 | 38,7 | 53,7 |
| Tiempo de CPU en el hilo `main` (ms) | 4.760 | 4.650 | 9.400 |
| Tiempo de CPU en los demás hilos (ms) | 5.530 | 5.140 | 11.010 |
| Memoria total PSS al final (MB) | 54,1 | 51,6 | 54,8 |
| Java/Kotlin heap al final (MB) | 11,4 | 10,2 | 11,6 |
| Native heap al final (MB) | 9,9 | 9,2 | 10,2 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 131,3 | 128,7 | 132,6 |
| Hilos vivos (pico) | 38 | 30 | 41 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 532 | 370 | 537 |
| Frames lentos (> 16 ms) | 324 | 273 | 331 |
| Frames congelados (> 700 ms) | 0 | 0 | 1 |
| Frames con jank (%) | 61,8 | 51,8 | 89,5 |
| Frame p90 (ms) | 48 | 44 | 150 |
| Frame p99 (ms) | 200 | 150 | 600 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/12` ×0,3, `GET /albums/13` ×0,3, `GET /albums/14` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 24 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 805 | 802 | 854 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 1.165 | 1.089 | 1.621 |
| Tiempo hasta ver «track-guardado» (ms) | 996 | 957 | 1.001 |
| CPU promedio del recorrido (% del dispositivo) | 9,4 | 9,3 | 9,6 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 44,1 | 43,0 | 44,6 |
| Tiempo de CPU en el hilo `main` (ms) | 4.970 | 4.860 | 5.140 |
| Tiempo de CPU en los demás hilos (ms) | 5.840 | 5.670 | 5.900 |
| Memoria total PSS al final (MB) | 52,9 | 51,6 | 58,1 |
| Java/Kotlin heap al final (MB) | 9,6 | 8,9 | 15,1 |
| Native heap al final (MB) | 9,7 | 9,7 | 10,0 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 134,2 | 131,7 | 134,2 |
| Hilos vivos (pico) | 34 | 33 | 34 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 781 | 766 | 781 |
| Frames lentos (> 16 ms) | 180 | 177 | 191 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 25,5 | 25,0 | 26,0 |
| Frame p90 (ms) | 27 | 26 | 27 |
| Frame p99 (ms) | 150 | 150 | 150 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 39 ms por consulta).

## moto-e20 · despues

### P-ONB (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.887 | 2.885 | 2.887 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.662 | 1.607 | 2.067 |
| Tiempo hasta ver «catalogo» (ms) | 1.294 | 1.272 | 1.331 |
| CPU promedio del recorrido (% del dispositivo) | 8,0 | 7,7 | 8,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,3 | 24,2 | 26,7 |
| Tiempo de CPU en el hilo `main` (ms) | 2.690 | 2.680 | 2.700 |
| Tiempo de CPU en los demás hilos (ms) | 4.050 | 3.990 | 4.130 |
| Memoria total PSS al final (MB) | 93,8 | 87,6 | 95,7 |
| Java/Kotlin heap al final (MB) | 13,6 | 13,3 | 13,8 |
| Native heap al final (MB) | 24,4 | 23,6 | 25,2 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 208,5 | 207,0 | 210,7 |
| Hilos vivos (pico) | 51 | 50 | 51 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 97 | 95 | 103 |
| Frames lentos (> 16 ms) | 28 | 28 | 36 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 29,9 | 29,5 | 35,0 |
| Frame p90 (ms) | 101 | 97 | 105 |
| Frame p99 (ms) | 500 | 450 | 500 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 104 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.895 | 1.873 | 2.106 |
| CPU promedio del recorrido (% del dispositivo) | 5,8 | 5,8 | 5,9 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 33,8 | 32,0 | 34,1 |
| Tiempo de CPU en el hilo `main` (ms) | 5.610 | 5.440 | 5.700 |
| Tiempo de CPU en los demás hilos (ms) | 9.490 | 9.420 | 9.560 |
| Memoria total PSS al final (MB) | 91,0 | 83,7 | 94,3 |
| Java/Kotlin heap al final (MB) | 13,7 | 12,7 | 15,5 |
| Native heap al final (MB) | 19,3 | 19,3 | 25,7 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 204,0 | 199,4 | 204,4 |
| Hilos vivos (pico) | 49 | 48 | 52 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 548 | 528 | 559 |
| Frames lentos (> 16 ms) | 78 | 67 | 97 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 14,8 | 12,2 | 17,5 |
| Frame p90 (ms) | 20 | 20 | 26 |
| Frame p99 (ms) | 150 | 150 | 150 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 120 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.141 | 1.119 | 1.150 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 934 | 917 | 948 |
| CPU promedio del recorrido (% del dispositivo) | 6,7 | 6,2 | 6,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 29,7 | 25,3 | 35,0 |
| Tiempo de CPU en el hilo `main` (ms) | 4.210 | 4.130 | 4.380 |
| Tiempo de CPU en los demás hilos (ms) | 5.730 | 5.040 | 6.230 |
| Memoria total PSS al final (MB) | 90,5 | 86,8 | 91,8 |
| Java/Kotlin heap al final (MB) | 16,3 | 15,2 | 17,5 |
| Native heap al final (MB) | 19,2 | 18,3 | 19,4 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 208,2 | 205,7 | 213,1 |
| Hilos vivos (pico) | 52 | 45 | 52 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 185 | 169 | 207 |
| Frames lentos (> 16 ms) | 44 | 35 | 44 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 21,7 | 20,7 | 24,3 |
| Frame p90 (ms) | 57 | 48 | 57 |
| Frame p99 (ms) | 500 | 400 | 500 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 99 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 1.404 | 1.402 | 1.405 |
| Tiempo hasta ver «artistas-regreso» (ms) | 890 | 875 | 896 |
| CPU promedio del recorrido (% del dispositivo) | 8,1 | 6,9 | 8,2 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 31,4 | 24,2 | 34,8 |
| Tiempo de CPU en el hilo `main` (ms) | 5.010 | 4.760 | 5.080 |
| Tiempo de CPU en los demás hilos (ms) | 7.530 | 6.370 | 7.990 |
| Memoria total PSS al final (MB) | 93,5 | 88,2 | 100,1 |
| Java/Kotlin heap al final (MB) | 15,4 | 13,8 | 16,2 |
| Native heap al final (MB) | 20,5 | 18,4 | 26,8 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 204,0 | 197,1 | 208,9 |
| Hilos vivos (pico) | 52 | 49 | 53 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 251 | 187 | 269 |
| Frames lentos (> 16 ms) | 49 | 42 | 90 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 26,2 | 17,1 | 33,5 |
| Frame p90 (ms) | 53 | 46 | 73 |
| Frame p99 (ms) | 450 | 350 | 500 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 68 ms por consulta).

### P-HU04 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-musico» (ms) | 1.154 | 1.126 | 1.809 |
| Tiempo hasta ver «album-desde-artista» (ms) | 1.077 | 1.073 | 1.138 |
| Tiempo hasta ver «detalle-banda» (ms) | 977 | 957 | 979 |
| CPU promedio del recorrido (% del dispositivo) | 7,7 | 7,7 | 7,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 32,6 | 29,8 | 33,8 |
| Tiempo de CPU en el hilo `main` (ms) | 6.680 | 6.590 | 6.700 |
| Tiempo de CPU en los demás hilos (ms) | 10.870 | 10.600 | 11.090 |
| Memoria total PSS al final (MB) | 100,7 | 99,1 | 101,3 |
| Java/Kotlin heap al final (MB) | 15,4 | 14,8 | 17,5 |
| Native heap al final (MB) | 28,4 | 27,5 | 29,8 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 217,9 | 216,2 | 219,7 |
| Hilos vivos (pico) | 59 | 53 | 59 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 475 | 463 | 491 |
| Frames lentos (> 16 ms) | 116 | 114 | 128 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 24,8 | 24,6 | 26,3 |
| Frame p90 (ms) | 34 | 34 | 34 |
| Frame p99 (ms) | 350 | 300 | 350 |
| Peticiones a la API en todo el recorrido | 8 | 8 | 8 |
| Peticiones a la API desde «detalle-musico» | 2 | 2 | 2 |
| Peticiones a la API desde «album-desde-artista» | 2 | 2 | 2 |
| Peticiones a la API desde «detalle-banda» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /musicians` ×1,0, `GET /bands` ×1,0, `GET /musicians/100` ×1,0, `GET /performerprizes` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0, `GET /bands/101` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 71 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.284 | 1.279 | 1.798 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 874 | 871 | 925 |
| CPU promedio del recorrido (% del dispositivo) | 6,3 | 6,0 | 6,4 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 32,9 | 30,5 | 34,7 |
| Tiempo de CPU en el hilo `main` (ms) | 4.190 | 4.110 | 4.310 |
| Tiempo de CPU en los demás hilos (ms) | 5.670 | 5.010 | 5.720 |
| Memoria total PSS al final (MB) | 90,1 | 85,8 | 93,8 |
| Java/Kotlin heap al final (MB) | 17,4 | 13,9 | 18,2 |
| Native heap al final (MB) | 19,4 | 18,4 | 25,7 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 201,4 | 195,5 | 201,9 |
| Hilos vivos (pico) | 48 | 47 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 165 | 159 | 180 |
| Frames lentos (> 16 ms) | 42 | 35 | 57 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 23,3 | 22,0 | 35,2 |
| Frame p90 (ms) | 69 | 57 | 77 |
| Frame p99 (ms) | 500 | 500 | 500 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «buscar» | 0 | 0 | 0 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 71 ms por consulta).

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.118 | 1.108 | 1.150 |
| Tiempo hasta ver «artista-favorito» (ms) | 1.002 | 962 | 1.023 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 976 | 964 | 983 |
| CPU promedio del recorrido (% del dispositivo) | 6,3 | 6,2 | 6,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 30,4 | 26,2 | 30,5 |
| Tiempo de CPU en el hilo `main` (ms) | 5.340 | 5.330 | 5.410 |
| Tiempo de CPU en los demás hilos (ms) | 7.410 | 7.260 | 7.720 |
| Memoria total PSS al final (MB) | 90,6 | 84,7 | 91,9 |
| Java/Kotlin heap al final (MB) | 13,9 | 13,8 | 14,3 |
| Native heap al final (MB) | 19,8 | 19,6 | 26,4 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 211,0 | 210,7 | 212,0 |
| Hilos vivos (pico) | 49 | 49 | 54 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 256 | 255 | 259 |
| Frames lentos (> 16 ms) | 58 | 53 | 80 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 22,7 | 20,5 | 31,4 |
| Frame p90 (ms) | 53 | 53 | 53 |
| Frame p99 (ms) | 300 | 300 | 400 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100` ×2,0, `GET /collectors/100/albums` ×2,0, `GET /musicians/100` ×1,0, `GET /bands` ×1,0, `GET /performerprizes` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 76 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 926 | 907 | 1.391 |
| Tiempo hasta ver «album-creado» (ms) | 2.132 | 2.129 | 2.140 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.672 | 1.660 | 1.698 |
| CPU promedio del recorrido (% del dispositivo) | 6,0 | 5,9 | 6,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 22,3 | 21,9 | 25,0 |
| Tiempo de CPU en el hilo `main` (ms) | 6.880 | 6.850 | 6.920 |
| Tiempo de CPU en los demás hilos (ms) | 7.870 | 7.620 | 8.330 |
| Memoria total PSS al final (MB) | 89,8 | 86,4 | 92,8 |
| Java/Kotlin heap al final (MB) | 16,7 | 16,5 | 18,2 |
| Native heap al final (MB) | 18,6 | 18,5 | 19,9 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 208,8 | 204,7 | 213,2 |
| Hilos vivos (pico) | 52 | 50 | 52 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 315 | 306 | 322 |
| Frames lentos (> 16 ms) | 167 | 139 | 175 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 55,0 | 45,4 | 55,6 |
| Frame p90 (ms) | 93 | 93 | 97 |
| Frame p99 (ms) | 350 | 300 | 350 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/5` ×0,3, `GET /albums/6` ×0,3, `GET /albums/7` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 41 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 1.209 | 1.157 | 1.223 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 1.753 | 1.751 | 1.766 |
| Tiempo hasta ver «track-guardado» (ms) | 1.605 | 1.580 | 1.773 |
| CPU promedio del recorrido (% del dispositivo) | 5,9 | 5,9 | 6,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 26,3 | 25,9 | 30,0 |
| Tiempo de CPU en el hilo `main` (ms) | 6.190 | 6.140 | 6.210 |
| Tiempo de CPU en los demás hilos (ms) | 8.320 | 8.320 | 8.430 |
| Memoria total PSS al final (MB) | 90,9 | 87,8 | 95,3 |
| Java/Kotlin heap al final (MB) | 16,6 | 16,3 | 17,8 |
| Native heap al final (MB) | 20,8 | 19,8 | 20,9 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 212,2 | 210,6 | 212,8 |
| Hilos vivos (pico) | 51 | 51 | 53 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 392 | 387 | 400 |
| Frames lentos (> 16 ms) | 73 | 68 | 91 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 18,9 | 17,3 | 23,5 |
| Frame p90 (ms) | 38 | 38 | 42 |
| Frame p99 (ms) | 300 | 250 | 300 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 52 ms por consulta).

## moto-g13 · despues

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 1.118 | 1.103 | 1.118 |
| Tiempo hasta ver «artista-favorito» (ms) | 808 | 762 | 812 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 785 | 767 | 816 |
| CPU promedio del recorrido (% del dispositivo) | 7,4 | 7,2 | 7,5 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 52,2 | 48,4 | 53,6 |
| Tiempo de CPU en el hilo `main` (ms) | 7.860 | 7.770 | 7.940 |
| Tiempo de CPU en los demás hilos (ms) | 7.450 | 7.230 | 7.450 |
| Memoria total PSS al final (MB) | 158,0 | 156,4 | 165,4 |
| Java/Kotlin heap al final (MB) | 15,9 | 15,4 | 21,1 |
| Native heap al final (MB) | 22,4 | 21,9 | 23,8 |
| Gráficos al final (MB) | 61,9 | 61,7 | 62,4 |
| RSS pico (MB) | 237,0 | 236,5 | 238,7 |
| Hilos vivos (pico) | 67 | 66 | 67 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 484 | 473 | 489 |
| Frames lentos (> 16 ms) | 85 | 72 | 85 |
| Frames congelados (> 700 ms) | 1 | 1 | 2 |
| Frames con jank (%) | 7,9 | 7,8 | 8,0 |
| Frame p90 (ms) | 32 | 30 | 32 |
| Frame p99 (ms) | 250 | 250 | 250 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100/albums` ×2,0, `GET /collectors/100` ×2,0, `GET /musicians/100` ×1,0, `GET /bands` ×1,0, `GET /performerprizes` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 43 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 1.347 | 1.193 | 1.353 |
| Tiempo hasta ver «album-creado» (ms) | 2.079 | 2.048 | 2.107 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.642 | 1.598 | 1.813 |
| CPU promedio del recorrido (% del dispositivo) | 7,1 | 6,8 | 7,5 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 41,0 | 38,5 | 51,5 |
| Tiempo de CPU en el hilo `main` (ms) | 10.540 | 10.360 | 10.580 |
| Tiempo de CPU en los demás hilos (ms) | 7.940 | 7.880 | 8.410 |
| Memoria total PSS al final (MB) | 171,4 | 169,2 | 172,3 |
| Java/Kotlin heap al final (MB) | 21,6 | 21,0 | 21,9 |
| Native heap al final (MB) | 23,4 | 22,8 | 23,5 |
| Gráficos al final (MB) | 67,1 | 66,8 | 67,3 |
| RSS pico (MB) | 247,2 | 243,2 | 247,3 |
| Hilos vivos (pico) | 67 | 67 | 69 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 600 | 590 | 609 |
| Frames lentos (> 16 ms) | 143 | 129 | 148 |
| Frames congelados (> 700 ms) | 0 | 0 | 2 |
| Frames con jank (%) | 13,5 | 13,1 | 13,7 |
| Frame p90 (ms) | 73 | 69 | 73 |
| Frame p99 (ms) | 300 | 300 | 300 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/3` ×0,3, `GET /albums/4` ×0,3, `GET /albums/5` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 32 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 1.217 | 1.207 | 1.348 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 2.016 | 1.960 | 2.037 |
| Tiempo hasta ver «track-guardado» (ms) | 1.225 | 1.199 | 1.438 |
| CPU promedio del recorrido (% del dispositivo) | 6,7 | 6,5 | 7,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 50,4 | 41,9 | 53,0 |
| Tiempo de CPU en el hilo `main` (ms) | 9.010 | 8.930 | 9.050 |
| Tiempo de CPU en los demás hilos (ms) | 8.510 | 8.440 | 8.680 |
| Memoria total PSS al final (MB) | 170,5 | 169,9 | 172,3 |
| Java/Kotlin heap al final (MB) | 21,2 | 20,5 | 21,4 |
| Native heap al final (MB) | 23,8 | 23,5 | 24,0 |
| Gráficos al final (MB) | 67,3 | 67,3 | 68,3 |
| RSS pico (MB) | 243,2 | 242,7 | 247,3 |
| Hilos vivos (pico) | 73 | 67 | 76 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 697 | 686 | 703 |
| Frames lentos (> 16 ms) | 136 | 133 | 137 |
| Frames congelados (> 700 ms) | 2 | 2 | 2 |
| Frames con jank (%) | 6,7 | 6,3 | 7,5 |
| Frame p90 (ms) | 30 | 29 | 32 |
| Frame p99 (ms) | 300 | 300 | 300 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 45 ms por consulta).

## sdk_gphone64_arm64 · despues

### P-HU06 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 688 | 680 | 707 |
| Tiempo hasta ver «artista-favorito» (ms) | 195 | 193 | 244 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 575 | 558 | 586 |
| CPU promedio del recorrido (% del dispositivo) | 14,3 | 13,6 | 14,4 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 61,9 | 53,3 | 71,3 |
| Tiempo de CPU en el hilo `main` (ms) | 1.280 | 1.240 | 1.290 |
| Tiempo de CPU en los demás hilos (ms) | 1.020 | 970 | 1.030 |
| Memoria total PSS al final (MB) | 111,3 | 109,8 | 115,4 |
| Java/Kotlin heap al final (MB) | 20,3 | 17,5 | 20,3 |
| Native heap al final (MB) | 30,7 | 30,0 | 34,0 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 215,2 | 213,5 | 217,6 |
| Hilos vivos (pico) | 49 | 47 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 314 | 303 | 315 |
| Frames lentos (> 16 ms) | 175 | 168 | 192 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 11,8 | 11,4 | 12,9 |
| Frame p90 (ms) | 32 | 32 | 34 |
| Frame p99 (ms) | 105 | 81 | 150 |
| Peticiones a la API en todo el recorrido | 9 | 9 | 9 |
| Peticiones a la API desde «detalle-primera-vez» | 2 | 2 | 2 |
| Peticiones a la API desde «artista-favorito» | 3 | 3 | 3 |
| Peticiones a la API desde «detalle-segunda-vez» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0, `GET /collectors/100` ×2,0, `GET /collectors/100/albums` ×2,0, `GET /performerprizes` ×1,0, `GET /musicians/100` ×1,0, `GET /bands` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 15 ms por consulta).

### P-HU07 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «formulario» (ms) | 202 | 195 | 207 |
| Tiempo hasta ver «album-creado» (ms) | 1.629 | 1.624 | 1.664 |
| Tiempo hasta ver «detalle-album-creado» (ms) | 1.375 | 1.361 | 1.383 |
| CPU promedio del recorrido (% del dispositivo) | 16,8 | 16,3 | 17,1 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 66,2 | 61,7 | 68,5 |
| Tiempo de CPU en el hilo `main` (ms) | 1.750 | 1.740 | 1.750 |
| Tiempo de CPU en los demás hilos (ms) | 1.090 | 1.080 | 1.100 |
| Memoria total PSS al final (MB) | 108,5 | 107,4 | 108,7 |
| Java/Kotlin heap al final (MB) | 18,7 | 18,5 | 21,2 |
| Native heap al final (MB) | 27,3 | 23,5 | 28,0 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 211,8 | 210,7 | 212,6 |
| Hilos vivos (pico) | 47 | 46 | 48 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 376 | 376 | 381 |
| Frames lentos (> 16 ms) | 210 | 183 | 216 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 16,0 | 15,2 | 17,3 |
| Frame p90 (ms) | 46 | 38 | 48 |
| Frame p99 (ms) | 150 | 133 | 150 |
| Peticiones a la API en todo el recorrido | 4 | 4 | 4 |
| Peticiones a la API desde «formulario» | 0 | 0 | 0 |
| Peticiones a la API desde «crear» | 1 | 1 | 1 |
| Peticiones a la API desde «ver-album» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `POST /albums` ×1,0, `GET /albums/6` ×0,3, `GET /albums/7` ×0,3, `GET /albums/8` ×0,3.
Los tiempos incluyen la ida y vuelta de Appium (mediana 18 ms por consulta).

### P-HU08 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-album» (ms) | 241 | 227 | 320 |
| Tiempo hasta ver «hoja-agregar-track» (ms) | 1.617 | 1.598 | 1.622 |
| Tiempo hasta ver «track-guardado» (ms) | 755 | 683 | 758 |
| CPU promedio del recorrido (% del dispositivo) | 14,9 | 14,5 | 15,4 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 61,5 | 60,3 | 61,9 |
| Tiempo de CPU en el hilo `main` (ms) | 1.480 | 1.440 | 1.510 |
| Tiempo de CPU en los demás hilos (ms) | 1.120 | 1.100 | 1.190 |
| Memoria total PSS al final (MB) | 107,5 | 105,3 | 113,5 |
| Java/Kotlin heap al final (MB) | 19,8 | 18,3 | 20,1 |
| Native heap al final (MB) | 27,0 | 23,1 | 31,3 |
| Gráficos al final (MB) | 0,0 | 0,0 | 0,0 |
| RSS pico (MB) | 215,0 | 211,5 | 220,1 |
| Hilos vivos (pico) | 48 | 46 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 406 | 394 | 407 |
| Frames lentos (> 16 ms) | 222 | 190 | 257 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 10,3 | 10,3 | 13,7 |
| Frame p90 (ms) | 32 | 31 | 34 |
| Frame p99 (ms) | 101 | 101 | 129 |
| Peticiones a la API en todo el recorrido | 5 | 5 | 5 |
| Peticiones a la API desde «detalle-album» | 1 | 1 | 1 |
| Peticiones a la API desde «guardar-track» | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0, `GET /albums/100` ×2,0, `POST /albums/100/tracks` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 21 ms por consulta).

