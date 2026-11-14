# Resultados de perfilamiento

Generado el 2026-11-14 22:50 UTC con `e2e/profiling/summarize.js`. Cada valor es la **mediana** de las corridas.

## Dispositivos

| Modelo | Android (API) | RAM | SoC | Núcleos · frec. máx. | Pantalla |
|---|---|---|---|---|---|
| HONOR HONOR 400 Smart (LGN-NX3) | 15 (API 35) | 5,4 GB | QTI SM6375 | 8 · 2,30 GHz | 720x1610 @ 320 dpi |

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

### P-HU05 (3 corridas)

| Métrica | Mediana | Mín. | Máx. |
|---|---|---|---|
| Tiempo hasta ver «coleccionistas-primera-vez» (ms) | 1.107 | 1.065 | 1.164 |
| Tiempo hasta ver «coleccionistas-regreso» (ms) | 700 | 560 | 1.090 |
| CPU promedio del recorrido (% del dispositivo) | 5,1 | 5,1 | 5,3 |
| CPU pico en ventanas de 0,5 s (% del dispositivo) | 24,0 | 23,9 | 34,1 |
| Tiempo de CPU en el hilo `main` (ms) | 4.050 | 4.020 | 4.180 |
| Tiempo de CPU en los demás hilos (ms) | 2.900 | 2.790 | 3.090 |
| Memoria total PSS al final (MB) | 113,8 | 111,9 | 114,2 |
| Java/Kotlin heap al final (MB) | 19,4 | 19,2 | 19,7 |
| Native heap al final (MB) | 15,6 | 15,5 | 16,2 |
| Gráficos al final (MB) | 18,3 | 18,3 | 18,3 |
| RSS pico (MB) | 208,3 | 208,2 | 208,9 |
| Hilos vivos (pico) | 46 | 45 | 50 |
| Activities vivas al final | 1 | 1 | 1 |
| Frames dibujados | 180 | 179 | 192 |
| Frames lentos (> 16 ms) | 29 | 26 | 44 |
| Frames congelados (> 700 ms) | 0 | 0 | 0 |
| Frames con jank (%) | 11,2 | 10,0 | 11,5 |
| Frame p90 (ms) | 30 | 28 | 30 |
| Frame p99 (ms) | 300 | 200 | 300 |
| Peticiones a la API en todo el recorrido | 2 | 2 | 2 |
| Peticiones a la API desde «primera-apertura» | 1 | 1 | 1 |
| Peticiones a la API desde «regreso» | 0 | 0 | 0 |
| ANR | 0 | 0 | 0 |
| Cierres inesperados | 0 | 0 | 0 |

Peticiones por corrida: `GET /albums` ×1,0, `GET /collectors` ×1,0.
Los tiempos incluyen la ida y vuelta de Appium (mediana 57 ms por consulta).

