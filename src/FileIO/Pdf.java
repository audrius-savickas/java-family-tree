package FileIO;

import Main.Controller;
import Tree.TreeNode;
import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Pdf {

    public static void saveToPdf(AnchorPane anchorPane) throws IOException {
        WritableImage image = takeSnapshot(anchorPane);
        File file = new File("Image.png");

        String dest = "sample.pdf";
//        PdfWriter writer = new PdfWriter(dest);
//        PdfDocument pdfDoc = new PdfDocument(writer);
//        pdfDoc.addNewPage();
//        Document document = new Document(pdfDoc);
//        document.close();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteArrayOutputStream);

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        PDImageXObject pdimage;
        PDPageContentStream content;

        pdimage = PDImageXObject.createFromFile("Image.png", doc);
        file.delete();
        content = new PDPageContentStream(doc, page);
        content.drawImage(pdimage, 10, (float) (800 - (pdimage.getHeight() / 3.2)), 600, (float) (pdimage.getHeight() / 3.2));
        content.close();

        PDPage page2 = new PDPage();
        PDPageContentStream content2 = new PDPageContentStream(doc, page2);
        BaseTable table = new BaseTable((float) (700), 700, 70, 500, 50, doc, page2, true, true);
        Row<PDPage> headerRow = table.createRow(10f);

        Cell<PDPage> cell = headerRow.createCell(5, "ID");
        headerRow.createCell(15, "First name");
        headerRow.createCell(15, "Last name");
        headerRow.createCell(15, "Birth place");
        headerRow.createCell(15, "Birth date");
        headerRow.createCell(10, "Parent IDs");
        headerRow.createCell(10, "Children IDs");
        headerRow.createCell(10, "Spouse ID");
        headerRow.createCell(8, "Level");
        table.addHeaderRow(headerRow);


        Row<PDPage> row = table.createRow(8);
        for (Map.Entry<Integer, TreeNode> entry : Controller.people.entrySet()) {
            int key = entry.getKey();
            TreeNode value = entry.getValue();
            ArrayList<Integer> childrenIds = value.info.getChildren();
            ArrayList <Integer> parentIds = value.info.getParents();

            row.createCell(5, String.valueOf(key));
            row.createCell(15, value.info.getFirstName());
            row.createCell(15, value.info.getLastName());
            row.createCell(15, value.info.getBirthCity());
            row.createCell(15, value.info.getBirthDate().toString());
            row.createCell(10, value.info.getParents().toString().replaceAll("\\[", "").replaceAll("]",""));
            row.createCell(10, value.info.getChildren().toString().replaceAll("\\[", "").replaceAll("]",""));
            row.createCell(10, String.valueOf(value.info.getSpouseId()));
            row.createCell(8, String.valueOf(value.level));


            row = table.createRow(8);
        }
        table.draw();

        content2.close();
        doc.addPage(page);
        doc.addPage(page2);
        doc.save("FamilyTree.pdf");
        doc.close();
    }

    public static WritableImage takeSnapshot(AnchorPane anchorPane) throws IOException {
        return anchorPane.snapshot(new SnapshotParameters(), null);
    }
}
