import java.io.File;
import java.util.ArrayList;

public class CrawlService {

    public static WebsiteDetailsClass[] websiteDetails = new WebsiteDetails().getAllWebsiteDetails();

    public static void main(String[] args) {

        File f = new File("src/main/output/raw.csv");
        if (f.delete()) {
            System.out.println("Old CSV file deleted");
        }

        for (WebsiteDetailsClass site : websiteDetails) {
            Crawler.crawl(1, site, site.rootUrl, new ArrayList<String>());
        }

    }

}
