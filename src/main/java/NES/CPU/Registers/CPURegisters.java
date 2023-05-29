package NES.CPU.Registers;

import NES.Common;
import NES.UI.Debugger.CPUDebugger.RegistersPanel;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Observable;

public class CPURegisters {

    private byte A, X, Y, S;
    private short PC;

    private StatusFlags P;

    private RegistersPanel listener;

    public CPURegisters() {
        P = new StatusFlags();
        reset();
    }

    @Override
    public String toString() {
        String a = "A: " + Common.byteToHexString(A, false);
        String x = "X: " + Common.byteToHexString(X, false);
        String y = "Y: " + Common.byteToHexString(Y, false);
        String s = "S: " + Common.byteToHexString(S, false);
        String pc = "PC: " + Common.shortToHexString(PC, false);
        String p = "P: NV-BDIZC " + Common.byteToBinaryString(P.getAllFlags());

        StringBuilder sb = new StringBuilder();
        sb.append(a).append("\t");
        sb.append(x).append("\t");
        sb.append(y).append("\t");
        sb.append(s).append("\t");
        sb.append(pc).append("\t");
        sb.append(p);
        return sb.toString();
    }

    public void reset() {
        A = X = Y = 0;
        P.reset();
        S = (byte) 0xFF;
        PC = 0;
    }

    public short getPC() {
        return PC;
    }

    public byte getA() {
        return A;
    }

    public byte getX() {
        return X;
    }

    public byte getY() {
        return Y;
    }

    public byte getS() {
        return S;
    }

    public StatusFlags getP() {
        return P;
    }

    public void setA(byte A) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "A", this.A, A));
        this.A = A;
    }

    public void setX(byte X) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "X", this.X, X));
        this.X = X;
    }

    public void setY(byte Y) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "Y", this.Y, Y));
        this.Y = Y;
    }

    public void setP(StatusFlags P) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "P", this.P, P));
        this.P = P;
    }

    public void setPC(short PC) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "PC", this.PC, PC));
        this.PC = PC;
    }

    public void setS(byte s) {
        if (listener != null)
            listener.propertyChange(new PropertyChangeEvent(this, "S", this.S, s));
        this.S = s;
    }

    public void addChangeListener(RegistersPanel listener) {
        this.listener = listener;
    }
}
