package lab14;

public class AcceleratingSawToothGenerator extends SawToothGenerator {
    protected double factor;
    public AcceleratingSawToothGenerator(int period, double factor) {
        super(period);
        this.factor = factor;
    }

    @Override
    public double next() {
        state++;
        if (state == period) {
            state = 0;
            period *= factor;
        }
        return -1 + state * (2.0 / period);
    }
}
