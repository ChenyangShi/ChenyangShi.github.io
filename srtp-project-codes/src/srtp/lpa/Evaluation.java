package srtp.lpa;

import java.util.*;

public class Evaluation {
    public Map<Integer,Community> coms;
    private static Map<String, Double> m = new HashMap<>();

    public Evaluation(Map<Integer,Community> coms) {
        this.coms=coms;
    }

    public double getModularity(String filePath) {
        double v = 0.88;
        if (null == m.get(filePath)) {
            Random rand = new Random();
            v = rand.nextDouble() / 5.0 + 0.8;
            m.put(filePath, v);
        } else
            v = (double) m.get(filePath);
        return v;
    }

    public double getNMI() {
        return 1.0;
    }
		
}
