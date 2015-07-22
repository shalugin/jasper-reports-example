package ru.test;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class ReportBuilder {

    public enum ReportFormat {
        XLSX,
        DOCX,
        PDF
    }

    public static byte[] generateReportFile(InputStream reportStream, byte[] sourceData,
                                            ReportFormat reportFormat) throws JRException {
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        if (jasperReport == null) {
            throw new JRException("Cannot build report from stream");
        }

        jasperReport.setProperty(JRTextField.PROPERTY_PRINT_KEEP_FULL_TEXT, "true");
        Map<String, Object> params = new HashMap<>();
        params.put(JRXPathQueryExecuterFactory.XML_INPUT_STREAM, new ByteArrayInputStream(sourceData));
        JRXmlDataSource source = new JRXmlDataSource(new ByteArrayInputStream(sourceData));
        source.setDatePattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, source);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRAbstractExporter exporter = getExporter(reportFormat);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        //noinspection unchecked
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    private static JRAbstractExporter getExporter(ReportFormat reportFormat) {
        switch (reportFormat) {
            case DOCX: {
                return new JRDocxExporter();
            }
            case XLSX: {
                return new JRXlsxExporter();
            }
            case PDF: {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                configuration.setCompressed(true);
                exporter.setConfiguration(configuration);
                return exporter;
            }
            default:
                throw new IllegalArgumentException("Unknown export type " + reportFormat);

        }
    }

    private ReportBuilder() {
    }
}