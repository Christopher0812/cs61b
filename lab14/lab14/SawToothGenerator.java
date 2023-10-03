package lab14;

import lab14lib.Generator;

public class SawToothGenerator implements Generator {

    protected int period;

    protected int state;

    public SawToothGenerator(int period) {
        this.state = 0;
        this.period = period;
    }

    @Override
    public double next() {
        state = (state + 1) % period;
        return -1 + state * (2.0 / period);
    }
}
