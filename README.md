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


## Cómo construir y ejecutar la app

### Requisitos

| Herramienta | Versión |
| --- | --- |
| Android Studio | Compatible con Android Gradle Plugin 8.8 *(versión exacta por confirmar)* |
| JDK | 17 para Gradle (Android Studio ya lo incluye) |
| Android SDK | 35 |
| Emulador | Android 7.0 (API 24) o superior |
| Backend | **BackVynils** corriendo localmente (ver su propio README) |

### 1. Levantar el backend

1. Clonar [BackVynils](https://github.com/TheSoftwareDesignLab/BackVynils) y seguir las instrucciones de su README.
2. Confirmar que responde en [`http://localhost:3000/albums`](http://localhost:3000/albums).

### 2. Revisar la URL de la API

La URL está en [`app/build.gradle.kts`](app/build.gradle.kts):

```kotlin
buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000/\"")
```

> [!NOTE]
> `10.0.2.2` es el computador visto desde el emulador.

> [!TIP]
> En un **teléfono físico**, cambia la URL por la IP del computador en la red local y agrega esa IP en
> [`app/src/main/res/xml/network_security_config.xml`](app/src/main/res/xml/network_security_config.xml).

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

La IP usada no está en `network_security_config.xml`.

</details>
