package lab14;


import lab14lib.Generator;
import lab14lib.GeneratorAudioVisualizer;
import lab14lib.MultiGenerator;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Generator g1 = new StrangeBitwiseGenerator(3110);
        Generator g2 = new StrangeBitwiseGenerator(4130);

        ArrayList<Generator> generators = new ArrayList<Generator>();
        generators.add(g1);
        generators.add(g2);
        MultiGenerator mg = new MultiGenerator(generators);

        GeneratorAudioVisualizer gav = new GeneratorAudioVisualizer(mg);
        gav.drawAndPlay(50000, 100000000);
    }
} 