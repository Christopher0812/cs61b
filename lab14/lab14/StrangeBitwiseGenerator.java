package lab14;

public class StrangeBitwiseGenerator extends SawToothGenerator {
    public StrangeBitwiseGenerator(int period) {
        super(period);
    }

    @Override
    public double next() {
        state = state + 1;
//        return state & (state << 11) & (state << 13) % period;
        return state & (state >> 5) & (state >> 11) % period;
    }
}
