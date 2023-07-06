package NES.CPU.Decoder;

import NES.Common;

public class DecoderException extends Exception {
    public DecoderException(byte opcode) {
        super("Could not decode the opcode: " + Common.byteToHex(opcode, true));
    }
}
