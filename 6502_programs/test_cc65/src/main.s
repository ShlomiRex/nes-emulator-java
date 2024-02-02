.segment "HEADER"
    .byte $4E, $45, $53, $1A
    .byte 2               ; 2x 16KB PRG code
    .byte 1               ; 1x  8KB CHR data
    .byte $01, $00        ; mapper 0, vertical mirroring

.segment "STARTUP"
.segment "VECTORS"
.segment "CHARS"

.segment "CODE"
.proc main
        ; Initialize PPU
        lda #$3F
        sta $2006   ; PPUADDR (set the address)
        lda #$00
        sta $2006

        lda #$2C
        sta $2007   ; PPUDATA (set the color)

    fillLoop:
        lda #$00
        sta $2006
        lda #$00
        sta $2006

        lda #$2C    ; Color code (palette entry) to fill the screen with (e.g., blue)
        sta $2007   ; PPUDATA (set the color)

        lda #$20
        sta $2006   ; Increment address by 32 (move to the next row)

        lda #$00
        sta $2006

        ; Check if we've filled the entire screen (30 rows x 32 columns)
        cmp #$1E
        beq done

        jmp fillLoop

    done:
        ; Endless loop
        jmp done

    .endproc