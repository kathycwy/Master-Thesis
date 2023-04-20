import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class GoogleScholarSearch {
    public static void main(String[] args) {
        String searchWord = "neuroscience";
        String searchUrl = "https://scholar.google.com/scholar?q=" + searchWord;

//        // title
//        String searchWord = "neuroscience";
//        String searchUrl = "https://scholar.google.com/scholar?q=intitle:" + searchWord;
//
//        // abstract
//        String searchWord = "neuroscience";
//        String searchUrl = "https://scholar.google.com/scholar?q=inabstract:" + searchWord;
//
//        //full text
//        String searchWord = "neuroscience";
//        String searchUrl = "https://scholar.google.com/scholar?q=allintext:" + searchWord;
//
//        // multiple operator
//        String searchWord = "neuroscience";
//        String searchUrl = "https://scholar.google.com/scholar?q=intitle:" + searchWord + "+AND+inabstract:" + searchWord;



        try {
            Document doc = Jsoup.connect(searchUrl).get();
            Elements resultStats = doc.select("#gs_ab_md");

            String resultStatsText = resultStats.text();
            String[] resultStatsArray = resultStatsText.split(" ");
            String numResults = resultStatsArray[1];

            System.out.println("Number of search results for \"" + searchWord + "\": " + numResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}