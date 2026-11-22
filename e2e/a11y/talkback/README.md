# Recorrido con TalkBack (A11Y-02)

Recorre la app con TalkBack activado y guarda, paso a paso, **lo que TalkBack dice**. Resultados del sprint 3: [`transcript-moto-e20.txt`](transcript-moto-e20.txt) (recorrido completo, con los hallazgos D-18 a D-20) y [`transcript-moto-e20-fixes.txt`](transcript-moto-e20-fixes.txt) (verificación de las correcciones); análisis en la wiki, [SP03 · Accesibilidad › A11Y-02](../../../../../wiki/SP03‐Accesibilidad#7-recorrido-con-talkback-a11y-02).

## Cómo funciona

- TalkBack **no reconoce los gestos** que envía `adb shell input`, y SELinux no deja escribir directo en la pantalla táctil. Por eso [`talkback_walk.py`](talkback_walk.py) conecta un **teclado virtual** (`adb shell hid -`) y usa los atajos de teclado de TalkBack: **Acción + →/←** (siguiente o anterior elemento) y **Acción + Espacio** (activar). «Acción» es la tecla Meta. El orden del foco y los anuncios son los mismos que con gestos.
- Lo que TalkBack dice se lee de logcat (`Speaking fragment text=…`).
- Para escribir en un campo se usa `adb shell input text` después de activarlo.

## Preparación (una vez, en el teléfono)

1. TalkBack → Configuración → Configuración avanzada → Configuración para desarrolladores → **Nivel de generación de registros: VERBOSE**. TalkBack avisa que el registro puede guardar información personal: al terminar, volver a **ERROR**.
2. BackVynils arriba, la app instalada contra él y `adb reverse tcp:3000 tcp:3000`.

El script activa TalkBack al empezar y lo desactiva al terminar.

## Ejemplo

```bash
python3 e2e/a11y/talkback/talkback_walk.py --serial <serial> --out transcript.txt \
  "note:ONB · Entrar como coleccionista" launch:fresh "to:Coleccionista" click "to:Manolo" click \
  "to:Continuar" click
```

Si un paso `to:` o `back-to:` no encuentra su texto en 40 movimientos, el recorrido se detiene (en lugar de activar lo que tenga el foco) y lo anota en la transcripción.

Pasos disponibles: `note:`, `launch[:fresh]`, `next[:n]`, `prev[:n]`, `to:<texto>`, `back-to:<texto>`, `click`, `back`, `type:<texto>` (`%s` es un espacio), `del:<n>`, `wait:<s>`.

## Limitaciones

- Los mensajes de estado (por ejemplo, «Revisa 2 campos.») entran en la cola de TalkBack detrás de lo que está leyendo: si el siguiente paso mueve el foco antes, TalkBack los descarta, como con un usuario que sigue navegando. Para oírlos, poner un `wait:` largo.
- En un catálogo con muchos álbumes, la barra inferior queda lejos: llegar a ella con `back-to:` desde el inicio de la pantalla.
- La primera vez que se activa TalkBack abre su tutorial, que atrapa el foco: abrir la app encima (`launch`) lo saca.
- `adb shell input text` a veces se salta letras repetidas («zzz» → «z»): usar textos sin letras dobles seguidas.
- Cuando un campo cambia la pantalla mientras se escribe (por ejemplo, la vista previa de la portada en HU07), el foco de TalkBack puede moverse: conviene volver al campo con `to:` antes de escribir el siguiente.
