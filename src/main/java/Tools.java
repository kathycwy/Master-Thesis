import com.opencsv.*;

import java.io.*;

public class Tools {

    public static void main(String[] args) throws IOException, InterruptedException {

//        reorderRaw();
//        extract200();
        TopicModellingService.prepareTxt();
//        TopicModeller.runMallet();

//        long lines = 0;
//
//        try (InputStream is = new BufferedInputStream(new FileInputStream("src/main/output/raw.csv"))) {
//            byte[] c = new byte[1024];
//            int count = 0;
//            int readChars = 0;
//            boolean endsWithoutNewLine = false;
//            while ((readChars = is.read(c)) != -1) {
//                for (int i = 0; i < readChars; ++i) {
//                    if (c[i] == '\n')
//                        ++count;
//                }
//                endsWithoutNewLine = (c[readChars - 1] != '\n');
//            }
//            if (endsWithoutNewLine) {
//                ++count;
//            }
//            lines = count;
//
//            System.out.println("count: " + count);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }


    static void reorderRaw() throws IOException {

        int index = 1;

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete-copy.csv"));
        File f = new File("src/main/output/raw-complete.csv");

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(br)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();


        FileWriter file = new FileWriter(f);
        CSVWriter writer = new CSVWriter(file);

        String[] header = { "Index", "PublishDate", "VisitDate", "Url", "Content" };
        writer.writeNext(header);

        String[] line;
        while ((line = csvReader.readNext()) != null) {

            String[] record = new String[]{String.valueOf(index++), line[1], line[2], line[3], line[4]};

            writer.writeNext(record, true);

        }

        writer.close();

    }


    static void extract200() throws IOException {

        int index = 1;

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete-copy.csv"));
        File f = new File("src/main/output/raw-200.csv");

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(br)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();


        FileWriter file = new FileWriter(f);
        CSVWriter writer = new CSVWriter(file);

        String[] header = { "Index", "PublishDate", "VisitDate", "Url", "Content" };
        writer.writeNext(header);

        String[] line;
        while ((line = csvReader.readNext()) != null && index <= 200) {
            if (line != null) {

                String[] record = new String[]{String.valueOf(index++), line[1], line[2], line[3], line[4]};

                writer.writeNext(record, true);

            }
        }

        writer.close();

    }




}
