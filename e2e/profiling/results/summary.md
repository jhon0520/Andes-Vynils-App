# Resultados de perfilamiento

Generado el 2026-11-22 01:58 UTC con `e2e/profiling/summarize.js`. Cada valor es la **mediana** de las corridas.

## Dispositivos

| Modelo | Android (API) | RAM | SoC | Núcleos · frec. máx. | Pantalla |
|---|---|---|---|---|---|
| HONOR HONOR 400 Smart (LGN-NX3) | 15 (API 35) | 5,4 GB | QTI SM6375 | 8 · 2,30 GHz | 720x1610 @ 320 dpi |
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
| Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.790 | 2.767 | 2.801 |
| Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.169 | 1.165 | 1.208 |
| Tiempo hasta ver «catalogo» (ms) | 1.633 | 1.353 | 1.731 |
| CPU promedio del recorrido (% del dispositivo) | 5,6 | 5,5 | 5,6 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 23,5 | 22,7 | 23,8 |
| Tiempo de CPU en el hilo `main` (ms) | 2.650 | 2.600 | 2.670 |
| Tiempo de CPU en los demás hilos (ms) | 2.230 | 2.230 | 2.250 |
| Memoria total PSS al final (MB) | 114,0 | 113,7 | 114,8 |
| Java/Kotlin heap al final (MB) | 16,3 | 16,0 | 17,1 |
| Native heap al final (MB) | 18,9 | 18,8 | 19,1 |
| Gráficos al final (MB) | 18,5 | 18,5 | 18,5 |
| RSS pico (MB) | 204,2 | 203,4 | 205,0 |
| Hilos vivos (pico) | 47 | 45 | 49 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 117 | 115 | 123 |
| Frames lentos (> 16 ms) | 20 | 19 | 20 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 12,2 | 11,1 | 14,8 |
| Frame p90 (ms) | 46 | 46 | 53 |
| Frame p99 (ms) | 250 | 250 | 250 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /collectors` ×1,0, `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 54 ms por consulta).

### P-HU01 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «catalogo» (ms) | 1.604 | 1.430 | 2.103 |
| CPU promedio del recorrido (% del dispositivo) | 3,6 | 3,6 | 3,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 27,8 | 18,3 | 30,8 |
| Tiempo de CPU en el hilo `main` (ms) | 4.540 | 4.530 | 4.620 |
| Tiempo de CPU en los demás hilos (ms) | 3.670 | 3.660 | 3.760 |
| Memoria total PSS al final (MB) | 115,1 | 110,3 | 119,3 |
| Java/Kotlin heap al final (MB) | 16,8 | 15,6 | 21,2 |
| Native heap al final (MB) | 18,8 | 15,2 | 19,0 |
| Gráficos al final (MB) | 18,7 | 18,7 | 18,8 |
| RSS pico (MB) | 208,8 | 207,5 | 209,4 |
| Hilos vivos (pico) | 49 | 47 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 402 | 401 | 443 |
| Frames lentos (> 16 ms) | 56 | 53 | 80 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 3,8 | 3,7 | 4,0 |
| Frame p90 (ms) | 24 | 24 | 25 |
| Frame p99 (ms) | 150 | 150 | 150 |
| Peticiones a la API en todo el recorrido | 1 | 1 | 1 |
| Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 84 ms por consulta).

### P-HU02 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-primera-vez» (ms) | 969 | 817 | 1.052 |
| Tiempo hasta ver «detalle-segunda-vez» (ms) | 799 | 785 | 815 |
| CPU promedio del recorrido (% del dispositivo) | 4,7 | 4,7 | 4,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 25,1 | 23,0 | 30,4 |
| Tiempo de CPU en el hilo `main` (ms) | 4.600 | 4.450 | 4.710 |
| Tiempo de CPU en los demás hilos (ms) | 3.220 | 3.180 | 3.260 |
| Memoria total PSS al final (MB) | 115,7 | 115,5 | 116,6 |
| Java/Kotlin heap al final (MB) | 19,3 | 18,9 | 19,3 |
| Native heap al final (MB) | 16,2 | 16,0 | 17,1 |
| Gráficos al final (MB) | 19,0 | 19,0 | 19,3 |
| RSS pico (MB) | 208,7 | 208,2 | 208,9 |
| Hilos vivos (pico) | 50 | 50 | 51 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 205 | 201 | 208 |
| Frames lentos (> 16 ms) | 36 | 30 | 52 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 12,9 | 11,7 | 13,0 |
| Frame p90 (ms) | 40 | 30 | 48 |
| Frame p99 (ms) | 200 | 200 | 200 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «segunda-apertura» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /albums/100` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 60 ms por consulta).

### P-HU03 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «artistas-primera-vez» (ms) | 1.239 | 1.196 | 1.264 |
| Tiempo hasta ver «artistas-regreso» (ms) | 995 | 585 | 1.055 |
| CPU promedio del recorrido (% del dispositivo) | 4,8 | 4,6 | 4,8 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 26,4 | 24,5 | 26,5 |
| Tiempo de CPU en el hilo `main` (ms) | 5.130 | 5.040 | 5.210 |
| Tiempo de CPU en los demás hilos (ms) | 3.280 | 3.120 | 3.350 |
| Memoria total PSS al final (MB) | 116,1 | 115,8 | 117,5 |
| Java/Kotlin heap al final (MB) | 21,5 | 21,0 | 22,4 |
| Native heap al final (MB) | 15,5 | 15,5 | 15,8 |
| Gráficos al final (MB) | 18,3 | 18,3 | 18,4 |
| RSS pico (MB) | 209,9 | 208,6 | 209,9 |
| Hilos vivos (pico) | 51 | 48 | 54 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 236 | 236 | 245 |
| Frames lentos (> 16 ms) | 32 | 31 | 33 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 11,0 | 10,2 | 11,9 |
| Frame p90 (ms) | 25 | 22 | 28 |
| Frame p99 (ms) | 200 | 200 | 200 |
| Peticiones a la API en todo el recorrido | 3 | 3 | 3 |
| Peticiones a la API desde «primera-apertura» | 2 | 2 | 2 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /bands` ×1,0, `GET /musicians` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 57 ms por consulta).

### P-HU04 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «detalle-musico» (ms) | 989 | 955 | 1.006 |
| Tiempo hasta ver «album-desde-artista» (ms) | 791 | 752 | 1.043 |
| Tiempo hasta ver «detalle-banda» (ms) | 834 | 801 | 843 |
| CPU promedio del recorrido (% del dispositivo) | 4,6 | 4,5 | 4,7 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 23,6 | 21,9 | 25,5 |
| Tiempo de CPU en el hilo `main` (ms) | 5.910 | 5.910 | 5.950 |
| Tiempo de CPU en los demás hilos (ms) | 4.070 | 3.950 | 4.090 |
| Memoria total PSS al final (MB) | 115,9 | 114,8 | 116,1 |
| Java/Kotlin heap al final (MB) | 16,6 | 16,3 | 16,9 |
| Native heap al final (MB) | 16,9 | 16,8 | 17,1 |
| Gráficos al final (MB) | 19,4 | 19,4 | 19,5 |
| RSS pico (MB) | 213,0 | 212,7 | 214,2 |
| Hilos vivos (pico) | 52 | 50 | 54 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 334 | 329 | 341 |
| Frames lentos (> 16 ms) | 46 | 36 | 50 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 9,4 | 8,1 | 9,4 |
| Frame p90 (ms) | 25 | 20 | 25 |
| Frame p99 (ms) | 150 | 150 | 200 |
| Peticiones a la API en todo el recorrido | 8 | 8 | 8 |
| Peticiones a la API desde «detalle-musico» | 2 | 2 | 2 |
| Peticiones a la API desde «album-desde-artista» | 2 | 2 | 2 |
| Peticiones a la API desde «detalle-banda» | 1 | 1 | 1 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /musicians` ×1,0, `GET /bands` ×1,0, `GET /musicians/100` ×1,0, `GET /performerprizes` ×1,0, `GET /collectors` ×1,0, `GET /albums/100` ×1,0, `GET /bands/101` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 51 ms por consulta).

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.223 | 1.200 | 1.233 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 969 | 586 | 1.190 |
| CPU promedio del recorrido (% del dispositivo) | 4,8 | 4,5 | 5,0 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 26,8 | 22,7 | 27,6 |
| Tiempo de CPU en el hilo `main` (ms) | 4.660 | 4.570 | 4.670 |
| Tiempo de CPU en los demás hilos (ms) | 3.160 | 3.130 | 3.190 |
| Memoria total PSS al final (MB) | 111,8 | 111,4 | 112,8 |
| Java/Kotlin heap al final (MB) | 19,8 | 19,5 | 20,5 |
| Native heap al final (MB) | 15,6 | 15,5 | 15,6 |
| Gráficos al final (MB) | 18,4 | 18,4 | 18,4 |
| RSS pico (MB) | 213,0 | 210,1 | 215,3 |
| Hilos vivos (pico) | 50 | 48 | 52 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 193 | 192 | 199 |
| Frames lentos (> 16 ms) | 41 | 41 | 50 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 13,1 | 11,9 | 13,5 |
| Frame p90 (ms) | 42 | 38 | 42 |
| Frame p99 (ms) | 250 | 200 | 250 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «buscar» | 0 | 0 | 0 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 47 ms por consulta).

## LGN-NX3 · antes vs. después

| Escenario | Métrica | Antes | Después | Cambio |
|---|---|---|---|---|
| P-ONB | Arranque en frío hasta el primer frame (ms, `am start -W`) | 2.766 | 2.790 | +1 % |
| P-ONB | Tiempo hasta ver «lista-de-coleccionistas» (ms) | 1.142 | 1.169 | +2 % |
| P-ONB | Tiempo hasta ver «catalogo» (ms) | 1.889 | 1.633 | -14 % |
| P-ONB | CPU promedio del recorrido (% del dispositivo) | 5,4 | 5,6 | +4 % |
| P-ONB | CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,8 | 23,5 | -5 % |
| P-ONB | Tiempo de CPU en el hilo `main` (ms) | 2.650 | 2.650 | +0 % |
| P-ONB | Tiempo de CPU en los demás hilos (ms) | 2.280 | 2.230 | -2 % |
| P-ONB | Memoria total PSS al final (MB) | 114,2 | 114,0 | -0 % |
| P-ONB | Java/Kotlin heap al final (MB) | 16,4 | 16,3 | -1 % |
| P-ONB | Native heap al final (MB) | 18,7 | 18,9 | +1 % |
| P-ONB | Gráficos al final (MB) | 18,5 | 18,5 | -0 % |
| P-ONB | RSS pico (MB) | 204,4 | 204,2 | -0 % |
| P-ONB | Hilos vivos (pico) | 46 | 47 | +2 % |
| P-ONB | Activities vivas al final | 1 | 1 | +0 % |
| P-ONB | Frames dibujados | 120 | 117 | -3 % |
| P-ONB | Frames lentos (> 16 ms) | 29 | 20 | -31 % |
| P-ONB | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-ONB | Frames con jank (%) | 12,6 | 12,2 | -3 % |
| P-ONB | Frame p90 (ms) | 48 | 46 | -4 % |
| P-ONB | Frame p99 (ms) | 300 | 250 | -17 % |
| P-ONB | Peticiones a la API en todo el recorrido | 2 | 2 | +0 % |
| P-ONB | ANR | 0 | 0 | = |
| P-ONB | Cierres inesperados | 0 | 0 | = |
| P-HU01 | Tiempo hasta ver «catalogo» (ms) | 1.487 | 1.604 | +8 % |
| P-HU01 | CPU promedio del recorrido (% del dispositivo) | 3,6 | 3,6 | +0 % |
| P-HU01 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 30,6 | 27,8 | -9 % |
| P-HU01 | Tiempo de CPU en el hilo `main` (ms) | 4.590 | 4.540 | -1 % |
| P-HU01 | Tiempo de CPU en los demás hilos (ms) | 3.740 | 3.670 | -2 % |
| P-HU01 | Memoria total PSS al final (MB) | 115,4 | 115,1 | -0 % |
| P-HU01 | Java/Kotlin heap al final (MB) | 16,8 | 16,8 | +0 % |
| P-HU01 | Native heap al final (MB) | 19,1 | 18,8 | -2 % |
| P-HU01 | Gráficos al final (MB) | 18,8 | 18,7 | -0 % |
| P-HU01 | RSS pico (MB) | 209,4 | 208,8 | -0 % |
| P-HU01 | Hilos vivos (pico) | 48 | 49 | +2 % |
| P-HU01 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU01 | Frames dibujados | 405 | 402 | -1 % |
| P-HU01 | Frames lentos (> 16 ms) | 71 | 56 | -21 % |
| P-HU01 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU01 | Frames con jank (%) | 4,0 | 3,8 | -3 % |
| P-HU01 | Frame p90 (ms) | 25 | 24 | -4 % |
| P-HU01 | Frame p99 (ms) | 150 | 150 | +0 % |
| P-HU01 | Peticiones a la API en todo el recorrido | 1 | 1 | +0 % |
| P-HU01 | Peticiones a la API desde «buscar-y-filtrar» | 0 | 0 | = |
| P-HU01 | ANR | 0 | 0 | = |
| P-HU01 | Cierres inesperados | 0 | 0 | = |
| P-HU02 | Tiempo hasta ver «detalle-primera-vez» (ms) | 1.037 | 969 | -7 % |
| P-HU02 | Tiempo hasta ver «detalle-segunda-vez» (ms) | 783 | 799 | +2 % |
| P-HU02 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,7 | -6 % |
| P-HU02 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,4 | 25,1 | -12 % |
| P-HU02 | Tiempo de CPU en el hilo `main` (ms) | 4.460 | 4.600 | +3 % |
| P-HU02 | Tiempo de CPU en los demás hilos (ms) | 3.290 | 3.220 | -2 % |
| P-HU02 | Memoria total PSS al final (MB) | 115,4 | 115,7 | +0 % |
| P-HU02 | Java/Kotlin heap al final (MB) | 19,3 | 19,3 | +0 % |
| P-HU02 | Native heap al final (MB) | 15,8 | 16,2 | +3 % |
| P-HU02 | Gráficos al final (MB) | 19,3 | 19,0 | -2 % |
| P-HU02 | RSS pico (MB) | 208,7 | 208,7 | +0 % |
| P-HU02 | Hilos vivos (pico) | 49 | 50 | +2 % |
| P-HU02 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU02 | Frames dibujados | 218 | 205 | -6 % |
| P-HU02 | Frames lentos (> 16 ms) | 31 | 36 | +16 % |
| P-HU02 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU02 | Frames con jank (%) | 10,1 | 12,9 | +29 % |
| P-HU02 | Frame p90 (ms) | 29 | 40 | +38 % |
| P-HU02 | Frame p99 (ms) | 200 | 200 | +0 % |
| P-HU02 | Peticiones a la API en todo el recorrido | 3 | 3 | +0 % |
| P-HU02 | Peticiones a la API desde «primera-apertura» | 2 | 2 | +0 % |
| P-HU02 | Peticiones a la API desde «segunda-apertura» | 0 | 0 | = |
| P-HU02 | ANR | 0 | 0 | = |
| P-HU02 | Cierres inesperados | 0 | 0 | = |
| P-HU03 | Tiempo hasta ver «artistas-primera-vez» (ms) | 1.246 | 1.239 | -1 % |
| P-HU03 | Tiempo hasta ver «artistas-regreso» (ms) | 627 | 995 | +59 % |
| P-HU03 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,8 | -4 % |
| P-HU03 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,8 | 26,4 | -8 % |
| P-HU03 | Tiempo de CPU en el hilo `main` (ms) | 4.970 | 5.130 | +3 % |
| P-HU03 | Tiempo de CPU en los demás hilos (ms) | 3.200 | 3.280 | +3 % |
| P-HU03 | Memoria total PSS al final (MB) | 116,7 | 116,1 | -1 % |
| P-HU03 | Java/Kotlin heap al final (MB) | 21,6 | 21,5 | -0 % |
| P-HU03 | Native heap al final (MB) | 15,6 | 15,5 | -1 % |
| P-HU03 | Gráficos al final (MB) | 18,3 | 18,3 | +0 % |
| P-HU03 | RSS pico (MB) | 208,3 | 209,9 | +1 % |
| P-HU03 | Hilos vivos (pico) | 50 | 51 | +2 % |
| P-HU03 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU03 | Frames dibujados | 234 | 236 | +1 % |
| P-HU03 | Frames lentos (> 16 ms) | 33 | 32 | -3 % |
| P-HU03 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU03 | Frames con jank (%) | 10,3 | 11,0 | +7 % |
| P-HU03 | Frame p90 (ms) | 25 | 25 | +0 % |
| P-HU03 | Frame p99 (ms) | 200 | 200 | +0 % |
| P-HU03 | Peticiones a la API en todo el recorrido | 3 | 3 | +0 % |
| P-HU03 | Peticiones a la API desde «primera-apertura» | 2 | 2 | +0 % |
| P-HU03 | Peticiones a la API desde «regreso» | 0 | 0 | = |
| P-HU03 | ANR | 0 | 0 | = |
| P-HU03 | Cierres inesperados | 0 | 0 | = |
| P-HU05 | Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.105 | 1.223 | +11 % |
| P-HU05 | Tiempo hasta ver «coleccionistas-regreso» (ms) | 997 | 969 | -3 % |
| P-HU05 | CPU promedio del recorrido (% del dispositivo) | 5,0 | 4,8 | -4 % |
| P-HU05 | CPU pico en ventanas de 0,5 s (% del dispositivo) | 28,2 | 26,8 | -5 % |
| P-HU05 | Tiempo de CPU en el hilo `main` (ms) | 4.080 | 4.660 | +14 % |
| P-HU05 | Tiempo de CPU en los demás hilos (ms) | 2.860 | 3.160 | +10 % |
| P-HU05 | Memoria total PSS al final (MB) | 113,1 | 111,8 | -1 % |
| P-HU05 | Java/Kotlin heap al final (MB) | 19,5 | 19,8 | +1 % |
| P-HU05 | Native heap al final (MB) | 15,1 | 15,6 | +3 % |
| P-HU05 | Gráficos al final (MB) | 18,3 | 18,4 | +1 % |
| P-HU05 | RSS pico (MB) | 208,2 | 213,0 | +2 % |
| P-HU05 | Hilos vivos (pico) | 45 | 50 | +11 % |
| P-HU05 | Activities vivas al final | 1 | 1 | +0 % |
| P-HU05 | Frames dibujados | 173 | 193 | +12 % |
| P-HU05 | Frames lentos (> 16 ms) | 31 | 41 | +32 % |
| P-HU05 | Frames congelados (> 700 ms) | 0 | 0 | = |
| P-HU05 | Frames con jank (%) | 11,6 | 13,1 | +13 % |
| P-HU05 | Frame p90 (ms) | 32 | 42 | +31 % |
| P-HU05 | Frame p99 (ms) | 250 | 250 | +0 % |
| P-HU05 | Peticiones a la API en todo el recorrido | 2 | 2 | +0 % |
| P-HU05 | Peticiones a la API desde «primera-apertura» | 1 | 1 | +0 % |
| P-HU05 | Peticiones a la API desde «regreso» | 0 | 0 | = |
| P-HU05 | ANR | 0 | 0 | = |
| P-HU05 | Cierres inesperados | 0 | 0 | = |

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

