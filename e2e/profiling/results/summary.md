# Resultados de perfilamiento

Generado el 2026-11-15 01:45 UTC con `e2e/profiling/summarize.js`. Cada valor es la **mediana** de las corridas.

## Dispositivos

| Modelo | Android (API) | RAM | SoC | Núcleos · frec. máx. | Pantalla |
|---|---|---|---|---|---|
| HONOR HONOR 400 Smart (LGN-NX3) | 15 (API 35) | 5,4 GB | QTI SM6375 | 8 · 2,30 GHz | 720x1610 @ 320 dpi |

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

