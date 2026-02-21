package pet.project.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class GeneratorHash {
    private final BlockingQueue<String> pool = new LinkedBlockingQueue<>();
    private static int SIZE = 50;

    @PostConstruct
    public void init() {
        generateHashPool(SIZE);
    }

    public synchronized String getHash() {
        if (pool.isEmpty()){
            generateHashPool(SIZE);
        }

        return pool.poll();
    }

    private void generateHashPool(int count) {
        for (int i = 0; i < count; i++) {
            pool.offer(generateRawHash());
        }
    }

    private String generateRawHash() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(bb.array())
                .substring(0, 15);
    }

}
