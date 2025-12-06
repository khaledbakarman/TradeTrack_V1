package com.tradetrackpro.controller;

import com.tradetrackpro.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportTrades(
            HttpServletRequest request,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "true") boolean includeTrades,
            @RequestParam(defaultValue = "true") boolean includeAnalytics,
            @RequestParam(defaultValue = "excel") String format) {

        try {
            Long userId = (Long) request.getAttribute("userId");
            
            // Check if userId is present (from JWT)
            if (userId == null) {
                System.err.println("Export failed: userId is null - JWT token may be missing or invalid");
                return ResponseEntity.status(401).build();
            }
            
            System.out.println("Export request - userId: " + userId + ", startDate: " + startDate + ", endDate: " + endDate + ", format: " + format);
            
            byte[] fileBytes = exportService.exportData(userId, startDate, endDate, includeTrades, includeAnalytics, format);

            String filename = "trade_journal_export." + ("pdf".equalsIgnoreCase(format) ? "pdf" : "xlsx");
            MediaType mediaType = "pdf".equalsIgnoreCase(format) ? MediaType.APPLICATION_PDF : MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
        } catch (Exception e) {
            System.err.println("Export failed with exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
