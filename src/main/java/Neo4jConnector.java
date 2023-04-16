import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

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

    public void createPair(final String topic, final String link) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        Query query = new Query(
                """
                CREATE (p1:Topic { name: $topic1_name })
                CREATE (p2:Link { name: $link1_name })
                CREATE (p1)-[:containsIn]->(p2)
                RETURN p1, p2
                """,
                Map.of("topic1_name", topic, "link1_name", link));

        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
            // Write transactions allow the driver to handle retries and transient errors
            var record = session.executeWrite(tx -> tx.run(query).single());
            System.out.printf(
                    "Created pair for : %s, %s%n",
                    record.get("p1").get("name").asString(),
                    record.get("p2").get("name").asString());
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
            throw ex;
        }
    }

//    public void findPerson(final String personName) {
//        Query query = new Query(
//                """
//                MATCH (p:Person)
//                WHERE p.name = $person_name
//                RETURN p.name AS name
//                """,
//                Map.of("person_name", personName));
//
//        try (Session session = driver.session(SessionConfig.forDatabase("neo4j"))) {
//            var record = session.executeRead(tx -> tx.run(query).single());
//            System.out.printf("Found person: %s%n", record.get("name").asString());
//            // You should capture any errors along with the query and data for traceability
//        } catch (Neo4jException ex) {
//            LOGGER.log(Level.SEVERE, query + " raised an exception", ex);
//            throw ex;
//        }
//    }

    public static void main(String... args) {
        // Aura queries use an encrypted connection using the "neo4j+s" protocol
        String uri = "neo4j+s://e7aa7ab1.databases.neo4j.io:7687";
        String user = "neo4j";
        String password = "password";

        try (Neo4jConnector app = new Neo4jConnector(uri, user, password, Config.defaultConfig())) {
            app.createPair("Computer", "https://blog.net/123.html");
//            app.findPerson("Alice");
        }
    }
}