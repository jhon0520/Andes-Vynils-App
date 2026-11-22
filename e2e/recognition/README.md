# Pruebas de reconocimiento

Las pruebas de reconocimiento recorren la app **sin un guion de casos**, para encontrar cierres, ANR y pantallas inesperadas que las pruebas E2E no cubren. Hay dos tipos:

| Tipo | Herramienta | Qué hace |
|---|---|---|
| **Aleatorias** | `adb shell monkey` (Android) | Envía eventos al azar (toques, gestos, teclas, cambios de app) solo a la app, con una semilla fija para poder repetir la corrida. |
| **Exploración sistemática** | [`explore.py`](explore.py) (adb + uiautomator) | Toca **cada elemento tocable de cada pantalla** que alcanza, una vez por pantalla, y guarda una captura de cada pantalla nueva. Para volver a una pantalla reinicia la app y repite los toques que llevan a ella, así el recorrido es determinista. |

Los resultados están en [`results/`](results) y el análisis en la wiki: [SP03 · Pruebas de reconocimiento](../../../../wiki/SP03‐Pruebas‐de‐reconocimiento).

## Requisitos

- BackVynils arriba en `localhost:3000` y la app compilada contra él (`api.base.url=http://localhost:3000/` en `local.properties`). En cada dispositivo: `adb -s <serial> reverse tcp:3000 tcp:3000`.
- La app instalada: `adb -s <serial> install -r -t app/build/outputs/apk/debug/app-debug.apk`.
- Python 3 (sin librerías extra) para la exploración sistemática.

## Aleatorias (monkey)

```bash
for seed in 1101 2202 3303; do
  adb -s <serial> shell am force-stop com.dev.wine.music.app
  adb -s <serial> logcat -c
  adb -s <serial> shell monkey -p com.dev.wine.music.app -s $seed --throttle 250 \
    --pct-syskeys 0 --pct-appswitch 5 --ignore-security-exceptions -v -v 3000 \
    > results/random/monkey-<dispositivo>-seed$seed.txt
  adb -s <serial> logcat -d -b crash > results/random/monkey-<dispositivo>-seed$seed-crash.txt
done
```

- `-p` limita los eventos a la app.
- `--pct-syskeys 0` evita las teclas de sistema (volumen, llamada).
- `--throttle 250` deja 250 ms entre eventos para que la app alcance a cargar datos del backend.

Una corrida está **en verde** si termina con `// Monkey finished` y el archivo `-crash.txt` está vacío. Si monkey encuentra un cierre o un ANR, se detiene y escribe `// CRASH` o `// NOT RESPONDING` con la traza.

Solo se corre en emulador: en un teléfono personal, monkey puede bajar la barra de notificaciones y cambiar ajustes del sistema.

## Exploración sistemática

```bash
python3 e2e/recognition/explore.py --serial <serial> --out e2e/recognition/results/systematic/<dispositivo> --max-actions 200
```

Deja en la carpeta de salida:

- `exploration.json`: dispositivo, acciones hechas, pantallas encontradas (con el camino de toques que lleva a cada una), cierres o ANR con su traza, y la bitácora de cada toque.
- `screens/Sxx.png`: una captura por pantalla distinta. En el repositorio quedan convertidas a JPEG de 540 px (`sips -s format jpeg -Z 540`) para que pesen menos.

Reglas del recorrido:

- **Cada pantalla se identifica por sus `testTag`** (expuestos como `resource-id` con `testTagsAsResourceId`), sin los números de los ítems. Por eso el detalle de cualquier álbum cuenta como la misma pantalla.
- **De los ítems de una lista se tocan solo los 2 primeros** (`MAX_PER_FAMILY`), para que una lista larga no se coma el recorrido.
- **En los campos de texto se escribe «e2e»** y se intenta cerrar el teclado con ESC. En Gboard del emulador ESC no lo cierra, así que el teclado puede tapar la barra inferior en el siguiente toque.
- **No se tocan los botones que escriben en el backend**: «Crear álbum» del formulario (`album_create_submit`) y «Agregar track» de la hoja (`add_track_submit`). Así la exploración no deja datos en la base.
- **Si un toque habilita acciones nuevas en la misma pantalla**, también se exploran. Por ejemplo, elegir un coleccionista habilita «Continuar».
