param(
    [string]$CobblemonJar = (Join-Path $PSScriptRoot '..\libs\Cobblemon-fabric-1.7.3+1.21.1.jar'),
    [string]$OutputDirectory = (Join-Path $PSScriptRoot '..\src\main\resources\assets\cobblemon_chromatics\textures\gui\summary'),
    [string]$PreviewPath = (Join-Path $PSScriptRoot '..\outputs\icon-design\status-icons-preview.png')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem

function Set-PixelGroup {
    param(
        [Drawing.Bitmap]$Bitmap,
        [Drawing.Color]$Color,
        [int[][]]$Pixels
    )
    foreach ($pixel in $Pixels) {
        $Bitmap.SetPixel($pixel[0], $pixel[1], $Color)
    }
}

$output = [IO.Path]::GetFullPath($OutputDirectory)
$preview = [IO.Path]::GetFullPath($PreviewPath)
[IO.Directory]::CreateDirectory($output) | Out-Null
[IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName($preview)) | Out-Null

$zip = [IO.Compression.ZipFile]::OpenRead([IO.Path]::GetFullPath($CobblemonJar))
try {
    $entry = $zip.Entries | Where-Object FullName -eq 'assets/cobblemon/textures/gui/summary/icon_shiny.png'
    if ($null -eq $entry) {
        throw 'Cobblemon summary shiny icon was not found.'
    }
    $stream = $entry.Open()
    try {
        $reference = [Drawing.Bitmap]::new($stream)
    } finally {
        $stream.Dispose()
    }
} finally {
    $zip.Dispose()
}

try {
    if ($reference.Width -ne 16 -or $reference.Height -ne 16) {
        throw "Unexpected shiny icon size: $($reference.Width)x$($reference.Height)"
    }

    $cyan = [Drawing.Color]::FromArgb(255, 69, 214, 208)
    $magenta = [Drawing.Color]::FromArgb(255, 216, 91, 213)
    $gold = [Drawing.Color]::FromArgb(255, 244, 201, 93)
    $violet = [Drawing.Color]::FromArgb(255, 112, 103, 232)

    $combined = [Drawing.Bitmap]::new(16, 16, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y = 0; $y -lt 16; $y++) {
        for ($x = 0; $x -lt 16; $x++) {
            $alpha = $reference.GetPixel($x, $y).A
            if ($alpha -gt 0) {
                $centerX = if ($x -le 8) { 5 } else { 11 }
                $centerY = if ($x -le 8) { 6 } else { 12 }
                $base = if ($y -le $centerY) {
                    if ($x -le $centerX) { $cyan } else { $magenta }
                } else {
                    if ($x -le $centerX) { $gold } else { $violet }
                }
                $combined.SetPixel($x, $y, [Drawing.Color]::FromArgb($alpha, $base.R, $base.G, $base.B))
            }
        }
    }

    $chromatic = [Drawing.Bitmap]::new(16, 16, [Drawing.Imaging.PixelFormat]::Format32bppArgb)

    Set-PixelGroup $chromatic $cyan @(
        @(6,2), @(7,2), @(5,3), @(6,3), @(4,4), @(5,4),
        @(3,5), @(4,5), @(2,6), @(3,6), @(2,7), @(3,7)
    )
    Set-PixelGroup $chromatic $magenta @(
        @(8,2), @(8,3), @(9,3), @(9,4), @(10,4), @(10,5),
        @(11,5), @(11,6), @(12,6), @(11,7), @(12,7)
    )
    Set-PixelGroup $chromatic $gold @(
        @(2,8), @(3,8), @(3,9), @(4,9), @(4,10), @(5,10),
        @(5,11), @(6,11), @(6,12)
    )
    Set-PixelGroup $chromatic $violet @(
        @(11,8), @(12,8), @(10,9), @(11,9), @(9,10), @(10,10),
        @(8,11), @(9,11), @(7,12), @(8,12)
    )

    $chromatic.SetPixel(7, 1, [Drawing.Color]::FromArgb(128, 69, 214, 208))
    $chromatic.SetPixel(1, 7, [Drawing.Color]::FromArgb(128, 69, 214, 208))
    $chromatic.SetPixel(13, 7, [Drawing.Color]::FromArgb(128, 216, 91, 213))
    $chromatic.SetPixel(7, 13, [Drawing.Color]::FromArgb(128, 112, 103, 232))

    $chromaticPath = Join-Path $output 'icon_chromatic.png'
    $combinedPath = Join-Path $output 'icon_shiny_chromatic.png'
    $chromatic.Save($chromaticPath, [Drawing.Imaging.ImageFormat]::Png)
    $combined.Save($combinedPath, [Drawing.Imaging.ImageFormat]::Png)

    $previewBitmap = [Drawing.Bitmap]::new(416, 176, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [Drawing.Graphics]::FromImage($previewBitmap)
    try {
        $graphics.Clear([Drawing.Color]::FromArgb(255, 30, 34, 42))
        $graphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
        $graphics.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::Half
        $graphics.DrawImage($reference, [Drawing.Rectangle]::new(8, 24, 128, 128), 0, 0, 16, 16, [Drawing.GraphicsUnit]::Pixel)
        $graphics.DrawImage($chromatic, [Drawing.Rectangle]::new(144, 24, 128, 128), 0, 0, 16, 16, [Drawing.GraphicsUnit]::Pixel)
        $graphics.DrawImage($combined, [Drawing.Rectangle]::new(280, 24, 128, 128), 0, 0, 16, 16, [Drawing.GraphicsUnit]::Pixel)
    } finally {
        $graphics.Dispose()
    }
    $previewBitmap.Save($preview, [Drawing.Imaging.ImageFormat]::Png)
    $previewBitmap.Dispose()
    $chromatic.Dispose()
    $combined.Dispose()
} finally {
    $reference.Dispose()
}

Write-Output $output
Write-Output $preview
