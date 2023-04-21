import com.opencsv.*;
import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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




    public void createTopicAndWord() throws Exception {

        for (int i = 0; i < 8; i++) {

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

    }

    public void createWebsiteAndDocument() {

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

    }

    public void runCypher(final Query query) {

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            var record = session.executeWrite(tx -> tx.run(query));
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }
    }


    public static void main(String... args) throws IOException {
        // Aura queries use an encrypted connection using the "neo4j+s" protocol
        String uri = "neo4j+s://7ffde07c.databases.neo4j.io:7687";
        String user = "neo4j";
        String password = "password";

        try (Neo4jConnector app = new Neo4jConnector(uri, user, password, Config.defaultConfig())) {

//            app.createWebsiteAndDocument();
            app.createTopicAndWord();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}