import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class WeakSignalCalculator {

    static final double tw = 0.05;
    final int year = 2023;

    public static void main(String[] args) throws IOException, InterruptedException {

//        double[] dov = calculateDoV(5, 1);
//        double[] dod = calculateDoD(5, 1);
//        calculateDoT();
        createWeakSignalValues();

//        System.out.println(Arrays.toString(dov));

    }

    // Method to find minimum value from the data set
    static double minValue(double arr[]){
        double min = arr[0];
        for(int i = 1; i<arr.length; i++)
            if(arr[i]<min) min = arr[i];
        return min;
    }

    // Method to find maximum value from the data set
    static double maxValue(double arr[]){
        double max = arr[0];
        for(int i = 1; i<arr.length; i++)
            if(arr[i]>max) max = arr[i];
        return max;
    }

    // Method to find the normalized values of the data set
    static double[] normalize(int new_min, int new_max, double arr[]) {
        double[] v = arr;
//        System.out.println("The Data Set after Normalization: ");
        for (int i = 0; i < arr.length; i++) {
            v[i] = ((arr[i] - minValue(arr)) / (maxValue(arr) - minValue(arr))) * (new_max - new_min) + new_min;
        }
        return v;
    }

    static double[] normalize(int new_min, int new_max, int arr[]) {
        double[] v = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            v[i] = (double) arr[i];
        }
//        System.out.println("The Data Set after Normalization: ");
        for (int i = 0; i < arr.length; i++) {
            v[i] = ((v[i] - minValue(v)) / (maxValue(v) - minValue(v))) * (new_max - new_min) + new_min;
        }
        return v;
    }

    static double[] add(double arr1[], double[] arr2) {
        double[] v = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            v[i] = arr1[i] + arr2[i];
        }
        return v;
    }

//    public static double[] normalize(double[] array) {
//        double sum = 0;
//        for (double item : array) {
//            sum += item;
//        }
//        for (int i = 0; i < array.length; i++) {
//            array[i] = array[i] / sum;
//        }
//        return array;
//    }

    public static double[] calculateDoV(int n, int j, double nnj, double[] arr) throws IOException {

        double[] dov = new double[19127];
        for (int i = 0; i < arr.length; i++) {
            dov[i] = arr[i] / nnj * (1 - tw * (n - j));
        }

        dov = normalize(0, 1, dov);

//        System.out.println(Arrays.toString(dov));

        return dov;

    }

    public static double[] calculateDoD(int n, int j, double nnj, int[] arr) {

        double[] dod = new double[19127];
        for (int i = 0; i < arr.length; i++) {
            dod[i] = arr[i] / nnj * (1 - tw * (n - j));
        }

        dod = normalize(0, 1, dod);

//        System.out.println(Arrays.toString(dod));

        return dod;
    }

    public static double geometricMean(double... values) {
        return Math.pow(product(values), 1. / values.length);
    }

    public static double product(double... values) {
        double sum = 1;
        for (double v : values) {
            sum *= v;
        }
        return sum;
    }

    public static double[] calculateDelta(double[] arr1, double[] arr2, double[] arr3, double[] arr4, double[] arr5) {

        int len = arr1.length;

        double[] delta = new double[19127];

        for (int i = 0; i < len; i++) {
            delta[i] = geometricMean(arr1[i], arr2[i], arr3[i], arr4[i], arr5[i]);
        }
//        double[] delta12 = new double[19127];
//        double[] delta23 = new double[19127];
//        double[] delta34 = new double[19127];
//        double[] delta45 = new double[19127];
//
//        for (int i = 0; i < len; i++) {
//            int n = 4;
//            if (arr4[i] == 0) { delta45[i] = 0; n--; } else { delta45[i] = ((arr5[i] - arr4[i]) / arr4[i]) / 5; }
//            if (arr3[i] == 0) { delta34[i] = 0; n--; } else { delta34[i] = ((arr4[i] - arr3[i]) / arr3[i]) / 5; }
//            if (arr2[i] == 0) { delta23[i] = 0; n--; } else { delta23[i] = ((arr3[i] - arr2[i]) / arr2[i]) / 5; }
//            if (arr1[i] == 0) { delta12[i] = 0; n--; } else { delta12[i] = ((arr2[i] - arr1[i]) / arr1[i]) / 5; }
//
//            delta[i] = (delta12[i] + delta23[i] + delta34[i] + delta45[i]) / n;
//        }

//        System.out.println(Arrays.toString(delta));

        return delta;
    }

    public static void calculateDoT() throws FileNotFoundException {


        String[] wordArray = new String[19127];

        Scanner scanner = new Scanner(new File("src/main/output/calWeakSignals/wordArray.txt"));
        int i = 0;
        while(scanner.hasNext())
        {
            wordArray[i++] = scanner.next();
        }

        double[] dot = new double[19127];
        int j = 0;

        for (String word : wordArray) {
            String startYear = "2018";
            String endYear = "2023";
            String url = "https://scholar.google.com/scholar?" + "&as_ylo=" + startYear + "&as_yhi=" + endYear + "q=" + word;

            try {
                Document doc = Jsoup.connect(url).get();
                Elements resultCss = doc.select("#gs_ab_md");

                String resultCssText = resultCss.text();
                String[] resultCssArray = resultCssText.split(" ");
                double result = Double.parseDouble(resultCssArray[1].replace(".", ""));

                dot[j++] = result;

//                System.out.println("Number of search results for \"" + word + "\": " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        System.out.println(Arrays.toString(dot));


    }

    public static double[] createWeakSignalScore(double[] dov, double[] dod, double[] composition, int[] numDocs) throws IOException {

//        double[] compositionArray5Years = TopicModellingService.getCompositionArray(new String[]{"2018", "2019", "2020", "2021", "2022"});
//        double[] dov = calculateDoV(5, 1, compositionArray5Years);
//
//        int[] numDocs = new int[19127];
//        int j = 0;
//        Scanner scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray.txt"));
//        while(scanner2.hasNextInt()) { numDocs[j++] = scanner2.nextInt(); }
//        double[] dod = calculateDoD(5, 1, numDocs);

//        double[] normalizedNumDocs = normalize(0, 1, numDocs);

//        System.out.println(Arrays.toString(normalizedNumDocs));

        double scores[] = new double[19127];
        double maxComposition = maxValue(composition);
        for (int i = 0; i < dov.length; i++) {
//            scores[i] = (dov[i] + dod[i] + (1 - normalizedNumDocs[i]));
            scores[i] = (dov[i] + dod[i] + (1.0 - (numDocs[i] / 785.0)) + (1 - (composition[i] / maxComposition)));
        }

        scores = normalize(0, 100, scores);

//        System.out.println(Arrays.toString(scores));

        return scores;

//        File f = new File("src/main/output/calWeakSignals/scores.csv");
//        CSVWriter writer = new CSVWriter(new FileWriter(f, true));
//        String[] header = {"wordId", "score"};
//        writer.writeNext(header);
//        for (int i = 0; i < scores.length; i++) {
//            String[] record = new String[]{"W" + i, String.valueOf(String.format("%,.2f", scores[i]))};
//            writer.writeNext(record, false);
//        }
//        writer.close();

    }


    public static void createWeakSignalValues() throws IOException {

        System.out.print("START composition array...   ");
        double[] compositionArray2018 = TopicModellingService.getCompositionArray(new String[]{"2018"});
        double[] compositionArray2019 = TopicModellingService.getCompositionArray(new String[]{"2019"});
        double[] compositionArray2020 = TopicModellingService.getCompositionArray(new String[]{"2020"});
        double[] compositionArray2021 = TopicModellingService.getCompositionArray(new String[]{"2021"});
        double[] compositionArray2022 = TopicModellingService.getCompositionArray(new String[]{"2022"});
        double[] compositionArray5Years = TopicModellingService.getCompositionArray(new String[]{"2018", "2019", "2020", "2021", "2022"});
        System.out.println("DONE");

        System.out.print("START word array...   ");
        String[] wordArray = new String[19127];
        Scanner scanner1 = new Scanner(new File("src/main/output/calWeakSignals/wordArray.txt"));
        int i = 0;
        while(scanner1.hasNext()) { wordArray[i++] = scanner1.next(); }
        System.out.println("DONE");

        System.out.print("START topic array...   ");
        String[] topicArray = new String[19127];
        scanner1 = new Scanner(new File("src/main/output/calWeakSignals/topicArray.txt"));
        i = 0;
        while(scanner1.hasNext()) { topicArray[i++] = scanner1.next(); }
        System.out.println("DONE");

        System.out.print("START numDocs array...   ");
        int[] numDocsArray5Years = new int[19127];
        int[] numDocsArray2018 = new int[19127];
        int[] numDocsArray2019 = new int[19127];
        int[] numDocsArray2020 = new int[19127];
        int[] numDocsArray2021 = new int[19127];
        int[] numDocsArray2022 = new int[19127];
        int j = 0;
        Scanner scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray.txt"));
        while(scanner2.hasNextInt()) { numDocsArray5Years[j++] = scanner2.nextInt(); }
        j = 0;
        scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray2018.txt"));
        while(scanner2.hasNextInt()) { numDocsArray2018[j++] = scanner2.nextInt(); }
        j = 0;
        scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray2019.txt"));
        while(scanner2.hasNextInt()) { numDocsArray2019[j++] = scanner2.nextInt(); }
        j = 0;
        scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray2020.txt"));
        while(scanner2.hasNextInt()) { numDocsArray2020[j++] = scanner2.nextInt(); }
        j = 0;
        scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray2021.txt"));
        while(scanner2.hasNextInt()) { numDocsArray2021[j++] = scanner2.nextInt(); }
        j = 0;
        scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray2022.txt"));
        while(scanner2.hasNextInt()) { numDocsArray2022[j++] = scanner2.nextInt(); }
        System.out.println("DONE");

        System.out.print("START dov array...   ");
        double[] dov2018 = calculateDoV(5, 1, 138.0, compositionArray2018);
        double[] dov2019 = calculateDoV(5, 1, 161.0, compositionArray2019);
        double[] dov2020 = calculateDoV(5, 1, 201.0, compositionArray2020);
        double[] dov2021 = calculateDoV(5, 1, 121.0, compositionArray2021);
        double[] dov2022 = calculateDoV(5, 1, 164.0, compositionArray2022);
//        double[] dov = calculateDoV(5, 5, 785.0, compositionArray5Years);
        double[] dov = add(add(add(add(dov2018, dov2019), dov2020), dov2021), dov2022);
        double[] dovDelta = calculateDelta(dov2018, dov2019, dov2020, dov2021, dov2022);
        System.out.println("DONE");

        System.out.print("START dod array...   ");
        double[] dod2018 = calculateDoD(5, 1, 138.0, numDocsArray2018);
        double[] dod2019 = calculateDoD(5, 1, 161.0, numDocsArray2019);
        double[] dod2020 = calculateDoD(5, 1, 201.0, numDocsArray2020);
        double[] dod2021 = calculateDoD(5, 1, 121.0, numDocsArray2021);
        double[] dod2022 = calculateDoD(5, 1, 164.0, numDocsArray2022);
//        double[] dod = calculateDoD(5, 5, 785.0, numDocsArray5Years);
        double[] dod = add(add(add(add(dod2018, dod2019), dod2020), dod2021), dod2022);
        double[] dodDelta = calculateDelta(dod2018, dod2019, dod2020, dod2021, dod2022);
        System.out.println("DONE");

        System.out.print("START scores array...   ");
        double[] scores = createWeakSignalScore(dovDelta, dodDelta, compositionArray5Years, numDocsArray5Years);
        System.out.println("DONE");

        // output
        File f = new File("src/main/output/calWeakSignals/WeakSignalValues_13.csv");
        CSVWriter writer = new CSVWriter(new FileWriter(f, true));
        String[] header = {"wordId", "word", "topicId", "dov", "dovDelta", "composition", "dod", "dodDelta", "numDocs", "score"};
        writer.writeNext(header);
        for (int x = 0; x < 19127; x++) {
            String[] record = new String[]{"W" + x, wordArray[x], topicArray[x],
                    String.valueOf(dov[x]), String.valueOf(dovDelta[x]), String.valueOf(compositionArray5Years[x]),
                    String.valueOf(dod[x]), String.valueOf(dodDelta[x]), String.valueOf(numDocsArray5Years[x]),
                    String.valueOf(String.format("%,.2f", scores[x]))};
            writer.writeNext(record, false);
        }
        writer.close();

    }


}
