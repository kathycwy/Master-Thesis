import com.opencsv.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tools {

    public static void main(String[] args) throws IOException, InterruptedException {

//        tableOutput();
        tableOutputKemKim();
//        reorderRaw();
//        reorderClean();
//        extract200();
//        TopicModellingService.prepareTxt();
//        TopicModellingService.getTopicState()
//        Neo4jConnector.prepareDbDataSplit();
//        Neo4jConnector.prepareDbDataAll();
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

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-150.csv"));
        File f = new File("src/main/output/raw-150-ok.csv");

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

        int docId = 0;
        String[] line;
        while ((line = csvReader.readNext()) != null) {

            String siteId = "";
            String siteUrl = "";
            String siteName = "";
            if (line[3].contains("blog.acthompson.net")) {siteId = "S1"; siteUrl = "blog.acthompson.net"; siteName = "Computer Science Teacher";}
            if (line[3].contains("computinged.wordpress.com")) {siteId = "S2"; siteUrl = "computinged.wordpress.com"; siteName = "Computing Ed Research";}
            if (line[3].contains("freedom-to-tinker.com")) {siteId = "S3"; siteUrl = "freedom-to-tinker.com"; siteName = "Freedom to Tinker";}
            if (line[3].contains("scottaaronson.blog")) {siteId = "S4"; siteUrl = "scottaaronson.blog"; siteName = "Shtetl-Optimized";}
            if (line[3].contains("www.section.io/blog/")) {siteId = "S5"; siteUrl = "www.section.io/blog/"; siteName = "Section Blog"; }
            if (line[3].contains("app.podscribe.ai")) {siteId = "S6"; siteUrl = "www.hanselminutes.com"; siteName = "Hanselminutes Podcast";}

            String[] record = new String[]{"D"+docId++, line[1], line[2], siteId, siteName, siteUrl, line[3], line[4]};

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

    static void tableOutput() {

        String[] str = {"0.024350156397071707", "0.01190118193045577", "0.003893065298512115", "0.0038651969378106633", "0.10507042742083379", "0.1389225267167732", "0.04892486958573556", "1.473014874844223", "0.0054026973802518774", "0.07822806749228811", "0.008854032990114719", "0.003369259978455782", "0.005411065974565124", "0.0046453304447834414", "0.007631730750505223", "0.012809453543364758", "0.0032763905602520645", "0.008027364106247837", "0.0255552355071122", "0.05768099510772615", "0.5596221364552824", "0.003893364698446832", "0.07095865927757601", "0.0031411086164720116", "0.005056669870592032", "0.0749613730954012", "0.01705962306064527", "0.0033911872278640817", "0.087462766408554", "0.003219129776844429", "0.009458453368525584", "0.0046831356888008685", "0.005408409063710223", "0.006558720686627553", "0.0015763779176009767", "0.0072023500354614885", "0.1476240138269329", "0.00768499806351681", "0.011866483181941034", "0.004543416497854436", "0.058898977986733825", "0.00644056831267982", "0.1608842971285247", "0.012931164930411871", "0.013835667944300698", "0.1836504606305236", "0.004157264876577076", "0.054327930780342294", "0.006940334703330411", "0.34896037995525975", "0.0052112531912002", "0.0070680097569369915", "0.004278446173061027", "0.33659094762686975", "0.19087946554228563", "0.018575803766508074", "0.5289515993842987", "0.042454492928553425", "0.0076082964006618965", "0.035026359182725945", "0.02897156112534007", "1.1613216238926158", "0.006682661017860367", "0.0035515491032759973", "0.82916273501921", "0.00825869769461589", "0.011923193321345772", "0.012673554004547009", "0.05532776386381378", "0.04993248911201267", "0.004252211870717551", "0.16982315054357772", "0.007330861443428341", "0.005261056242320526", "0.021994403387895423", "0.004849010044605951", "0.24748272082323383", "0.020266513583670702", "0.013168515778251073", "0.26554192168971447", "0.0037792145428644447", "0.07435338516848645", "0.0028855530126138057", "0.0062029871946988595", "1.1569706866338747", "0.022841204236464872", "0.006034720414310844", "0.004655700093882733", "0.010561377002272011", "0.006773770065098158", "0.044882620445099684", "0.005929551253559441", "0.00625467020014273", "0.005016831182627296", "0.005434871640578475", "0.011276700922218282", "0.19840032039949393", "0.0078097662142118385", "0.006608036290810665", "0.005565780872770736", "", "", "", "", "", "", "", "", ""};

        for (int i = 1; i <= 34; i++) {

            System.out.println(
                    Integer.valueOf(i) + " & " + str[i-1] + " & " +
                    Integer.valueOf(i+34) + " & " + str[i+33] + " & " +
                    Integer.valueOf(i+68) + " & " + str[i+67] + "\\\\");
        }

    }

    static void tableOutputTop50Scores() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader("src/main/output/calWeakSignals/top_50_scores.csv"));

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(br)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        String[] line;
        String[] word = new String[50];
        String[] score = new String[50];
        int index = 0;
        while ((line = csvReader.readNext()) != null) {
            if (line != null) {
                word[index] = line[1];
                score[index++] = line[2];
            }
        }
        for (int i = 0; i < 25; i++) {

            System.out.println(
                    word[i] + " & " + score[i] + " & " +
                            word[i+25] + " & " + score[i+25] +" \\\\");

        }

    }

    static void tableOutputKemKim() throws IOException {

        BufferedReader br1 = new BufferedReader(new FileReader("src/main/output/calWeakSignals/plot_kem.csv"));
        BufferedReader br2 = new BufferedReader(new FileReader("src/main/output/calWeakSignals/plot_kim_2.csv"));

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader1 = new CSVReaderBuilder(br1)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        CSVReader csvReader2 = new CSVReaderBuilder(br2)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        String[] line;
        String[] kem = new String[4005];
        String[] kim = new String[860];
        int index = 0;
        while ((line = csvReader1.readNext()) != null) {
            if (line != null) {
                kem[index++] = line[2];
            }
        }
        System.out.println("KEM: " + --index + Arrays.toString(kem));

        index = 0;
        while ((line = csvReader2.readNext()) != null) {
            if (line != null) {
                kim[index++] = line[2];
            }
        }
        System.out.println("KIM: " + --index + Arrays.toString(kim));

        String[] identical = new String[5000];
        index = 0;

        Set<String> set = new HashSet<>();

        for (String element : kem) {
            set.add(element);
        }

        for (String element : kim) {
            if (set.contains(element)) {
                identical[index++] = element;
            }
        }

        System.out.println("identical: " + --index + Arrays.toString(identical));

//        for (int i = 0; i < 25; i++) {
//
//            System.out.println(
//                    kem[i] + " & " + kim[i] + " & " +
//                            kem[i+25] + " & " + kim[i+25] +" \\\\");
//
//        }

    }




}
