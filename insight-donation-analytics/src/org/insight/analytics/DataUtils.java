package org.insight.analytics;

import java.util.ArrayList;
import java.util.Collections;

public class DataUtils {
    
    /**
     * Percentile_index = (xthPercentile / 100 ) * size of list
     * Percentile = values[Percentile_index]
     * @param values
     * @param xthPercentile
     * @return 
     */
    public static double calculatePercentile(ArrayList<Double> values, double xthPercentile) {
        Collections.sort(values);
        int index = (int)Math.ceil( xthPercentile / 100 * values.size());
        return values.get(index);
    }
}
