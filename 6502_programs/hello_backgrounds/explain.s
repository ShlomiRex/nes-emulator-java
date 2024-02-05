; The # symbol means immediate value

; Number representation
; The $ symbol means the number is in hexadecimal
; The % symbol means the number is in binary
; If no symbol is used, the number is in decimal

ldy #$50        ; loads Y with 50 in hex.
lda #%00100011  ; loads A with a binary number.
ldx #50         ; loads X with 50 in decimal.

; Memory location representation
lda $2002   ; load A with the value at memory location 2002 hex.
ldx 2002    ; load X with the value at memory location 2002 decimal.
sta $2004  ; stores A in memory location 2004 hex.
stx $FF   ; stores X in memory location 00FF hex.

; Banks
Bank 0 - We're our code goes starting at $8000.
Bank 1 - An Interrupt Table. Important. Starts at $FFFA for us.
Bank 2 - Where we will be putting our sprite and background data. Starting at $0000.

; INES header
.inesprg     - tells how many program code banks there are.
.ineschr     - tells how many picture data banks there are.
.inesmir     - tells something I don't remember, but will be always 1.
.inesmap     - We always use Mapper 0.