import com.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeakSignalCalculator {

    static final double tw = 0.05;
    final int year = 2023;

    public static void main(String[] args) throws IOException, InterruptedException {

//        double[] dov = calculateDoV(5, 1);
        calculateDoD();

//        System.out.println(Arrays.toString(dov));

    }

    public static double[] calculateDoV(int n, int j) throws IOException {

        double[] compositionArray = TopicModellingService.getCompositionArray();

//        System.out.println(Arrays.toString(compositionArray));

        double[] dov = new double[19127];
        for (int i = 0; i < compositionArray.length; i++) {
            dov[i] = compositionArray[i] * (1 - tw * (n - j));
        }

        return dov;

    }


    public static void calculateDoD() {

        ArrayList<String[]> numDocsList = Neo4jConnector.getNumOfDocWordAppears();
        System.out.println(Arrays.toString(new ArrayList[]{numDocsList}));
        

    }


    public void calculateDoT() {

    }


}
