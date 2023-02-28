import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import io.github.pepperkit.corenlp.stopwords.StopWordsAnnotator;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class StopWordsRemover {

    public static Set<String> removeStopWords(String str) {

        // I want to get the list of lemmas created from the text, excluding words from the provided list and all the
        // common or simple words (like propositions, conjunctions, etc.), since I want to extract only the words
        // I could be interested to learn
        String[] expectedWords = {"dear", "look", "have", "give", "riding", "hood", "velvet", "suit", "wear", "call"};

        // And the stop words in resources (containing the most known English words)
        // from https://github.com/powerlanguage/word-lists/blob/master/1000-most-common-words.txt
        final String stopWordsResourcePath = "common-words-list.txt";

        final Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, stopwords");
        props.setProperty("customAnnotatorClass.stopwords", "io.github.pepperkit.corenlp.stopwords.StopWordsAnnotator");
        props.setProperty("ssplit.isOneSentence", "true");

        // to filter out all the punctuation and simple words like be, so, etc.
        props.setProperty("stopwords.withLemmasShorterThan", "3");

        // to filter out all the common and simple words
        // Description of the available POS categories can be found here:
        // - https://nlp.stanford.edu/software/pos-tagger-faq.html
        // - https://catalog.ldc.upenn.edu/docs/LDC99T42/tagguid1.pdf
        props.setProperty("stopwords.withPosCategories",
                "NNP,NNPS," + // proper noun singular and plural
                        "PDT," + // predeterminer
                        "IN,CC," + // conjunction and coordinating conjunction (but, and etc.)
                        "DT," + // determiner - the, a, etc.
                        "UH," + // interjection - my, his, oh, uh etc.
                        "FW," + // foreign word
                        "MD," + // modal verb
                        "RP," + // particle
                        "PRP,PRP$," + // personal pronoun
                        "EX," + // existential there
                        "POS," + // possessive ending: 's
                        "SYM," + // symbol
                        "WDT,WP,WP$," + // wh-determiner (who), wh-pronoun (who, what, whom) and possessive wh-pronoun (whose)
                        "WRB" // wh-adverb
        );

        // provide the file with stop words list
        props.setProperty("stopwords.customListResourcesFilePath", stopWordsResourcePath);

        // Annotate the text using StanfordCoreNLP pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(str);
        pipeline.annotate(document);

        // Process returned tokens
        Set<String> result = new HashSet<>();
        List<CoreLabel> tokens = document.get(CoreAnnotations.TokensAnnotation.class);

        // Return only lemmas of only interesting words
        for (CoreLabel token : tokens) {
            if (!token.get(StopWordsAnnotator.class)) {
                result.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return result;

    }

}
