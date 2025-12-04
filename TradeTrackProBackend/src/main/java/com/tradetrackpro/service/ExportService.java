package com.tradetrackpro.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tradetrackpro.model.Trade;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    public ByteArrayInputStream exportTradesToExcel(List<Trade> trades) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Trades");

            // Header Row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Date", "Symbol", "Entry", "Exit", "Qty", "Type", "Outcome", "P/L", "Notes"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Data Rows
            int rowIdx = 1;
            for (Trade trade : trades) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(trade.getTradeDate() != null ? trade.getTradeDate().toString() : "");
                row.createCell(1).setCellValue(trade.getSymbol());
                row.createCell(2).setCellValue(trade.getEntryPrice() != null ? trade.getEntryPrice().doubleValue() : 0);
                row.createCell(3).setCellValue(trade.getExitPrice() != null ? trade.getExitPrice().doubleValue() : 0);
                row.createCell(4).setCellValue(trade.getQuantity() != null ? trade.getQuantity().doubleValue() : 0);
                row.createCell(5).setCellValue(trade.getPositionType());
                row.createCell(6).setCellValue(trade.getOutcome());
                row.createCell(7).setCellValue(trade.getProfitLoss() != null ? trade.getProfitLoss().doubleValue() : 0);
                row.createCell(8).setCellValue(trade.getNotes());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream exportTradesToPdf(List<Trade> trades) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            Paragraph title = new Paragraph("Trade Journal Export", font);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Spacer

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 2, 2, 2, 2, 2, 2, 2, 4});

            // Header
            String[] headers = {"Date", "Symbol", "Entry", "Exit", "Qty", "Type", "Outcome", "P/L", "Notes"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell();
                cell.setPhrase(new Phrase(header));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Data
            for (Trade trade : trades) {
                table.addCell(trade.getTradeDate() != null ? trade.getTradeDate().toString() : "");
                table.addCell(trade.getSymbol());
                table.addCell(String.valueOf(trade.getEntryPrice()));
                table.addCell(String.valueOf(trade.getExitPrice()));
                table.addCell(String.valueOf(trade.getQuantity()));
                table.addCell(trade.getPositionType());
                table.addCell(trade.getOutcome());
                table.addCell(String.valueOf(trade.getProfitLoss()));
                table.addCell(trade.getNotes() != null ? trade.getNotes() : "");
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export PDF data: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
