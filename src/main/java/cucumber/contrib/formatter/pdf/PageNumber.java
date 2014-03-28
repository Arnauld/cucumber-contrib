package cucumber.contrib.formatter.pdf;

import cucumber.contrib.formatter.util.RomanNumeral;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PageNumber {

    private int pageNumber;
    private Sequence pnContent = new Sequence();
    private Sequence pnExtra = new Sequence(0, true);
    private Sequence pnCurrent = pnExtra;
    private List<PageInfos> emittedPageInfos = new ArrayList<PageInfos>();

    public void notifyPageChange(int newPageNumber) {
        if (pageNumber == newPageNumber)
            return; // already notified

        pageNumber = newPageNumber;
        pnCurrent = pnCurrent.next();
    }

    public PageInfos pageInfos() {
        PageInfos pageInfos = new PageInfos(
                pageNumber,
                pnCurrent.formatPageNumber(),
                pnCurrent == pnExtra);

        if (!emittedPageInfos.contains(pageInfos))
            emittedPageInfos.add(pageInfos);

        return pageInfos;
    }

    public List<PageInfos> getEmittedPageInfos() {
        return emittedPageInfos;
    }

    public void continueExtra() {
        pnCurrent.next = pnExtra;
    }

    public void startExtra() {
        pnCurrent.next = pnExtra;
    }

    public void startContent() {
        pnCurrent.next = pnContent;
    }

    private static class Sequence {
        int count = 1;
        Sequence next;
        boolean isRoman;

        public Sequence(int start, boolean isRoman) {
            this.count = start;
            this.isRoman = isRoman;
        }

        public Sequence() {
            this(1, false);
        }

        public String formatPageNumber() {
            if (count == 0)
                count++;
            if (isRoman)
                return new RomanNumeral().format(count);
            else
                return String.valueOf(count);
        }

        public Sequence next() {
            count++;
            if (next != null) {
                Sequence tmp = next;
                next = null;
                return tmp;
            }
            return this;
        }
    }

}
