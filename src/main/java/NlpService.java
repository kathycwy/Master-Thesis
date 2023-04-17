import com.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class NlpService {

    private static String filepath = "src/main/output/raw-complete.csv";

    public static void main(String[] args) {

        ArrayList<String> rawText = getRawText(filepath);

        try {

            int count = rawText.size();

            File f = new File("src/main/output/raw-complete-clean.csv");
            FileWriter file = new FileWriter(f);
            CSVWriter writer = new CSVWriter(file);
            String[] header = { "Index", "Tokens" };
            writer.writeNext(header);

            System.out.println("Start text cleaning and tokenizing.");

            Set<String> tokens = null;
            int index = 1;
            for (String text : rawText) {

                tokens = NlpTextProcessor.removePosAndStopWords(text);

                tokens = NlpTextProcessor.removeUrl(tokens);

                String[] record = new String[]{String.valueOf(index), String.valueOf(tokens)};

                writer.writeNext(record, true);

                System.out.println("[" + index++ + "/" + count + "] " + Arrays.toString(tokens.toArray()));

                }

                System.out.println("Complete text cleaning and tokenizing.");
                writer.close();



            } catch (IOException ioException) {
            ioException.printStackTrace();
        }


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
                    rawText.add(line[4]);
                }
            }


        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }

        return rawText;

    }

}
