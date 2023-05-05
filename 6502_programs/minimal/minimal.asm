.segment "HEADER"
	.byte "NES" 	; Magic bytes
	.byte $1A 		; Magic bytes
	.byte 2    		; PRG ROM size in 16k chunks. "2" means 32k
	.byte 0    		; CHR ROM size in 8k chunks. "0" means no ROM, use RAM

.segment "CODE"
	reset:
		lda #$AA
		stop:
			jmp stop
	nmi:
		rti

	irq:
		rti

.segment "VECTORS"
	.word nmi
	.word reset
	.word irq

.segment "CHARS"

.segment "STARTUP"
