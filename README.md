# Java NES Emulator

This is my attempt to create basic NES emulator in Java.

My goal is to not to look at other's code, but to understand on the high-level the NES architecture and create it in Java.

# Progress Screenshots

While developing the NES, not everything goes according to plan.

This section is to show the fun and weird experiences I had.

----------------

First pattern tile render (notice I already added CPU debugger (everything works except the stack, which for now is just for show):
![](README-resources/Screenshot2023-05-10%20031001.png)

Added pattern tables, added system palette, added run/stop buttons
![](README-resources/Screenshot2023-05-12%20203615.png)

Added assembly viewer, PPU registers viewer, and PPU debugger
![](README-resources/Screenshot2023-05-26%20173426.png)

First time working palette from RAM:
![](README-resources/Screenshot%202023-07-04%20175232.png)

First time rendering basic background (on the right - the game itself):
![](README-resources/Screenshot%202023-07-04%20234402.png)

First time working complete palette initialization from NESTEST, code is just waiting for input interrupts:
![](README-resources/Screenshot%202023-07-05%20014713.png)

First time display working:
![](README-resources/Screenshot%202023-07-12%20192227.png)

Fixed debugger nametable canvases + fixed NMI interrupt loop:
![](README-resources/Screenshot%202023-07-27%20192342.png)

First time working controller display:

![](README-resources/Screenshot%202023-07-27%20232945.png)

First time working NESTEST:

![](README-resources/Screenshot%202023-07-28%20000909.png)

First time working NESTEST, fixed controller input:
![](README-resources/Animation.gif)

First time running Donkey Kong (cool glitches):
![](README-resources/Animation2.gif)

First time working donkey kong:
![](README-resources/Animation4.gif)

Cool glitch:
![](README-resources/02440f42-e308-4590-9180-ed8955d7d203.jpg)

Found mario face in my debugger:
![](README-resources/8fe822e2-60fd-44a2-8037-b1576888a490.jpg)

Fixed color issues:
![](README-resources/Screenshot%202023-07-29%20012454.png)

First time sprite rendering:
![](README-resources/Screenshot%202023-08-15%20000121.png)

Weird sprite rendering glitch:
![](README-resources/Screenshot%202023-08-15%20001952.png)

Fixed sprite rendering glitch:
![](README-resources/Animation5.gif)

Fixed palette and colors for sprites:
![](README-resources/Screenshot%202023-08-18%20232651.png)

A lot of debugger improvements, including:

- Palette selection: redraws the pattern tables when user selects a palette
- Rendering nametables now works correctly, sprites are not displayed there
- Added global controls for the debugger: Run/Stop and checkbox of maximum emulation speed

![](README-resources/Screenshot%202023-12-06%20165127.png)

![](README-resources/Screenshot%202023-12-06%20165109.png)

Trying to fix PPU rendering issues (left - my game, right - FCEUX emulator:

![](README-resources/Screenshot%202024-02-02%20004954.png)

First time working render output using PPU timings, instead of directly reading the nametable, notice
I still have some rendering problems (not drawing '1/2' pattern correctly):

![](README-resources/Screenshot%202024-02-09%20222329.png)

# Resources

![](README-resources/ppu_diagram.svg)

The most used resorces:

- Online emulator for quick testing of the CPU: [here](https://skilldrick.github.io/easy6502/#first-program)
- Best CPU instructions summary: [here](https://www.masswerk.at/6502/6502_instruction_set.html)
- Addressing Modes + to make my emulator CPU/cycle accurate: [here](https://www.nesdev.org/6502_cpu.txt), 
which is from a question I posted on reddit: [here](https://www.reddit.com/r/EmuDev/comments/13vyfug/nes_test_lda_i_think_the_test_rom_is_wrong/)

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
