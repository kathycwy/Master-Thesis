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

    public static void prepareDbData() throws IOException {

        ArrayList<ArrayList<String>> topicState = TopicModellingService.getTopicState();
//        List<String[]> documents = TopicModellingService.getDocument();

        File f = new File("src/main/output/word-topic-doc-db.csv");

        FileWriter file = new FileWriter(f);
        CSVWriter writer = new CSVWriter(file);

        String[] header = { "wordId", "word", "docId", "topicId" };
        writer.writeNext(header);

        for (ArrayList<String> line : topicState) {

            String[] record = new String[]{line.get(3), line.get(4), line.get(1), line.get(5)};

            writer.writeNext(record, true);
        }

        writer.close();


    }

    public void createTopicAndWord() throws IOException {

        Query query = new Query(
                """
                LOAD CSV WITH HEADERS FROM 'https://raw.githubusercontent.com/kathycwy/Master-Thesis/master/src/main/output/word-topic-doc-db.csv' AS row
                MERGE (t:Topic { topicId: row.topicId })
                MERGE (w:Word { wordId: row.wordId, text: row.word })
                MERGE (w)-[:belongs_to]->(t)
                WITH w
                MATCH (d:Document WHERE d.docId = row.docId)
                MERGE (w)-[:found_in]->(d);
                """);
        runCypher(query);

//        System.out.println("Creating Topic and Word nodes completed");

    }

    public void createWebsiteAndDocument() {

        try {

            BufferedReader br = new BufferedReader(new FileReader("src/main/output/raw-complete.csv"));

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreQuotations(false)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(br)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line != null) {
                    Query query = new Query(
                        """
                        MERGE (s:Website { siteId: $siteId, siteName: $siteName, siteUrl: $siteUrl })
                        CREATE (d:Document { docId: $docId, publishDate: date($publishDate), visitDate: date($visitDate), url: $url })
                        CREATE (s)-[:contains]->(d);
                        """,
                        Map.of("siteId", line[3], "siteName", line[4], "siteUrl", line[5], "docId", line[0], "publishDate", line[1], "visitDate", line[2], "url", line[6] )
                    );
                    runCypher(query);
                    System.out.println("Creating nodes for Website " + line[3] + " and Document " + line[0] + "...");
                }
            }

            System.out.println("Creating Website and Document nodes completed");

        } catch (IOException e) {
            System.out.println("Error - Input source not found.");
        }
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
        String uri = "neo4j+s://b6b5a73b.databases.neo4j.io:7687";
        String user = "neo4j";
        String password = "password";

        ArrayList<ArrayList<String>> topicStates = TopicModellingService.getTopicState();
//        ArrayList<ArrayList<String>> tokensList = NlpService.getTokensList("src/main/output/testfiles/raw-200-clean.csv");

        try (Neo4jConnector app = new Neo4jConnector(uri, user, password, Config.defaultConfig())) {

//            app.createWebsiteAndDocument();
            app.createTopicAndWord();
//            int id = 1;
//            for (ArrayList<String> tokenLine : tokensList) {
//                for (String token : tokenLine) {
//                    app.createTopic("T" + id, token);
//                    System.out.println("Creating nodes for Topic " + id++ + "...");
//                }
//            }
//            System.out.println("Total " + id + " Topic nodes are created");
        }
    }
}