import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TopicModellingService {

    public static void main(String[] args) throws Exception {
//        prepareTxt();
//        runMallet();
        getCompositionArray();
    }

    static void prepareTxt() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-200-clean.csv"));

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

        GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("Mallet-202108/output/calComposition/topic-state-all.gz"));
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

    static public ArrayList<ArrayList<String>> getCompositionTxt() throws IOException {

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        ArrayList<String> lines = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader("Mallet-202108/output/calComposition/composition-all.txt"));

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(br)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        for (String line; (line = br.readLine()) != null; ) {
            lines.add(line);
        }

        for (String str : lines) {
            ArrayList<String> data = new ArrayList<>(Arrays.asList(str.split("\t")));
            result.add(data);
        }

        return result;
    }

    static public ArrayList<ArrayList<String>> getRaw200() throws IOException {

        ArrayList<ArrayList<String>> result = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-200.csv"));

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
            ArrayList<String> lines = new ArrayList<>();
            lines.add(line[0]);
            lines.add(line[1]);
//            lines.add(line[2]);
//            lines.add(line[3]);
//            lines.add(line[4]);
//            lines.add(line[5]);
//            lines.add(line[6]);
//            lines.add(line[7]);
            result.add(lines);
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

    static public double[] getCompositionArray() throws IOException {

        ArrayList<ArrayList<String>> topicState = getTopicState();
        ArrayList<ArrayList<String>> compositionTxt = getCompositionTxt();
        ArrayList<ArrayList<String>> raw200 = getRaw200();

//        double[] compositions = new double[topicState.size()];
        // total number of word = 19127
        double[] compositions = new double[19127];

        int wordId;
        int docRow;
        int topicColumn;

//        String docId = topicState.get(0).get(1);

        boolean within5Years = false;

        for (ArrayList<String> line : topicState) {
//
            String docId = line.get(1);
//
//            // if docId of this line is not as same as last line in topicState
//            if (!docId.equals(lastDocId)) {
//
//                lastDocId = docId;

                for (ArrayList<String> rawLine : raw200) {

                    if (rawLine.get(0).equals(docId)) {
                        // check if PublishDate within 5 years
                        within5Years = rawLine.get(1).startsWith("2022") || rawLine.get(1).startsWith("2021") || rawLine.get(1).startsWith("2020") || rawLine.get(1).startsWith("2019") || rawLine.get(1).startsWith("2018");

                        // break when the required docId is found
                        break;
                    }

                }
//            }

            if (within5Years) {
                wordId = Integer.parseInt(line.get(3).substring(1));
                docRow = Integer.parseInt(line.get(0));
                topicColumn = Integer.parseInt(line.get(5).substring(1));

                ArrayList<String> compositionLine = compositionTxt.get(docRow);
                String value = compositionLine.get(topicColumn + 2);
                double val = Double.parseDouble(value);
                compositions[wordId] += val;
            }

        }

//        for (int i = 0; i < compositions.length; i++) {
//            System.out.println("composition[" + i + "]: " + compositions[i]);
//        }

        return compositions;

    }


    static public void runMallet() throws IOException, InterruptedException {

//        String[] command = new String[2];
//        // transform individual txt files into a single MALLET format file
//        command[0] = "Mallet-202108/bin/mallet import-dir --input Mallet-202108/data-20 --output Mallet-202108/output/input20.mallet --keep-sequence --remove-stopwords";
//        // trains MALLET to find 50 topics
//        command[1] = "Mallet-202108/bin/mallet train-topics  --input Mallet-202108/output/input20.mallet --num-topics 2 --optimize-interval 10 --output-state Mallet-202108/output/topic-state-2.gz --output-topic-keys Mallet-202108/output/keys-2.txt --output-doc-topics Mallet-202108/output/composition-2.txt";

        Runtime rt = Runtime.getRuntime();
        // transform individual txt files into a single MALLET format file
        Process pr = rt.exec("Mallet-202108/bin/mallet import-dir --input Mallet-202108/data --output Mallet-202108/output/input.mallet --keep-sequence");

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
