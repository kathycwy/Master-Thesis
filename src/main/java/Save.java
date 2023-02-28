import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Save {

    public static void save(String url, String date, String title, String content, int count) throws IOException {

        File f = new File("src/main/output/raw.csv");
        if (!f.exists()) {
            FileWriter file = new FileWriter(f);
            CSVWriter writer = new CSVWriter(file);

            String[] header = { "Count", "Date", "Url", "Content" };
            writer.writeNext(header);

            writer.close();
        }

//        String[] tokens = Tokenizer.tokenize(content);

        CSVWriter writer = new CSVWriter(new FileWriter(f, true));
        String[] record = new String[]{String.valueOf(count), date, url, String.valueOf(content)};
        writer.writeNext(record, true);
        writer.close();

//        System.out.println("[" + count + "] " + url);
//        System.out.println("[" + date + "] " + title);
//        System.out.println(content);

    }

}
