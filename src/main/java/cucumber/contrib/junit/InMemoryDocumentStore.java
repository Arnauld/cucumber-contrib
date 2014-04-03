package cucumber.contrib.junit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class InMemoryDocumentStore implements DocumentStore {

    private AtomicInteger idGen = new AtomicInteger();
    private Map<String,String> data = new ConcurrentHashMap<String,String>();

    @Override
    public void put(String refId, String content) {
        if(refId==null)
            throw new IllegalArgumentException("RefId cannot be null");
        data.put(refId, content);
    }

    @Override
    public String get(String refId) {
        return data.get(refId);
    }

    @Override
    public String generateRefId() {
        return "{{" + idGen.incrementAndGet() + "}}";
    }
}
