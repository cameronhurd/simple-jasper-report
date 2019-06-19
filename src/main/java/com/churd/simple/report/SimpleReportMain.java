package com.churd.simple.report;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleReportMain {

    private static final String TEMPLATE = "/templates/simple_report_template.jrxml";
    private static final String REPORT_OUTPUT_FILE = "Report.pdf";

    public static void main(String[] args) {
        try {
            System.out.println("Starting report generation");
            SimpleReportMain main = new SimpleReportMain();
            main.generateReport();
        }
        catch (Exception e) {
            System.out.println("Exception generating report!");
            e.printStackTrace();
        }
    }

    public void generateReport() throws JRException, IOException {
        List<JasperPrint> populatedTemplates = new ArrayList<>();
        JasperReport report = JasperCompileManager.compileReport(
                SimpleReportMain.class.getResourceAsStream(TEMPLATE));
        populatedTemplates.add(JasperFillManager.fillReport(
                report, _getParameters(), new JREmptyDataSource()));

        // PDF export configuration
        SimplePdfExporterConfiguration exportConfiguration = new SimplePdfExporterConfiguration();
        exportConfiguration.setCreatingBatchModeBookmarks(false);
        exportConfiguration.setCompressed(true);

        // PDF report configuration
        SimplePdfReportConfiguration reportConfiguration = new SimplePdfReportConfiguration();
        reportConfiguration.setForceSvgShapes(true);

        ByteArrayOutputStream reportOutputStream = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        try {
            // export the completed report
            JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(SimpleExporterInput.getInstance(populatedTemplates));
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(reportOutputStream));
            pdfExporter.setConfiguration(exportConfiguration);
            pdfExporter.setConfiguration(reportConfiguration);
            pdfExporter.exportReport();

            byte[] aocReportByteArray = reportOutputStream.toByteArray();
            File reportFile = new File( REPORT_OUTPUT_FILE);
            fileOutputStream = new FileOutputStream(reportFile);
            fileOutputStream.write(aocReportByteArray);
            System.out.println("Report complete! File: " + reportFile.getAbsolutePath());
        }
        finally {
            if (null != reportOutputStream) {
                reportOutputStream.close();
            }
            if (null != fileOutputStream) {
                fileOutputStream.close();
            }
        }
    }
    
    private Map<String, Object> _getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("param1", "test value");
        return params;
    }
}
