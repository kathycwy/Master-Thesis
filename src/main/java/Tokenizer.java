import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Tokenizer {

    public static String[] tokenize(String str) {

//        StringTokenizer st = new StringTokenizer(str);
//
//        String[] tokens = new String[st.countTokens()];
//
//        for (int i = 1; st.hasMoreTokens(); i++) {
//            tokens[i-1] = st.nextToken();
//        }
//        return tokens;

        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize");
        // example of how to customize the PTBTokenizer (these are just random example settings!!)
        props.setProperty("tokenize.options", "splitHyphenated=false,americanize=false");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create a document object
        CoreDocument doc = new CoreDocument(str);
        // annotate
        pipeline.annotate(doc);
        // display tokens
        String[] tokens = new String[doc.tokens().size()];
        int i = 0;
        for (CoreLabel tok : doc.tokens()) {
            tokens[i++] = tok.word();
//            System.out.print(tok.word());
        }
//        System.out.println();

        return tokens;

    }

}
