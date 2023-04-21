import com.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class NlpService {

    private static String filepath = "src/main/output/raw-150.csv";

    public static void main(String[] args) throws IOException {

        ArrayList<String> rawText = getRawText(filepath);

        System.out.println("Text cleaning and tokenizing STARTED.");

        int count = rawText.size();

            File f = new File("src/main/output/raw-150-clean.csv");
            FileWriter file = new FileWriter(f);
            CSVWriter writer = new CSVWriter(file);
            String[] header = { "DocId", "Tokens" };
            writer.writeNext(header);

        Set<String> tokens = null;
        int index = 0;
        for (String text : rawText) {

            tokens = NlpTextProcessor.removePosAndStopWords(text);

            tokens = NlpTextProcessor.removeUrl(tokens);

            String[] record = new String[]{"D"+index, String.valueOf(tokens)};

            writer.writeNext(record, true);

            System.out.println("[" + index++ + "/" + count + "] " + Arrays.toString(tokens.toArray()));

            }

        writer.close();


        System.out.println("Text cleaning and tokenizing COMPLETED.");

    }

    static ArrayList<String> getRawText(String path) {

        ArrayList<String> rawText = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(filepath));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line != null) {
                    rawText.add(line[7]);
                }
            }


        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }

        return rawText;

    }

    static ArrayList<ArrayList<String>> getTokensList(String path) {

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(path));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line != null) {
                    list.add(line[1]);
                }
            }


        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }

        for (String token : list) {
            token = token.substring(1, token.length()-1);
            ArrayList<String> tokens = new ArrayList<>(Arrays.asList(token.split(", ")));
            result.add(tokens);
        }

        return result;

    }

}
