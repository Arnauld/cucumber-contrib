package cucumber.contrib.formatter.pdf;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PageInfos {
    private final int rawPageNumber;
    private final String formattedPageNumber;
    private final boolean extra;

    public PageInfos(int rawPageNumber, String formattedPageNumber, boolean isExtra) {
        this.rawPageNumber = rawPageNumber;
        this.formattedPageNumber = formattedPageNumber;
        this.extra = isExtra;
    }

    public String getFormattedPageNumber() {
        return formattedPageNumber;
    }

    public int getRawPageNumber() {
        return rawPageNumber;
    }

    public boolean isExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "PageInfos{" +
                "raw: " + rawPageNumber +
                ", formatted: '" + formattedPageNumber + '\'' +
                ", xtra=" + extra +
                '}';
    }
}
