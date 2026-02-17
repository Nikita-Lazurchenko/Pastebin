package pet.project.storage.hash_genarator;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import pet.project.PastebinApplication;
import pet.project.storage.GeneratorHash;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@SpringBootTest(classes = PastebinApplication.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class GenerateHashTest {
    private final GeneratorHash generatorHash;

    @Test
    void shouldReturnValidHash() {
        String hash = generatorHash.getHash();

        assertNotNull(hash);
        assertEquals(10, hash.length());
    }

    @Test
    void shouldGenerateUniqueHashes() {
        Set<String> hashes = new HashSet<>();
        int count = 100;

        for (int i = 0; i < count; i++) {
            String hash = generatorHash.getHash();
            assertFalse(hashes.contains(hash));
            hashes.add(hash);
        }

        assertEquals(count, hashes.size());
    }

    @Test
    void shouldHandlePoolExhaustion() {
        for (int i = 0; i < 50; i++) {
            generatorHash.getHash();
        }

        String hashAfterRefill = generatorHash.getHash();
        assertNotNull(hashAfterRefill);
    }

    @Test
    void testConcurrentHashGeneration() throws InterruptedException {
        int threadCount = 20;
        int requestsPerThread = 50;
        int totalRequests = threadCount * requestsPerThread;

        Set<String> allHashes = Collections.synchronizedSet(new HashSet<>());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(totalRequests);

        for (int i = 0; i < totalRequests; i++) {
            executor.execute(() -> {
                try {
                    startLatch.await();
                    String hash = generatorHash.getHash();
                    if (hash != null) {
                        allHashes.add(hash);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println(allHashes.size());
        System.out.println(totalRequests);
        assertEquals(totalRequests, allHashes.size());
    }
}
