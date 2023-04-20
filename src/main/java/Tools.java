import com.opencsv.*;

import java.io.*;
import java.util.List;

public class Tools {

    public static void main(String[] args) throws IOException, InterruptedException {

        reorderRaw();
//        reorderClean();
//        extract200();
//        TopicModellingService.prepareTxt();
//        TopicModellingService.getTopicState();
//        TopicModellingService.getDocument();
//        NlpService.getTokensList("src/main/output/testfiles/raw-200-clean.csv");
//        TopicModeller.runMallet();
//        deleteRowinCsv();
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

        String[] header = { "DocId", "PublishDate", "VisitDate", "SiteId", "SiteName", "SiteUrl", "Url", "Content" };
        writer.writeNext(header);

        String[] line;
        while ((line = csvReader.readNext()) != null) {

            String siteUrl = "";
            String siteName = "";
            if (line[0].equals("S1")) {siteUrl = "blog.acthompson.net"; siteName = "Computer Science Teacher";}
            if (line[0].equals("S2")) {siteUrl = "computinged.wordpress.com"; siteName = "Computing Ed Research";}
            if (line[0].equals("S3")) {siteUrl = ""; siteName = "Freedom to Tinker";}
            if (line[0].equals("S4")) {siteUrl = "scottaaronson.blog"; siteName = "Shtetl-Optimized";}
            if (line[0].equals("S5")) {siteUrl = "www.section.io/blog/"; siteName = "Section Blog"; }
            if (line[0].equals("S6")) {siteUrl = "www.hanselminutes.com"; siteName = "Hanselminutes Podcast";}

            String[] record = new String[]{line[1], line[2], line[3], line[0], siteName, siteUrl, line[4], line[5]};

            writer.writeNext(record, true);

        }

        writer.close();

    }

    static void reorderClean() throws IOException {

        int index = 1;

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete-clean-copy.csv"));
        File f = new File("src/main/output/raw-complete-clean.csv");

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

        String[] header = { "DocId", "Tokens" };
        writer.writeNext(header);

        String[] line;
        while ((line = csvReader.readNext()) != null) {

            String[] record = new String[]{"D"+line[0], line[1]};

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

        String[] header = { "DocId", "PublishDate", "VisitDate", "Url", "Content" };
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

    static public void deleteRowinCsv() throws IOException {

        CSVReader reader2 = new CSVReader(new FileReader("src/main/output/raw-complete.csv"));
        List<String[]> allElements = reader2.readAll();
        allElements.remove(2646);
        FileWriter sw = new FileWriter("src/main/output/raw-complete-new.csv");
        CSVWriter writer = new CSVWriter(sw);
        writer.writeAll(allElements);
        writer.close();

    }




}
