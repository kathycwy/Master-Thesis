import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TopicModellingService {

    public static void main(String[] args) throws Exception {
        prepareTxt();
//        runMallet();
    }

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

            File file = new File("Mallet-202108/data/" + line[0] + ".txt");
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(line[1]);
            myWriter.close();

        }

    }

    static public ArrayList<ArrayList<String>> getTopicState() throws IOException {

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("Mallet-202108/output/topic-state.gz"));
        BufferedReader br = new BufferedReader(new InputStreamReader(gzip));

        // skip the first 3 lines
        br.readLine();
        br.readLine();
        br.readLine();

        for (String line; (line = br.readLine()) != null; ) {
            lines.add(line);
        }

        for (String str : lines) {
            ArrayList<String> data = new ArrayList<>(Arrays.asList(str.split(" ")));
            // get docId from the filepath
            data.set(1, data.get(1).substring(19, data.get(1).length() - 4));
            // add W before word id
            data.set(3, "W" + data.get(3));
            // add T before topic id
            data.set(5, "T" + data.get(5));
            result.add(data);
        }

        return result;
    }

    static public List<String[]> getDocument() {

        try {

            BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete.csv"));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            List<String[]> result = csvReader.readAll();
            return result;


        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


        static public void runMallet() throws IOException, InterruptedException {

//        String[] command = new String[2];
//        // transform individual txt files into a single MALLET format file
//        command[0] = "Mallet-202108/bin/mallet import-dir --input Mallet-202108/data-20 --output Mallet-202108/output/input20.mallet --keep-sequence --remove-stopwords";
//        // trains MALLET to find 50 topics
//        command[1] = "Mallet-202108/bin/mallet train-topics  --input Mallet-202108/output/input20.mallet --num-topics 2 --optimize-interval 10 --output-state Mallet-202108/output/topic-state-2.gz --output-topic-keys Mallet-202108/output/keys-2.txt --output-doc-topics Mallet-202108/output/composition-2.txt";

        Runtime rt = Runtime.getRuntime();
        // transform individual txt files into a single MALLET format file
        Process pr = rt.exec("Mallet-202108/bin/mallet import-dir --input Mallet-202108/data --output Mallet-202108/output/input.mallet --keep-sequence --remove-stopwords");

        // trains MALLET to find 50 topics
        pr = rt.exec("Mallet-202108/bin/mallet train-topics  --input Mallet-202108/output/input.mallet --num-topics 100 --optimize-interval 10 --output-state Mallet-202108/output/topic-state.gz --output-topic-keys Mallet-202108/output/keys.txt --output-doc-topics Mallet-202108/output/composition.txt");

//        Process pr = rt.exec(command);

//        // get the input stream of the process and print it
//        InputStream in = pr.getInputStream();
//        for (int i = 0; i < in.available(); i++) {
//            System.out.println("" + in.read());
//        }
//
//        // wait for 10 seconds and then destroy the process
//        Thread.sleep(10000);
//        pr.destroy();

//        BufferedReader stdInput = new BufferedReader(new
//                InputStreamReader(pr.getInputStream()));
//
//        String s = null;
//        while ((s = stdInput.readLine()) != null) {
//            System.out.println(s);
//        }

//        ProcessBuilder pb = new ProcessBuilder(command);
//        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//        Process p = pb.start();

    }
}
