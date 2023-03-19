public class CrawlService {

    public static WebsiteDetailsClass[] websiteDetails = new WebsiteDetails().getAllWebsiteDetails();

    public static void main(String[] args) throws Exception {

//        File f = new File("src/main/output/raw.csv");
//        if (f.delete()) {
//            System.out.println("Old CSV file deleted");
//        }
//
//        for (WebsiteDetailsClass site : websiteDetails) {
//            Crawler.crawl(1, site, site.rootUrl, new ArrayList<String>());
//        }

        Crawler2.getHTML("https://backend.podscribe.ai/api/series/extra?id=2017&numEpisodes=100000");

    }

}
