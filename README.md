# Wine Music

## Equipo

Jhon Edman Linares Rivera - [je.linaresr@uniandes.edu.co](mailto:je.linaresr@uniandes.edu.co)

Nicolas Zambrano Madero - [n.zambrano3@uniandes.edu.co](mailto:n.zambrano3@uniandes.edu.co)

Jesus Enrique Quesada Diaz - [je.quesadad@uniandes.edu.co](mailto:je.quesadad@uniandes.edu.co)

Juan Martin Santos Ayala - [j.santosa@uniandes.edu.co](mailto:j.santosa@uniandes.edu.co)

## Compromisos del grupo:

Horario: El equipo se compromete reunirse todo los Lunes de 7 - 8 pm para realizar una sincronización con los avances o dudas que puede tener alguno de los integrantes.

1. **Acuerdos**: Acordaremos con anterioridad las reuniones que vamos a tener para poder cumplir con los objetivos del curso.
2. **Puntualidad:** Nos conectaremos a la hora acordada o llegaremos 5 minutos antes de cada reunión, y si alguien del equipo no puede asistir avisará al grupo con tiempo de anticipación.
3. **Cumplimiento de entregas internas:** Nos comprometemos a realizar las tareas comprometidas en las sesiones que tenga el equipo.
4. **Comunicación respetuosa y oportuna:** En las sesiones con el equipo mantendremos una conversación respetuosa y respetando el MAAD.
5. **Apoyo mutuo y transparencia:** Si alguien tiene dificultades con su parte, lo comunicará al grupo con tiempo y el equipo buscará apoyarlo; las decisiones importantes se tomarán entre todo el equipo.

Utilizaremos metodología Scrum y llevaremos comunicación continua por medio de un grupo creado en slack por los estudiantes, acordaremos de antemano sesiones para compartir las tareas asignadas o resolver dudas y dificultades que tenga el equipo.

## Actas por Semana

Se adjunta documento con acta y evidencias de reunión del equipo [Documento](https://docs.google.com/document/d/18FE3xbBaMrto_UNlFDcZVraxfUT13bbsTulDw_RbJc0/edit?usp=sharing).

## Diseño de mockups de Wine Music App

* WireFrame: [WireFrame](https://send-sauna-69077229.figma.site/)
* Diseño en Figma: [Figma](https://www.figma.com/design/4CKmBMf3pZgEf5rkBU9XRv/Wireframe-App-Vinilos?node-id=0-1&t=uVtnkMcJR235XoSf-1)

## Gitflow
[GitFlow documentation](./docs/gitflow.md)

## APK

Puedes descargar la aplicación desde ./distributions/app-debug.apk

> [!NOTE]
> Actualmente el apk apunta a entornos locales.

## Cómo construir y ejecutar la app

### Atajo: todo en un comando (backend local)

[`scripts/e2e-local.sh`](scripts/e2e-local.sh) deja la app lista contra un BackVynils local y corre las pruebas E2E:

1. Revisa si el backend responde en `localhost:3000`; si no, lo levanta con Docker (Colima) y carga `sql/data.sql` si la base está vacía.
2. Pone la IP de tu computador en `api.base.url` de `local.properties` (no se sube a git).
3. Compila el APK debug y lo copia en `distributions/`.
4. Corre las pruebas E2E de Compose y Kraken (necesita un emulador o teléfono conectado).

```bash
./scripts/e2e-local.sh                      # todo
./scripts/e2e-local.sh --skip-tests         # solo backend + APK
./scripts/e2e-local.sh --skip-compose       # sin pruebas de Compose
./scripts/e2e-local.sh --skip-kraken        # sin Kraken
./scripts/e2e-local.sh --backend-dir=RUTA   # BackVynils en otra carpeta (por defecto ../BackVynils)
./scripts/e2e-local.sh --release            # APK con el dominio por defecto (Render), para el release en main
```

Si la IP detectada no sirve, pásala a mano: `HOST_IP=192.168.x.x ./scripts/e2e-local.sh`. El script está pensado para macOS (usa `ipconfig getifaddr` y `sed -i ''`). Los pasos manuales están abajo.

### Requisitos

| Herramienta | Versión |
| --- | --- |
| Android Studio | Compatible con Android Gradle Plugin 8.8 *(versión exacta por confirmar)* |
| JDK | 17 para Gradle (Android Studio ya lo incluye) |
| Android SDK | 35 |
| Emulador | Android 7.0 (API 24) o superior |
| Backend | **BackVynils** en Render (por defecto) o local (ver [Backend](#1-backend)) |

### 1. Backend

Por defecto la app usa la instancia de BackVynils del curso en Render, [`https://backvynils-q6yc.onrender.com/`](https://backvynils-q6yc.onrender.com/albums), y no hace falta levantar nada. En el plan gratuito de Render, si el servicio estuvo inactivo, la primera petición puede tardar hasta un minuto.

Para usar un backend local, clona [BackVynils](https://github.com/TheSoftwareDesignLab/BackVynils), sigue su README (con Docker responde en [`http://localhost:3000/albums`](http://localhost:3000/albums)) y cambia el dominio como se explica abajo.

### 2. Cambiar el dominio de la API

Toda petición HTTP de la app sale por un solo dominio, `api.base.url`. Al compilar, [`app/build.gradle.kts`](app/build.gradle.kts) genera con él `BuildConfig.API_BASE_URL`, que es la URL base de Retrofit, y `network_security_config.xml`, que permite HTTP sin cifrar solo hacia ese host cuando el dominio es `http`. **No escribas el dominio en el código ni en ese XML.**

| Quieres cambiarlo… | Dónde | ¿Se sube a git? |
|---|---|---|
| Para todo el equipo (valor por defecto) | `api.base.url` en [`gradle.properties`](gradle.properties) | Sí |
| Solo en tu máquina | `api.base.url` en `local.properties` (tiene prioridad) | No |
| Solo para un comando | `./gradlew installDebug -Papi.base.url=http://…/` (si no está en `local.properties`) | No |

El valor debe empezar por `http://` o `https://` y terminar en `/`; si no, Gradle falla al compilar con un mensaje. Después de cambiarlo, vuelve a compilar e instalar la app.

Valores habituales para `local.properties`, con BackVynils corriendo en Docker en tu computador:

| Dónde corre la app | `api.base.url` | Además |
|---|---|---|
| Emulador | `http://10.0.2.2:3000/` | `10.0.2.2` es el `localhost` del computador visto desde el emulador |
| Teléfono por USB | `http://localhost:3000/` | `adb reverse tcp:3000 tcp:3000`; se pierde al desconectar el cable o reiniciar el teléfono |
| Teléfono por Wi-Fi | `http://<IP del computador>:3000/`, p. ej. `http://192.168.1.6:3000/` | Misma red Wi-Fi. La IP sale con `ipconfig getifaddr en0` (macOS) y puede cambiar si el router reasigna |

Para volver al dominio por defecto, borra la línea `api.base.url` de `local.properties`.

### 3. Compilar y ejecutar

```bash
git clone https://github.com/jhon0520/Andes-Vynils-App.git
cd Andes-Vynils-App
./gradlew assembleDebug
./gradlew installDebug   # con el emulador abierto
```

O abrir la carpeta en **Android Studio** y pulsar **Run ▶** sobre el módulo `app`.

### 4. Pruebas

```bash
./gradlew test                   # pruebas unitarias
./gradlew connectedAndroidTest   # pruebas de interfaz (emulador abierto)
```

## Arquitectura

**MVVM** con **Jetpack Compose**, casos de uso, **Repository** y **Retrofit** como *Service Adapter*, con inyección de dependencias por **Hilt**.

> [!NOTE]
> Los diagramas están en el documento de diseño arquitectónico del equipo.

## Problemas comunes

<details>
<summary><b>La app muestra "Something went wrong"</b></summary>

Revisar que el backend responde y que la URL base es correcta.

</details>

<details>
<summary><b>"Cleartext HTTP traffic not permitted"</b></summary>

El host al que llama la app no es el de `api.base.url`. Revisa `local.properties` y `gradle.properties` y vuelve a compilar: `network_security_config.xml` se genera con ese host.

</details>
