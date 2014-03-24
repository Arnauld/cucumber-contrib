package cucumber.contrib.formatter.pdf;

import cucumber.contrib.formatter.util.RomanNumeral;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PageNumber {

    private int pagenumber;
    private Sequence pnContent = new Sequence();
    private Sequence pnExtra = new Sequence(0, true);
    private Sequence pnCurrent = pnExtra;

    public void notifyPageChange(int pagenumber) {
        if(this.pagenumber == pagenumber)
            return; // already notified

        this.pagenumber = pagenumber;
        pnCurrent = pnCurrent.next();
    }

    public String formatPageNumber() {
        return pnCurrent.formatPageNumber();
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
            if(count == 0)
                count++;
            if(isRoman)
                return new RomanNumeral().format(count);
            else
                return String.valueOf(count);
        }

        public Sequence next() {
            count++;
            if(next!=null) {
                Sequence tmp = next;
                next = null;
                return tmp;
            }
            return this;
        }
    }

}
