package es.ubu.lsi.ubumonitor.export.photos;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.EnrolledUser;

public class UserPhoto {
	private static final int N_COLUMNS = 5;

	public void exportEnrolledUsersPhoto(Course course, List<EnrolledUser> enrolledUsers, File file,
			boolean defaultPhoto) throws IOException {

		byte[] defaultUser = null;

		if (defaultPhoto) {
			BufferedImage bImage = ImageIO.read(getClass().getResource("/img/default_user.png"));
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bImage, "png", bos);
			defaultUser = bos.toByteArray();
		}

		try (FileOutputStream out = new FileOutputStream(file); XWPFDocument document = new XWPFDocument()) {

			XWPFHeaderFooterPolicy headerFooterPolicy = document.getHeaderFooterPolicy();
			if (headerFooterPolicy == null)
				headerFooterPolicy = document.createHeaderFooterPolicy();

			// create header start
			XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

			XWPFParagraph paragraph = header.createParagraph();

			XWPFRun run = paragraph.createRun();
			run.setText(course.getFullName());
			paragraph = header.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.RIGHT);

			run = paragraph.createRun();
			run.setText(LocalDateTime.now()
					.format(Controller.DATE_TIME_FORMATTER));

			XWPFFooter footer = document.createFooter(HeaderFooterType.DEFAULT);

			paragraph = footer.getParagraphArray(0);
			if (paragraph == null)
				paragraph = footer.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			run = paragraph.createRun();

			paragraph.getCTP()
					.addNewFldSimple()
					.setInstr("PAGE \\* ARABIC MERGEFORMAT");
			run = paragraph.createRun();
			run.setText("/");
			paragraph.getCTP()
					.addNewFldSimple()
					.setInstr("NUMPAGES \\* ARABIC MERGEFORMAT");

			XWPFTable tab = document.createTable((int) Math.ceil(enrolledUsers.size() / (double) N_COLUMNS),
					enrolledUsers.size() < N_COLUMNS ? enrolledUsers.size() : N_COLUMNS);
			tab.setCellMargins(50, 0, 0, 0);

			for (int i = 0; i < enrolledUsers.size(); i++) {

				XWPFTableRow row = tab.getRow(i / N_COLUMNS);
				row.setCantSplitRow(false);
				XWPFTableCell cell = row.getCell(i % N_COLUMNS);
				paragraph = cell.getParagraphArray(0);
				paragraph.setAlignment(ParagraphAlignment.CENTER);
				run = paragraph.createRun();
				byte[] bytePhoto = defaultPhoto ? defaultUser
						: enrolledUsers.get(i)
								.getImageBytes();

				run.addPicture(new ByteArrayInputStream(bytePhoto), Document.PICTURE_TYPE_PNG, enrolledUsers.get(i)
						.getId() + ".png", Units.pixelToEMU(80), Units.pixelToEMU(80));
				run.addBreak();
				run.setText(enrolledUsers.get(i)
						.getFullName());

			}
			document.write(out);

		} catch (InvalidFormatException e) {
			throw new IllegalStateException("Invalid format exception");
		}

	}
}
