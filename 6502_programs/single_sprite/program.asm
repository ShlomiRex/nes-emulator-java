bit $2002 ; test the PPU status register (initializes the status register P)
bit $2002 ; test the PPU status register for real
bpl $8003 ; loop back to $8003 while N flag is 0. Continue if N = 1 (it means that one VBlank has occurred)
bit $2002 ; test the PPU status register again
bpl $8008 ; loop back to $8008 while N flag is 0. Continue if N = 1 (it means that a second VBlank has occurred)
LDX 3F    ; set X = 3F.
STX $2006 ; write X (3F) into the high byte of PPUADDR (the PPU address we want to write to), through the CPU register $2006.
LDX 00    ; set X = 00
STX $2006 ; write X (00) into the low byte of PPUADDR. After these two writes, PPUADDR = $3F00.
LDX 3f    ; set X = first color of BG palette 0.
STX $2007 ; write X in PPU memory at address $3F00. This is done by writing X at CPU address $2007 (PPUDATA). PPUADDR is auto-incremented after the write.
LDX 2d    ; set X = second color of BG palette 0.
STX $2007 ; write X in PPU memory at address $3F01.
LDX 10    ; set X = third color of BG palette 0.
STX $2007 ; write X in PPU memory at address $3F02.
LDX 30    ; set X = fourth color of BG palette 0.
STX $2007 ; write X in PPU memory at address $3F03.
LDX 0A    ; set X = 0A
STX $2001 ; write X in CPU memory at address $2001 (PPUMASK). This enables PPU's background rendering.
bpl $802e ; infinite loop.
                 ; at this point, all the background tiles should be drawn like in the editor above, as they all use the tile 0 and palette 0 by default.
                 ; also: the screen has been scrolled because of the PPUADDR writes. If a different scroll value is required (ex: reset 