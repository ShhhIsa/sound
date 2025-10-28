````markdown
name=README.md
```markdown
# Sound (Noise detector + sound library)

Proyecto listo para importar en Android Studio.

Instrucciones rápidas:
1. Clona o descarga el repo.
2. Ejecuta app/fetch_samples.ps1 (en Windows PowerShell) para descargar audios de ejemplo a app/src/main/res/raw/
   - Ejecuta desde la raíz del repo: .\app\fetch_samples.ps1
3. Abre Android Studio > File > Open > selecciona la carpeta sound
4. Gradle sincronizará. Ejecuta en un dispositivo físico y concede permiso al micrófono.

Notas:
- La medición de dB es aproximada; ajusta noiseThresholdDb en MainActivity.kt.
- Si tienes tus propios audios, colócalos en app/src/main/res/raw/ y nómbralos relax_1.mp3, relax_2.mp3, rain.mp3, ocean.mp3
```
````