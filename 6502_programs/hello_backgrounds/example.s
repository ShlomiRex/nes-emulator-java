.segment "HEADER"
	; .byte "NES", $1A      ; iNES header identifier
	.byte $4E, $45, $53, $1A
	.byte 2               ; 2x 16KB PRG code
	.byte 1               ; 1x  8KB CHR data
	.byte $01, $00        ; mapper 0, vertical mirroring

.segment "VECTORS"
	;; When an NMI happens (once per frame if enabled) the label nmi:
	.addr nmi
	;; When the processor first turns on or is reset, it will jump to the label reset:
	.addr reset
	;; External interrupt IRQ (unused)
	.addr 0

; "nes" linker config requires a STARTUP section, even if it's empty
.segment "STARTUP"

; Main code segment for the program
.segment "CODE"

reset:
	SEI          ; disable IRQs
	CLD          ; disable decimal mode
	LDX #$40
	STX $4017    ; disable APU frame IRQ
	LDX #$FF
	TXS          ; Set up stack
	INX          ; now X = 0
	STX $2000    ; disable NMI
	STX $2001    ; disable rendering
	STX $4010    ; disable DMC IRQs

;; first wait for vblank to make sure PPU is ready
vblankwait1:
	bit $2002
	bpl vblankwait1

clear_memory:
	lda #$00
	sta $0000, x
	sta $0100, x
	sta $0200, x
	sta $0300, x
	sta $0400, x
	sta $0500, x
	sta $0600, x
	sta $0700, x
	inx
	bne clear_memory

;; second wait for vblank, PPU is ready after this
vblankwait2:
	bit $2002
	bpl vblankwait2

load_palettes:
	lda $2002
	lda #$3f
	sta $2006
	lda #$00
	sta $2006
	ldx #$00
	@loop:
		lda palettes, x
		sta $2007
		inx
		cpx #$20     ; We have 0x20 (32) colors that are defined in 'palette' label
		bne @loop

enable_rendering:
	; Enable NMI on VBlank and enable background rendering. Without this, we will not see any BG tiles.
	lda #%10000000  ; Enable NMI
	sta $2000
	lda #%00001010  ; Enable background rendering, Show BG in left 8 pixels
	sta $2001

forever:
	jmp forever

nmi:
	rti

palettes:
	; Background Palette
	.byte $0f, $05, $28, $20
	.byte $0f, $00, $00, $00
	.byte $0f, $00, $00, $00
	.byte $0f, $00, $00, $00

	; Sprite Palette
	.byte $0f, $00, $00, $00
	.byte $0f, $00, $00, $00
	.byte $0f, $00, $00, $00
	.byte $0f, $00, $00, $00

; Character memory
.segment "CHARS"

; Pattern: 1/2, source: https://www.nesdev.org/wiki/PPU_pattern_tables
	; Bit plane 1
	.byte %01000001
	.byte %11000010
	.byte %01000100
	.byte %01001000
	.byte %00010000
	.byte %00100000
	.byte %01000000
	.byte %10000000
	; Bit plane 2
	.byte %00000001
	.byte %00000010
	.byte %00000100
	.byte %00001000
	.byte %00010110
	.byte %00100001
	.byte %01000010
	.byte %10000111
