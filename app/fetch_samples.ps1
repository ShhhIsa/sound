$raw = Join-Path -Path $PSScriptRoot -ChildPath "src\main\res\raw"
if (!(Test-Path $raw)) { New-Item -ItemType Directory -Path $raw -Force | Out-Null }

Write-Host "Descargando muestras de audio en $raw ..."

$files = @(
    @{url = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_700KB.mp3"; name = "relax_1.mp3"},
    @{url = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_1MG.mp3"; name = "relax_2.mp3"},
    @{url = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_2MG.mp3"; name = "rain.mp3"},
    @{url = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_1MG.mp3"; name = "ocean.mp3"}
)

foreach ($f in $files) {
    $dest = Join-Path $raw $($f.name)
    Write-Host "Downloading $($f.url) -> $dest"
    try {
        Invoke-WebRequest -Uri $f.url -OutFile $dest -UseBasicParsing
    } catch {
        Write-Host "Error downloading $($f.url): $_"
    }
}

Write-Host "Descarga completada."