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
@lombok.RequiredArgsConstructor
public class ExportService {

    private final TradeService tradeService;

    private java.time.LocalDate parseDateSafely(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || "undefined".equalsIgnoreCase(dateStr) || "null".equalsIgnoreCase(dateStr)) {
            return null;
        }
        try {
            return java.time.LocalDate.parse(dateStr);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + dateStr + " - " + e.getMessage());
            return null;
        }
    }

    public byte[] exportData(Long userId, String startDate, String endDate, boolean includeTrades, boolean includeAnalytics, String format) {
        java.time.LocalDate sDate = parseDateSafely(startDate);
        java.time.LocalDate eDate = parseDateSafely(endDate);
        
        List<Trade> trades = tradeService.getFilteredTradesForExport(userId, sDate, eDate);

        ByteArrayInputStream in;
        if ("excel".equalsIgnoreCase(format)) {
            in = exportToExcel(trades, includeTrades, includeAnalytics);
        } else {
            in = exportToPdf(trades, includeTrades, includeAnalytics, startDate, endDate);
        }
        
        return in.readAllBytes();
    }

    private ByteArrayInputStream exportToExcel(List<Trade> trades, boolean includeTrades, boolean includeAnalytics) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            if (includeTrades) {
                Sheet sheet = workbook.createSheet("Trades");
                createTradesSheet(sheet, trades, workbook);
            }

            if (includeAnalytics) {
                Sheet sheet = workbook.createSheet("Analytics");
                createAnalyticsSheet(sheet, trades, workbook);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel data: " + e.getMessage());
        }
    }

    private void createTradesSheet(Sheet sheet, List<Trade> trades, Workbook workbook) {
        // ... Excel logic remains same ...
        // Header Row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Date", "Symbol", "Entry", "Exit", "Qty", "Type", "Outcome", "TP", "SL", "Result", "P/L", "Notes"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
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
            row.createCell(7).setCellValue(trade.getTp() != null ? trade.getTp() : 0);
            row.createCell(8).setCellValue(trade.getSl() != null ? trade.getSl() : 0);
            row.createCell(9).setCellValue(trade.getResult());
            row.createCell(10).setCellValue(trade.getProfitLoss() != null ? trade.getProfitLoss().doubleValue() : 0);
            row.createCell(11).setCellValue(trade.getNotes());
        }
    }

    private void createAnalyticsSheet(Sheet sheet, List<Trade> trades, Workbook workbook) {
        Analytics analytics = calculateAnalytics(trades);

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Analytics Summary");
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        headerCell.setCellStyle(style);

        String[][] data = {
            {"Total Trades", String.valueOf(analytics.totalTrades)},
            {"Wins", String.valueOf(analytics.wins)},
            {"Losses", String.valueOf(analytics.losses)},
            {"Win Rate", String.format("%.2f%%", analytics.winRate)},
            {"Total Profit", String.format("%.2f", analytics.totalProfit)},
            {"Total Loss", String.format("%.2f", analytics.totalLoss)},
            {"Net P/L", String.format("%.2f", analytics.netPL)},
            {"Best Trade", String.format("%.2f (%s)", analytics.bestTradePL, analytics.bestTradeSymbol)},
            {"Worst Trade", String.format("%.2f (%s)", analytics.worstTradePL, analytics.worstTradeSymbol)}
        };

        int rowIdx = 2;
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rowData[0]);
            row.createCell(1).setCellValue(rowData[1]);
        }
    }

    private ByteArrayInputStream exportToPdf(List<Trade> trades, boolean includeTrades, boolean includeAnalytics, String startDate, String endDate) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new FooterEvent()); // Add Footer
            document.open();

            // 1. Title Section
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("TradeTrackPro — Trade Journal Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Divider Line
            com.lowagie.text.pdf.draw.LineSeparator line = new com.lowagie.text.pdf.draw.LineSeparator();
            line.setOffset(-2);
            document.add(new Paragraph(" "));
            document.add(line);
            document.add(new Paragraph(" "));

            // Create mutable copy and sort trades by date descending for report
            List<Trade> sortedTrades = new java.util.ArrayList<>(trades);
            sortedTrades.sort((t1, t2) -> {
                if (t1.getTradeDate() == null && t2.getTradeDate() == null) return 0;
                if (t1.getTradeDate() == null) return 1;
                if (t2.getTradeDate() == null) return -1;
                return t2.getTradeDate().compareTo(t1.getTradeDate());
            });

            // Report Range
            String rangeText = (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty())
                    ? "Report Range: " + formatDate(startDate) + " → " + formatDate(endDate)
                    : "Report Range: All Dates";
            
            Paragraph range = new Paragraph(rangeText, FontFactory.getFont(FontFactory.HELVETICA, 12));
            range.setAlignment(Element.ALIGN_CENTER);
            range.setSpacingAfter(20);
            document.add(range);

            if (includeAnalytics) {
                addAnalyticsSection(document, sortedTrades);
            }

            if (includeTrades) {
                if (includeAnalytics) document.add(new Paragraph(" "));
                addTradesTable(document, sortedTrades);
            }

            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export PDF data: " + e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String formatDate(String dateStr) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d, yyyy"));
        } catch (Exception e) {
            return dateStr;
        }
    }

    private void addAnalyticsSection(Document document, List<Trade> trades) throws DocumentException {
        Analytics analytics = calculateAnalytics(trades);
        
        PdfPTable container = new PdfPTable(1);
        container.setWidthPercentage(100);
        
        // Section Title
        PdfPCell titleCell = new PdfPCell(new Phrase("Analytics Overview", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
        titleCell.setBorder(PdfPCell.NO_BORDER);
        titleCell.setPaddingBottom(10);
        container.addCell(titleCell);

        // Box
        PdfPTable box = new PdfPTable(2);
        box.setWidthPercentage(100);
        box.setSpacingBefore(10);
        
        // Left Column (Stats)
        PdfPTable leftCol = new PdfPTable(2);
        leftCol.setWidths(new float[]{1, 1});
        addStatRow(leftCol, "Total Trades", String.valueOf(analytics.totalTrades));
        addStatRow(leftCol, "Wins", String.valueOf(analytics.wins));
        addStatRow(leftCol, "Losses", String.valueOf(analytics.losses));
        addStatRow(leftCol, "Win Rate", String.format("%.2f%%", analytics.winRate));
        
        PdfPCell leftCell = new PdfPCell(leftCol);
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setPadding(10);
        box.addCell(leftCell);

        // Right Column (Financials) and Best/Worst
        PdfPTable rightCol = new PdfPTable(2);
        rightCol.setWidths(new float[]{1, 1});
        
        addStatRow(rightCol, "Net P/L", String.format("%.2f", analytics.netPL), analytics.netPL);
        addStatRow(rightCol, "Total Profit", String.format("%.2f", analytics.totalProfit), analytics.totalProfit);
        addStatRow(rightCol, "Total Loss", String.format("%.2f", analytics.totalLoss), analytics.totalLoss);
        addStatRow(rightCol, "Best Trade", String.format("%.2f", analytics.bestTradePL)); // Simplified for layout
        addStatRow(rightCol, "Worst Trade", String.format("%.2f", analytics.worstTradePL));

        PdfPCell rightCell = new PdfPCell(rightCol);
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setPadding(10);
        box.addCell(rightCell);

        // Wrap Box in a cell with border/bg
        PdfPCell boxCell = new PdfPCell(box);
        boxCell.setBackgroundColor(new java.awt.Color(249, 250, 251)); // Gray-50
        boxCell.setBorderColor(java.awt.Color.LIGHT_GRAY);
        boxCell.setBorderWidth(1f);
        boxCell.setPadding(5);
        
        container.addCell(boxCell);
        document.add(container);
        document.add(new Paragraph(" ")); // Spacer
    }

    private void addStatRow(PdfPTable table, String label, String value) {
        addStatRow(table, label, value, null);
    }

    private void addStatRow(PdfPTable table, String label, String value, Double amount) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, java.awt.Color.GRAY)));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPaddingBottom(5);
        table.addCell(labelCell);

        com.lowagie.text.Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        if (amount != null) {
            if (amount > 0) valueFont.setColor(new java.awt.Color(22, 163, 74)); // Green
            else if (amount < 0) valueFont.setColor(new java.awt.Color(220, 38, 38)); // Red
        }

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPaddingBottom(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addTradesTable(Document document, List<Trade> trades) throws DocumentException {
        // Section Title
        Paragraph tableTitle = new Paragraph("Trades List (" + trades.size() + ")", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);

        PdfPTable table = new PdfPTable(11);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2.5f, 1.5f, 2f, 2f, 1.5f, 2f, 2f, 1.5f, 1.5f, 2f, 4f});
        table.setHeaderRows(1);

        // Styling
        java.awt.Color headerBg = new java.awt.Color(31, 41, 55); // Dark Gray
        java.awt.Color rowStriping = new java.awt.Color(243, 244, 246); // Light Gray

        // Header
        String[] headers = {"Date", "Symbol", "Entry", "Exit", "Qty", "Type", "Outcome", "TP", "SL", "P/L", "Notes"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, java.awt.Color.WHITE)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(headerBg);
            cell.setPadding(6);
            cell.setBorderColor(java.awt.Color.GRAY);
            table.addCell(cell);
        }

        // Data
        boolean isGray = false;
        com.lowagie.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
        
        for (Trade trade : trades) {
            isGray = !isGray;
            java.awt.Color bg = isGray ? rowStriping : java.awt.Color.WHITE;

            addCell(table, trade.getTradeDate() != null ? trade.getTradeDate().toString() : "", cellFont, bg, Element.ALIGN_CENTER);
            addCell(table, trade.getSymbol(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8), bg, Element.ALIGN_CENTER);
            addCell(table, String.valueOf(trade.getEntryPrice()), cellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, String.valueOf(trade.getExitPrice()), cellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, String.valueOf(trade.getQuantity()), cellFont, bg, Element.ALIGN_CENTER);
            addCell(table, trade.getPositionType(), cellFont, bg, Element.ALIGN_CENTER);
            addCell(table, trade.getOutcome(), cellFont, bg, Element.ALIGN_CENTER);
            addCell(table, trade.getTp() != null ? String.valueOf(trade.getTp()) : "-", cellFont, bg, Element.ALIGN_RIGHT);
            addCell(table, trade.getSl() != null ? String.valueOf(trade.getSl()) : "-", cellFont, bg, Element.ALIGN_RIGHT);
            
            // P/L Logic
            double pl = trade.getProfitLoss() != null ? trade.getProfitLoss().doubleValue() : 0;
            com.lowagie.text.Font plFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            if (pl > 0) plFont.setColor(new java.awt.Color(22, 163, 74));
            else if (pl < 0) plFont.setColor(new java.awt.Color(220, 38, 38));
            
            addCell(table, String.format("%.2f", pl), plFont, bg, Element.ALIGN_RIGHT);
            addCell(table, trade.getNotes() != null ? trade.getNotes() : "", cellFont, bg, Element.ALIGN_LEFT);
        }

        document.add(table);
    }
    
    private void addCell(PdfPTable table, String text, com.lowagie.text.Font font, java.awt.Color bg, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(new java.awt.Color(229, 231, 235)); // Gray-200
        table.addCell(cell);
    }

    private Analytics calculateAnalytics(List<Trade> trades) {
        Analytics a = new Analytics();
        a.totalTrades = trades.size();
        
        for (Trade t : trades) {
            double pl = t.getProfitLoss() != null ? t.getProfitLoss().doubleValue() : 0;
            if (pl > 0) {
                a.wins++;
                a.totalProfit += pl;
            } else if (pl < 0) {
                a.losses++;
                a.totalLoss += pl;
            }
            
            a.netPL += pl;

            if (pl > a.bestTradePL) {
                a.bestTradePL = pl;
                a.bestTradeSymbol = t.getSymbol();
            }
            
            if (pl < a.worstTradePL) {
                a.worstTradePL = pl;
                a.worstTradeSymbol = t.getSymbol();
            }
        }
         // Initial values for best/worst if no trades
        if (a.bestTradePL == -Double.MAX_VALUE) a.bestTradePL = 0;
        if (a.worstTradePL == Double.MAX_VALUE) a.worstTradePL = 0;

        if (a.totalTrades > 0) {
            a.winRate = (double) a.wins / a.totalTrades * 100;
        }
        
        return a;
    }

    private static class Analytics {
        int totalTrades = 0;
        int wins = 0;
        int losses = 0;
        double winRate = 0;
        double totalProfit = 0;
        double totalLoss = 0;
        double netPL = 0;
        double bestTradePL = -Double.MAX_VALUE;
        String bestTradeSymbol = "-";
        double worstTradePL = Double.MAX_VALUE;
        String worstTradeSymbol = "-";
    }

    // PDF Footer Event Helper
    class FooterEvent extends com.lowagie.text.pdf.PdfPageEventHelper {
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPCell cell = new PdfPCell(new Phrase("Generated on " + java.time.LocalDate.now().toString() + " | Page " + writer.getPageNumber(), 
                FontFactory.getFont(FontFactory.HELVETICA, 8, java.awt.Color.GRAY)));
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            
            PdfPTable table = new PdfPTable(1);
            table.setTotalWidth(523);
            table.addCell(cell);
            table.writeSelectedRows(0, -1, 36, 30, writer.getDirectContent());
        }
    }
}
