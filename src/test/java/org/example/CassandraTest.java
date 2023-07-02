package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.junit.AfterClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
public class CassandraTest {
    @Container
    public static CassandraContainer cassandraContainer = (CassandraContainer) new CassandraContainer("cassandra:4.1.2").withExposedPorts(9042);

    @Autowired
    private ReviewRepository reviewRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        cassandraContainer.start();
        registry.add("spring.cassandra.contact-points", () -> cassandraContainer.getContactPoint().getAddress().getHostAddress());
        registry.add("spring.cassandra.port", () -> cassandraContainer.getMappedPort(9042));
        registry.add("spring.cassandra.local-datacenter", () -> cassandraContainer.getLocalDatacenter());
    }

    @BeforeAll
    static void init() {

        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(cassandraContainer.getContactPoint().getAddress().getHostAddress(),
                        cassandraContainer.getMappedPort(9042)))
                .withLocalDatacenter(cassandraContainer.getLocalDatacenter())
                .build();

        String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS reviews " +
                "WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};";
        String createReviewsTable = "CREATE COLUMNFAMILY IF NOT EXISTS reviews.review (productId text PRIMARY KEY, averageReviewScore double, numberOfReviews int);";

        session.execute(createKeyspace);
        session.execute(createReviewsTable);
    }

    @AfterEach
    void after() {
        reviewRepository.deleteAll();
    }

    @Test
    void testInsertDb() {
        Review entity = new Review("asd", 0.1, 5);
        reviewRepository.save(entity);
        Review review = reviewRepository.findById("asd").get();
        assertEquals(entity, review);
    }

    @Test
    void testUpdateDb() {
        Review entity = new Review("asd", 0.1, 5);
        reviewRepository.save(entity);
        entity.setNumberOfReviews(6);
        reviewRepository.save(entity);
        Review review = reviewRepository.findById("asd").get();
        assertEquals(6, review.getNumberOfReviews());
    }

    @Test
    void testDeleteDb() {
        Review entity = new Review("asd", 0.1, 5);
        reviewRepository.save(entity);
        reviewRepository.deleteById("asd");
        assertFalse(reviewRepository.existsById("asd"));
    }

    @AfterClass
    public static void down() {
        cassandraContainer.stop();
    }
}
