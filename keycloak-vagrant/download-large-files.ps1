# Set file URLs and names
$files = @(
    @{
        ID = "1scqAQ1FMp81kbWM_Y-8fxarW-1i1Xvj5"
        Name = "bento-ubuntu-22-04.box"
        TargetDir = "boxes\vbox-ubuntu"
    },
    @{
        ID = "10qFK7TiVOfA4J2vtqPBqKx9c7mYJuRN6"
        Name = "keycloak-22.0.1.tar.gz"
        TargetDir = "provision\keycloak\common"
    }
)

# Ensure target directories exist
foreach ($file in $files) {
    $targetPath = Join-Path -Path $PSScriptRoot -ChildPath $file.TargetDir
    if (-not (Test-Path -Path $targetPath)) {
        New-Item -Path $targetPath -ItemType Directory
    }
}

# Function to download files from Google Drive
function Download-GoogleDriveFile {
    param (
        [string]$FileID,
        [string]$FileName,
        [string]$TargetDir
    )

    $targetPath = Join-Path -Path $PSScriptRoot -ChildPath $TargetDir
    $filePath = Join-Path -Path $targetPath -ChildPath $FileName

    if (-not (Test-Path -Path $filePath)) {
        Write-Host "Downloading $FileName with FILE_ID: $FileID..."

        # Initial request to get the confirmation token
        $initialResponse = Invoke-WebRequest -Uri "https://drive.google.com/uc?export=download&id=$FileID" -SessionVariable session
        Write-Host "Initial response status code: $($initialResponse.StatusCode)"
        Write-Host "Initial response content length: $($initialResponse.Content.Length)"

        # Extract the confirmation token from the response content using regex
        $confirmationToken = $null
        if ($initialResponse.Content -match 'name="confirm" value="([^"]+)"') {
            $confirmationToken = $matches[1]
        }

        if (-not $confirmationToken) {
            Write-Host "Error: Confirmation token not found."
            return
        }

        Write-Host "Confirmation token: $confirmationToken"

        # Download the file with the confirmation token
        $downloadUrl = "https://drive.google.com/uc?export=download&confirm=$confirmationToken&id=$FileID"
        $downloadResponse = Invoke-WebRequest -Uri $downloadUrl -WebSession $session
        Write-Host "Download response status code: $($downloadResponse.StatusCode)"
        Write-Host "Download response content length: $($downloadResponse.Content.Length)"

        # Extract form action and input values
        $downloadAnywayUrl = $null
        $formData = @{}
        if ($downloadResponse.Content -match 'id="download-form" action="([^"]+)"') {
            $downloadAnywayUrl = $matches[1]
        }

        if ($downloadResponse.Content -match 'name="id" value="([^"]+)"') {
            $formData["id"] = $matches[1]
        }
        if ($downloadResponse.Content -match 'name="export" value="([^"]+)"') {
            $formData["export"] = $matches[1]
        }
        if ($downloadResponse.Content -match 'name="confirm" value="([^"]+)"') {
            $formData["confirm"] = $matches[1]
        }
        if ($downloadResponse.Content -match 'name="uuid" value="([^"]+)"') {
            $formData["uuid"] = $matches[1]
        }

        if ($downloadAnywayUrl) {
            if ($downloadAnywayUrl -notmatch '^https?://') {
                $downloadAnywayUrl = "https://drive.google.com$downloadAnywayUrl"
            }
            Write-Host "Download anyway URL: $downloadAnywayUrl"
            Invoke-WebRequest -Uri $downloadAnywayUrl -WebSession $session -Method Get -Body $formData -OutFile $filePath
        } else {
            Write-Host "Error: 'Download anyway' link not found in response."
            return
        }

        # Check if the file is actually downloaded and is not an HTML content
        $fileContent = Get-Content -Path $filePath -Raw
        if ($fileContent -like "<html*") {
            Write-Host "Error: Failed to download $FileName. Received HTML content."
            Remove-Item -Path $filePath
        } else {
            Write-Host "$FileName downloaded successfully."
        }
    } else {
        Write-Host "$FileName already exists in $targetPath"
    }
}

# Download files
foreach ($file in $files) {
    Download-GoogleDriveFile -FileID $file.ID -FileName $file.Name -TargetDir $file.TargetDir
}

Pause
