import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class NlpService {

    private static String filepath = "src/main/output/raw.csv";

    public static void main(String[] args) {

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

            ArrayList<String> rawText = new ArrayList<>();
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line != null) {
//                System.out.println(line[3]);
                    rawText.add(line[4]);
                }
            }

//        String[] tokens;
            Set<String> tokens;
            for (String text : rawText) {
//            tokens = Tokenizer.tokenize(text);
//            System.out.println(Arrays.toString(tokens));
                tokens = StopWordsRemover.removeStopWords(text);
                System.out.println(Arrays.toString(tokens.toArray()));
            }

        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }

    }

}
