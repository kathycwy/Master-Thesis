import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
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
        System.out.println("The Data Set after Normalization: ");
        for (int i = 0; i < arr.length; i++) {
            v[i] = ((arr[i] - minValue(arr)) / (maxValue(arr) - minValue(arr))) * (new_max - new_min) + new_min;
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

    public static double[] calculateDoV(int n, int j, double[] arr) throws IOException {

        double[] dov = new double[19127];
        for (int i = 0; i < arr.length; i++) {
            dov[i] = arr[i] * (1 - tw * (n - j));
        }

        dov = normalize(0, 1, dov);

        System.out.println(Arrays.toString(dov));

        return dov;

    }


    public static double[] calculateDoD(int n, int j, int[] arr) throws FileNotFoundException {

        double[] dod = new double[19127];
        for (int i = 0; i < arr.length; i++) {
            dod[i] = arr[i] * (1 - tw * (n - j));
        }

        dod = normalize(0, 1, dod);

        System.out.println(Arrays.toString(dod));

        return dod;
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
                Double result = Double.parseDouble(resultCssArray[1].replace(".", ""));

                dot[j++] = result;

//                System.out.println("Number of search results for \"" + word + "\": " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Arrays.toString(dot));


    }

    public static void createWeakSignalValues() throws IOException {

        double[] compositionArray = TopicModellingService.getCompositionArray();

        String[] wordArray = new String[19127];
        Scanner scanner1 = new Scanner(new File("src/main/output/calWeakSignals/wordArray.txt"));
        int i = 0;
        while(scanner1.hasNext()) { wordArray[i++] = scanner1.next(); }

        int[] numDocsArray = new int[19127];
        Scanner scanner2 = new Scanner(new File("src/main/output/calWeakSignals/numDocsArray.txt"));
        int j = 0;
        while(scanner2.hasNextInt()) { numDocsArray[j++] = scanner2.nextInt(); }

        double[] dov = calculateDoV(5, 1, compositionArray);
        double[] dod = calculateDoD(5, 1, numDocsArray);

        File f = new File("src/main/output/calWeakSignals/WeakSignalValues.csv");
        CSVWriter writer = new CSVWriter(new FileWriter(f, true));
        String[] header = {"wordId", "word", "DoV", "composition", "DoD", "numDocs"};
        writer.writeNext(header);
        for (int x = 0; x < 19127; x++) {
            String[] record = new String[]{"W" + x, wordArray[x],
                    String.valueOf(dov[x]), String.valueOf(compositionArray[x]),
                    String.valueOf(dod[x]), String.valueOf(numDocsArray[x])};
            writer.writeNext(record, true);
        }
        writer.close();

    }


}
