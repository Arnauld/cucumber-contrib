package cucumber.contrib.formatter.pdf;

import com.google.common.base.Strings;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 *
 */
public class DefaultFirstPageContentProvider implements ContentUpdater {

    @Override
    public void update(Configuration configuration, Document document) throws DocumentException {
        int data = 0;
        Paragraph preface = new Paragraph();
        Paragraph lastLine = Configuration.addEmptyLines(preface, 1);
        lastLine.setSpacingAfter(200.0f);

        String title = configuration.getTitle();
        if (!Strings.isNullOrEmpty(title)) {
            Font font = configuration.mainTitleFont();
            Paragraph paragraph = new Paragraph(title, font);
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(15.0f);
            LineSeparator line = new LineSeparator(1, 45, font.getColor(), Element.ALIGN_RIGHT, -10);
            paragraph.add(line);
            preface.add(paragraph);
            data++;
        }

        String subject = configuration.getSubject();
        if (!Strings.isNullOrEmpty(subject)) {
            Paragraph paragraph = new Paragraph(subject, configuration.subTitleFont());
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(10.0f);
            //
            preface.add(paragraph);
            data++;
        }

        String version = configuration.getVersion();
        if (!Strings.isNullOrEmpty(version)) {
            Paragraph paragraph = new Paragraph(version, configuration.versionTitleFont());
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(10.0f);
            preface.add(paragraph);
            data++;
        }

        document.add(preface);
        if (data > 0) {
            document.newPage();
        }
    }
}
