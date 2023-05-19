import com.opencsv.*;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import org.neo4j.driver.types.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Neo4jConnector implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(Neo4jConnector.class.getName());
    private final Driver driver;

    public Neo4jConnector(String uri, String user, String password, Config config) {
        // The driver is a long living object and should be opened during the start of your application
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
    }

    @Override
    public void close() {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    public static void prepareDbDataSplit() throws IOException {

        ArrayList<ArrayList<String>> topicState = TopicModellingService.getTopicState();
//        List<String[]> documents = TopicModellingService.getDocument();

        int i = 0;
        int name = 0;
        int loop = 0;

        while (i < topicState.size()) {

            File f = new File("src/main/output/word-topic-doc-db-" + name + ".csv");

            FileWriter file = new FileWriter(f);
            CSVWriter writer = new CSVWriter(file);

            String[] header = {"wordId", "word", "docId", "topicId"};
            writer.writeNext(header);

            while (loop < 50000) {

                ArrayList<String> line = topicState.get(i++);

                String[] record = new String[]{line.get(3), line.get(4), line.get(1), line.get(5)};

                writer.writeNext(record, true);

                loop++;
            }
            writer.close();

            loop = 0;
            name++;
            System.out.println("created word-topic-doc-db-" + name + ".csv");
        }
    }

    public static void prepareDbDataAll() throws IOException {

        ArrayList<ArrayList<String>> topicState = TopicModellingService.getTopicState();
//        List<String[]> documents = TopicModellingService.getDocument();

        File f = new File("src/main/output/word-topic-doc-db.csv");

        FileWriter file = new FileWriter(f);
        CSVWriter writer = new CSVWriter(file);

        String[] header = {"wordId", "word", "docId", "topicId"};
        writer.writeNext(header);

        for (ArrayList<String> line : topicState) {

            String[] record = new String[]{line.get(3), line.get(4), line.get(1), line.get(5)};

            writer.writeNext(record, true);

        }
        writer.close();


    }

    public void getWordIdList() {

        Query query = new Query(
                """
                          MATCH (n:Word)-[:belongs_to]->(t:Topic)
                          WITH n.wordId AS wordId, n.text AS text, t.topicId AS topicId
                          RETURN wordId, topicId, text
                          ORDER BY wordId ASC;
                        """);

        try (Session session = driver.session()) {

            String[] wordArray = new String[19127];
            Arrays.fill(wordArray, "");
            String[] topicArray = new String[19127];
            Arrays.fill(topicArray, "");

            // Write transactions allow the driver to handle retries and transient errors
            Stream<Map<String, Object>> stream = session.executeRead(tx -> {
                Result result = tx.run(query);

                while (result.hasNext()) {
                    Record record = result.next();
                    int wordId = Integer.parseInt(record.get("wordId").asString().substring(1));
                    String topicId = record.get("topicId").asString();
                    String text = record.get("text").asString();
                    wordArray[wordId] = text;
                    topicArray[wordId] = (topicArray[wordId].isEmpty() ? topicId : topicArray[wordId]);
                }

                FileWriter writer = null;
                try {
                    writer = new FileWriter("src/main/output/calWeakSignals/wordArray.txt");
                    int wordArrayLen = wordArray.length;
                    for (int i = 0; i < wordArrayLen; i++) {

                        writer.write(wordArray[i] + " ");
                    }

                    writer.close();

                    writer = new FileWriter("src/main/output/calWeakSignals/topicArray.txt");
                    int topicLen = topicArray.length;
                    for (int i = 0; i < topicLen; i++) {

                        writer.write(topicArray[i] + " ");
                    }

                    writer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(Arrays.toString(wordArray));
                System.out.println(Arrays.toString(topicArray));

                return result.list(r -> r.asMap()).stream();});

//

            session.close();



        } catch (Neo4jException ex) {
            // capture any errors along with the query and data for traceability
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }

    }

    public int[] getNumOfDocWordAppears() {

        int year = 2018;
//        int endYear = year + 1;
        int endYear = 2023;

        Query query = new Query(
                """
                          MATCH (w:Word)-[:found_in]->(d:Document)
                          WHERE d.publishDate >= date({year: $year}) AND d.publishDate < date({year: $endYear})
                          WITH w.wordId AS wordId, count(d) AS numDocs
                          RETURN wordId, numDocs;
                        """,
                Map.of("year", year, "endYear", endYear));

//        try (var session = driver.session(SessionConfig.forDatabase("neo4j"))) {
//            var record = session.executeRead(tx -> tx.run(query));
//            System.out.printf("wordId: ", record.get("wordId").asString());
//            // You should capture any errors along with the query and data for traceability
//        } catch (Neo4jException ex) {
//            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
//            throw ex;
//        }

//        Result result;

        try (Session session = driver.session()) {

            int[] numDocsArray = new int[19127];
            Arrays.fill(numDocsArray, 0);

            // Write transactions allow the driver to handle retries and transient errors
            Stream<Map<String, Object>> stream = session.executeRead(tx -> {
                Result result = tx.run(query);

                ArrayList<String[]> resultList = new ArrayList<>();
                String[] line = new String[2];



                while (result.hasNext()) {
                    Record record = result.next();
                    int wordId = Integer.parseInt(record.get("wordId").asString().substring(1));
                    int numDocs = record.get("numDocs").asInt();
                    numDocsArray[wordId] = numDocs;
//                    line[0] = wordId;
//                    line[1] = String.valueOf(numDocs);
//                    resultList.add(line);
                }

                FileWriter writer = null;
                try {
//                    writer = new FileWriter("src/main/output/calWeakSignals/numDocsArray" + year + ".txt");
                    writer = new FileWriter("src/main/output/calWeakSignals/numDocsArray.txt");
                    int len = numDocsArray.length;
                    for (int i = 0; i < len; i++) {

                        writer.write(numDocsArray[i] + " ");
                    }

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(Arrays.toString(numDocsArray));

                return result.list(r -> r.asMap()).stream();});

//

            session.close();



            return numDocsArray;


        } catch (Neo4jException ex) {
            // capture any errors along with the query and data for traceability
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }

    }

//    public static int[] getNumDocsArray() {
//
//        return numDocsArray;
//    }



    public void createTopicAndWord() throws Exception {

        System.out.println("Start Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

        for (int i = 0; i < 6; i++) {

            System.out.print("Start sending Cypher " + i + "...  ");
            String url = "https://raw.githubusercontent.com/kathycwy/Master-Thesis/master/src/main/output/word-topic-doc-db-" + i + ".csv";

            Query query = new Query(
                """
                           LOAD CSV WITH HEADERS FROM $url AS line
                           MERGE (t:Topic { topicId: line.topicId })
                           MERGE (w:Word { wordId: line.wordId, text: line.word })
                           MERGE (w)-[:belongs_to]->(t)
                           WITH w, line
                           MATCH (d:Document WHERE d.docId = line.docId)
                           MERGE (w)-[:found_in]->(d);
                        """,
                    Map.of("url", url));
            runCypher(query);

            System.out.println("Completed");
        }

        System.out.println("End Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

    }

    public void createWebsiteAndDocument() {

        System.out.println("Start Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

        System.out.print("Start sending Cypher...  ");

        Query query = new Query(
            """
            LOAD CSV WITH HEADERS FROM 'https://raw.githubusercontent.com/kathycwy/Master-Thesis/master/src/main/output/raw-200.csv' AS line
            MERGE (s:Website { siteId: line.SiteId, siteName: line.SiteName, siteUrl: line.SiteUrl })
            CREATE (d:Document { docId: line.DocId, publishDate: date(line.PublishDate), visitDate: date(line.VisitDate), url: line.Url })
            CREATE (s)-[:contains]->(d);
            """
        );
        runCypher(query);

        System.out.println("Completed");

        System.out.println("End Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

    }

    public void createScore() throws Exception {

        System.out.println("Start Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

        System.out.print("Start sending Cypher...  ");
        String url = "https://raw.githubusercontent.com/kathycwy/Master-Thesis/master/src/main/output/calWeakSignals/WeakSignalValues_16.csv";

        Query query = new Query(
                """
                           LOAD CSV WITH HEADERS FROM $url AS line
                           MATCH (w:Word {wordId: line.wordId})
                           SET w.score = line.score;
                        """,
                Map.of("url", url));
        runCypher(query);

        System.out.println("Completed");

        System.out.println("End Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

    }

    public void createTopicLabel() throws Exception {

        System.out.println("Start Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

        System.out.print("Start sending Cypher...  ");
        String url = "https://raw.githubusercontent.com/kathycwy/Master-Thesis/master/Mallet-202108/output/keys_topic_label.csv";

        Query query = new Query(
                """
                           LOAD CSV FROM $url AS line
                           MATCH (t:Topic {topicId: line[0]})
                           SET t.label = line[1];
                        """,
                Map.of("url", url));
        runCypher(query);

        System.out.println("Completed");

        System.out.println("End Time: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new java.util.Date()));

    }

    public Result runCypher(final Query query) {

        Result result;

        try (Session session = driver.session()) {

            // Write transactions allow the driver to handle retries and transient errors
            result = session.executeWrite(tx -> tx.run(query));

        } catch (Neo4jException ex) {
            // capture any errors along with the query and data for traceability
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }

        return result;
    }


    public static void main(String... args) throws IOException {
        // Aura queries use an encrypted connection using the "neo4j+s" protocol
        String uri = "neo4j+s://7ffde07c.databases.neo4j.io:7687";
        String user = "neo4j";
        String password = "password";

        try (Neo4jConnector app = new Neo4jConnector(uri, user, password, Config.defaultConfig())) {

//            app.createWebsiteAndDocument();
//            app.createTopicAndWord();
//            app.getNumOfDocWordAppears();
//            app.getWordIdList();
            app.createScore();
//            app.createTopicLabel();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}