package cucumber.contrib.junit;

/**
* @author <a href="http://twitter.com/aloyer">@aloyer</a>
*/
public interface DocumentStore {
    void put(String refId, String content);

    String get(String refId);

    String generateRefId();
}
