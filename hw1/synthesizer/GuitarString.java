package synthesizer;

import java.util.Random;

// Make sure this class is public
public class GuitarString {
    /**
     * Constants. Do not change. In case you're curious, the keyword final means
     * the values cannot be changed at runtime. We'll discuss this and other topics
     * in lecture on Friday.
     */
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = .996; // energy decay factor

    /* Buffer for storing sound data. */
    private BoundedQueue<Double> buffer;

    /* Create a guitar string of the given frequency.  */
    public GuitarString(double frequency) {
        buffer = new ArrayRingBuffer<>((int) Math.round(SR / frequency));

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.enqueue(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {
        double r;
        for (int i = 0; i < buffer.capacity(); i++) {
            r = Math.random() - 0.5;
            buffer.dequeue();
            buffer.enqueue(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double first = buffer.dequeue();
        double second = sample();
        double newSample = 0.5 * (first + second) * DECAY;

        buffer.enqueue(newSample);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {
        return buffer.peek();
    }
}
