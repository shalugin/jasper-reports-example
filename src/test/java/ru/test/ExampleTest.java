package ru.test;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.junit.Ignore;
import org.junit.Test;
import ru.test.ReportBuilder.ReportFormat;

import java.io.*;

import static org.junit.Assert.assertNotNull;
import static ru.test.ReportBuilder.ReportFormat.DOCX;
import static ru.test.ReportBuilder.ReportFormat.PDF;
import static ru.test.ReportBuilder.ReportFormat.XLSX;

@Ignore
public class ExampleTest {

    @Test
    public void testGenerateReportFile1() throws IOException, JRException {
        byte[] report = generateReport(XLSX);
        saveToFile(report, "test.xlsx");
    }

    @Test
    public void testGenerateReportFile2() throws IOException, JRException {
        byte[] report = generateReport(DOCX);
        saveToFile(report, "test.docx");
    }

    @Test
    public void testGenerateReportFile3() throws IOException, JRException {
        byte[] report = generateReport(PDF);
        saveToFile(report, "test.pdf");
    }

    private void saveToFile(byte[] report, String name) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(name)) {
            fileOutputStream.write(report);
            fileOutputStream.flush();
        }
    }

    private byte[] generateReport(ReportFormat format) throws IOException, JRException {
        try (InputStream jrxml = getClass().getResourceAsStream("/example.jrxml")) {
            byte[] source = getSource("/xml/test.xml");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(jrxml, byteArrayOutputStream);
            InputStream reportStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            byte[] report = ReportBuilder.generateReportFile(reportStream, source, format);
            assertNotNull(report);
            return report;
        }
    }

    private byte[] getSource(String name) throws IOException {
        try (InputStream dataStream = getClass().getResourceAsStream(name);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(dataStream)) {
            int i;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((i = bufferedInputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}