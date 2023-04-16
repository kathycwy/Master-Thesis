import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;

public class TopicModeller {

    static void prepareTxt() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete-clean.csv"));

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

            File file = new File("src/main/output/txt/txt" + line[0] + ".txt");
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(line[1]);
            myWriter.close();

        }

    }

    static public void runMallet() throws IOException {

        Runtime rt = Runtime.getRuntime();
        // transform individual txt files into a single MALLET format file
        Process pr = rt.exec("Mallet-202108/bin/mallet import-dir --input Mallet-202108/sample-data/my_test_data --output Mallet-202108/output/abc1.mallet --keep-sequence --remove-stopwords");
        // trains MALLET to find 50 topics
        rt.exec("Mallet-202108/bin/mallet train-topics  --input Mallet-202108/output/abc1.mallet --num-topics 50 --optimize-interval 10 --output-state Mallet-202108/output/topic-state.gz --output-topic-keys Mallet-202108/output/tutorial_keys_all.txt --output-doc-topics Mallet-202108/output/tutorial_compostion_all.txt");


    }
}
