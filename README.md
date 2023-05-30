# Java NES Emulator

This is my attempt to create basic NES emulator in Java.

My goal is to not to look at other's code, but to understand on the high-level the NES architecture and create it in Rust.

# Screenshots

First pattern tile render

Notice I already added CPU debugger (everything works except the stack, which for now is just for show)
![](README-resources/Screenshot2023-05-10%20031001.png)

Added pattern tables, added system palette, added run/stop buttons
![](README-resources/Screenshot2023-05-12%20203615.png)

Added assembly viewer, PPU registers viewer, and PPU debugger
![](README-resources/Screenshot2023-05-26%20173426.png)

# Resources

The most used resorces:

- Online emulator for quick testing of the CPU: [here](https://skilldrick.github.io/easy6502/#first-program)
- Best CPU instructions summary: [here](https://www.masswerk.at/6502/6502_instruction_set.html)

Others:

- CPU Registers: [wiki](https://en.wikipedia.org/wiki/MOS_Technology_6502#Registers)
- R6500 Microchip datasheet: [datasheet](https://datasheetspdf.com/pdf-file/527507/Rockwell/R6502/1)
- Complete NES Emulator from scratch: [YouTube](https://www.youtube.com/watch?v=F8kx56OZQhg)
- NES References guide (`nesdev.org`): [wiki](https://www.nesdev.org/wiki/NES_reference_guide)
- yizhang82's blog: [blog](https://yizhang82.dev/nes-emu-overview)
- A blog/website that is no longer maintained but useful (using Wayback machine): [here](https://web.archive.org/web/20210909190432/http://www.obelisk.me.uk/6502/)
- Another youtuber reading the architecture: [YouTube](https://www.youtube.com/watch?v=qJgsuQoy9bc)
- Blog explains 6502 addressing mode: [Emulator101](http://www.emulator101.com/6502-addressing-modes.html#:~:text=The%206502%20has%20the%20ability,to%20the%20address%20being%20accessed.&text=This%20addressing%20mode%20makes%20the,register%20to%20an%20absolute%20address.)
- Introduction to 6502 assembly: [here](https://famicom.party/book/05-6502assembly/)
- Basic NES program that actually works: [here](https://famicom.party/book/03-gettingstarted/)
- Great CPU summary of almost everything: [here](https://www.nesdev.org/6502_cpu.txt)
- Started reading PPU blogs, but this one is the best: [here](https://www.youtube.com/watch?v=-THeUXqR3zY)
- Blog explains a lot about PPU, includes asm source code that runs basic blue background: [here](https://taywee.github.io/NerdyNights/nerdynights/asmfirstapp.html)
- Detailed PPU timings (scanline, cycles): [here](https://www.nesdev.org/w/images/default/4/4f/Ppu.svg)