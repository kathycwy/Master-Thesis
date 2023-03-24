import com.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class NlpService {

    private static String filepath = "src/main/output/raw-200.csv";

    public static void main(String[] args) {

        ArrayList<String> rawText = getRawText(filepath);

        try {

//            BufferedReader br = new BufferedReader(new FileReader(filepath));
//
//            CSVParser parser = new CSVParserBuilder()
//                    .withSeparator(',')
//                    .withIgnoreQuotations(false)
//                    .build();
//
//            CSVReader csvReader = new CSVReaderBuilder(br)
//                    .withSkipLines(1)
//                    .withCSVParser(parser)
//                    .build();
//
//            ArrayList<String> rawText = new ArrayList<>();
//            String[] line;
//            while ((line = csvReader.readNext()) != null) {
//                if (line != null) {
////                System.out.println(line[3]);
//                    rawText.add(line[4]);
//                }
//            }


            File f = new File("src/main/output/raw-200-clean.csv");
            FileWriter file = new FileWriter(f);
            CSVWriter writer = new CSVWriter(file);
            String[] header = { "Index", "Tokens" };
            writer.writeNext(header);

//        String[] tokens;
            Set<String> tokens = null;
            int index = 1;
            for (String text : rawText) {
//            tokens = Tokenizer.tokenize(text);
//            System.out.println(Arrays.toString(tokens));
                tokens = StopWordsRemover.removeStopWords(text);

                String[] record = new String[]{String.valueOf(index++), String.valueOf(tokens)};

                writer.writeNext(record, true);


                }

                writer.close();

                System.out.println(Arrays.toString(tokens.toArray()));

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
//                System.out.println(line[3]);
                    rawText.add(line[4]);
                }
            }


        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }

        return rawText;

    }

}
