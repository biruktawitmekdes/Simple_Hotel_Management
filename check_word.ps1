try {
    $word = New-Object -ComObject Word.Application
    $word.Quit()
    Write-Output 'WORD-OK'
} catch {
    Write-Output "WORD-FAIL:$($_.Exception.Message)"
}
