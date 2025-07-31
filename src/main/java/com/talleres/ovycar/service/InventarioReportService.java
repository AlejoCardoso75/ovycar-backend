package com.talleres.ovycar.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.talleres.ovycar.dto.ProductoDTO;
import com.talleres.ovycar.repository.ProductoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class InventarioReportService {

    @Autowired
    private ProductoRepository productoRepository;

    public byte[] generarInventarioPdf() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // Configurar fuentes
        PdfFont fontBold = PdfFontFactory.createFont();
        PdfFont fontNormal = PdfFontFactory.createFont();
        PdfFont fontItalic = PdfFontFactory.createFont();

        // Crear logo usando elementos de iText
        com.itextpdf.layout.element.Table logoTable = new com.itextpdf.layout.element.Table(1).useAllAvailableWidth();
        logoTable.setMarginBottom(20);

        // Fondo del logo
        com.itextpdf.layout.element.Cell logoCell = new com.itextpdf.layout.element.Cell();
        logoCell.setBackgroundColor(ColorConstants.DARK_GRAY);
        logoCell.setHeight(80);
        logoCell.setBorder(null);
        logoCell.setPadding(10);

        // Contenido del logo
        Paragraph logoTitle = new Paragraph("OVY CAR")
            .setFont(fontBold)
            .setFontSize(24)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);

        Paragraph logoSubtitle = new Paragraph("TALLER AUTOMOTRIZ")
            .setFont(fontNormal)
            .setFontSize(12)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);

        // Línea roja decorativa
        LineSeparator redLine = new LineSeparator(new SolidLine(2));
        redLine.setStrokeColor(ColorConstants.RED);
        redLine.setMarginTop(5);
        redLine.setMarginBottom(5);

        logoCell.add(logoTitle);
        logoCell.add(logoSubtitle);
        logoCell.add(redLine);

        logoTable.addCell(logoCell);
        document.add(logoTable);

        // Información de contacto
        Paragraph address = new Paragraph("Dirección: Calle Principal #123, Ciudad")
            .setFont(fontNormal)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);
        document.add(address);

        Paragraph phone = new Paragraph("Tel: (123) 456-7890 | Email: info@ovycar.com")
            .setFont(fontNormal)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(phone);

        // Título del reporte
        Paragraph titulo = new Paragraph("INFORME DE INVENTARIO")
            .setFont(fontBold)
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(titulo);

        // Fecha del reporte
        Paragraph fecha = new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .setFont(fontNormal)
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(fecha);

        // Obtener todos los productos
        List<ProductoDTO> productos = productoRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(java.util.stream.Collectors.toList());

        if (!productos.isEmpty()) {
            // Tabla de productos
            com.itextpdf.layout.element.Table productosTable = new com.itextpdf.layout.element.Table(6).useAllAvailableWidth();
            productosTable.setMarginBottom(20);
            productosTable.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            com.itextpdf.layout.element.Cell headerCodigo = new com.itextpdf.layout.element.Cell().add(new Paragraph("Código").setFont(fontBold).setFontSize(12));
            headerCodigo.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerCodigo.setTextAlignment(TextAlignment.CENTER);
            headerCodigo.setWidth(UnitValue.createPercentValue(15));
            productosTable.addHeaderCell(headerCodigo);

            com.itextpdf.layout.element.Cell headerNombre = new com.itextpdf.layout.element.Cell().add(new Paragraph("Nombre").setFont(fontBold).setFontSize(12));
            headerNombre.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerNombre.setTextAlignment(TextAlignment.CENTER);
            headerNombre.setWidth(UnitValue.createPercentValue(25));
            productosTable.addHeaderCell(headerNombre);

            com.itextpdf.layout.element.Cell headerMarca = new com.itextpdf.layout.element.Cell().add(new Paragraph("Marca").setFont(fontBold).setFontSize(12));
            headerMarca.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerMarca.setTextAlignment(TextAlignment.CENTER);
            headerMarca.setWidth(UnitValue.createPercentValue(20));
            productosTable.addHeaderCell(headerMarca);

            com.itextpdf.layout.element.Cell headerStock = new com.itextpdf.layout.element.Cell().add(new Paragraph("Stock").setFont(fontBold).setFontSize(12));
            headerStock.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerStock.setTextAlignment(TextAlignment.CENTER);
            headerStock.setWidth(UnitValue.createPercentValue(15));
            productosTable.addHeaderCell(headerStock);

            com.itextpdf.layout.element.Cell headerPrecio = new com.itextpdf.layout.element.Cell().add(new Paragraph("Precio").setFont(fontBold).setFontSize(12));
            headerPrecio.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerPrecio.setTextAlignment(TextAlignment.CENTER);
            headerPrecio.setWidth(UnitValue.createPercentValue(15));
            productosTable.addHeaderCell(headerPrecio);

            com.itextpdf.layout.element.Cell headerValor = new com.itextpdf.layout.element.Cell().add(new Paragraph("Valor Total").setFont(fontBold).setFontSize(12));
            headerValor.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerValor.setTextAlignment(TextAlignment.CENTER);
            headerValor.setWidth(UnitValue.createPercentValue(10));
            productosTable.addHeaderCell(headerValor);

            // Filas de productos
            BigDecimal valorTotalInventario = BigDecimal.ZERO;
            for (ProductoDTO producto : productos) {
                // Código
                com.itextpdf.layout.element.Cell cellCodigo = new com.itextpdf.layout.element.Cell().add(new Paragraph(producto.getCodigo()).setFont(fontNormal).setFontSize(11));
                cellCodigo.setPadding(8);
                productosTable.addCell(cellCodigo);

                // Nombre
                com.itextpdf.layout.element.Cell cellNombre = new com.itextpdf.layout.element.Cell().add(new Paragraph(producto.getNombre()).setFont(fontNormal).setFontSize(11));
                cellNombre.setPadding(8);
                productosTable.addCell(cellNombre);

                // Marca
                com.itextpdf.layout.element.Cell cellMarca = new com.itextpdf.layout.element.Cell().add(new Paragraph(producto.getMarca()).setFont(fontNormal).setFontSize(11));
                cellMarca.setTextAlignment(TextAlignment.CENTER);
                cellMarca.setPadding(8);
                productosTable.addCell(cellMarca);

                // Stock
                com.itextpdf.layout.element.Cell cellStock = new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(producto.getStock())).setFont(fontNormal).setFontSize(11));
                cellStock.setTextAlignment(TextAlignment.CENTER);
                cellStock.setPadding(8);
                productosTable.addCell(cellStock);

                // Precio
                com.itextpdf.layout.element.Cell cellPrecio = new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + String.format("%,.0f", producto.getPrecioVenta().doubleValue())).setFont(fontNormal).setFontSize(11));
                cellPrecio.setTextAlignment(TextAlignment.RIGHT);
                cellPrecio.setPadding(8);
                productosTable.addCell(cellPrecio);

                // Valor Total
                BigDecimal valorProducto = producto.getPrecioVenta().multiply(BigDecimal.valueOf(producto.getStock()));
                valorTotalInventario = valorTotalInventario.add(valorProducto);
                com.itextpdf.layout.element.Cell cellValor = new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + String.format("%,.0f", valorProducto.doubleValue())).setFont(fontNormal).setFontSize(11));
                cellValor.setTextAlignment(TextAlignment.RIGHT);
                cellValor.setPadding(8);
                productosTable.addCell(cellValor);
            }

            document.add(productosTable);

            // Resumen
            com.itextpdf.layout.element.Table resumenTable = new com.itextpdf.layout.element.Table(2).useAllAvailableWidth();
            resumenTable.setMarginTop(20);
            resumenTable.setWidth(UnitValue.createPercentValue(100));

            com.itextpdf.layout.element.Cell cellTotalProductosLabel = new com.itextpdf.layout.element.Cell().add(new Paragraph("Total de Productos:").setFont(fontBold).setFontSize(12));
            cellTotalProductosLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cellTotalProductosLabel.setWidth(UnitValue.createPercentValue(70));
            cellTotalProductosLabel.setPadding(10);
            resumenTable.addCell(cellTotalProductosLabel);

            com.itextpdf.layout.element.Cell cellTotalProductosValue = new com.itextpdf.layout.element.Cell().add(new Paragraph(String.valueOf(productos.size())).setFont(fontNormal).setFontSize(12));
            cellTotalProductosValue.setTextAlignment(TextAlignment.RIGHT);
            cellTotalProductosValue.setWidth(UnitValue.createPercentValue(30));
            cellTotalProductosValue.setPadding(10);
            resumenTable.addCell(cellTotalProductosValue);

            com.itextpdf.layout.element.Cell cellValorTotalLabel = new com.itextpdf.layout.element.Cell().add(new Paragraph("Valor Total del Inventario:").setFont(fontBold).setFontSize(14));
            cellValorTotalLabel.setBackgroundColor(ColorConstants.DARK_GRAY);
            cellValorTotalLabel.setFontColor(ColorConstants.WHITE);
            cellValorTotalLabel.setPadding(12);
            resumenTable.addCell(cellValorTotalLabel);

            com.itextpdf.layout.element.Cell cellValorTotalValue = new com.itextpdf.layout.element.Cell().add(new Paragraph("$" + String.format("%,.0f", valorTotalInventario.doubleValue())).setFont(fontBold).setFontSize(14));
            cellValorTotalValue.setTextAlignment(TextAlignment.RIGHT);
            cellValorTotalValue.setBackgroundColor(ColorConstants.DARK_GRAY);
            cellValorTotalValue.setFontColor(ColorConstants.WHITE);
            cellValorTotalValue.setPadding(12);
            resumenTable.addCell(cellValorTotalValue);

            document.add(resumenTable);

        } else {
            Paragraph noProductos = new Paragraph("No hay productos en el inventario.")
                .setFont(fontItalic)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(noProductos);
        }

        // Pie de página
        LineSeparator footerSeparator = new LineSeparator(new SolidLine());
        footerSeparator.setMarginTop(30);
        footerSeparator.setMarginBottom(10);
        document.add(footerSeparator);

        Paragraph footer = new Paragraph("Reporte generado automáticamente por Ovy Car")
            .setFont(fontItalic)
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5);
        document.add(footer);

        Paragraph footerContact = new Paragraph("www.ovycar.com | (123) 456-7890 | info@ovycar.com")
            .setFont(fontNormal)
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(footerContact);

        document.close();
        return baos.toByteArray();
    }

    public byte[] generarInventarioExcel() throws IOException {
        List<ProductoDTO> productos = productoRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(java.util.stream.Collectors.toList());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");

            // Crear estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.cloneStyleFrom(numberStyle);
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Código", "Nombre", "Marca", "Stock", "Precio", "Valor Total"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            BigDecimal valorTotalInventario = BigDecimal.ZERO;
            for (int i = 0; i < productos.size(); i++) {
                ProductoDTO producto = productos.get(i);
                Row row = sheet.createRow(i + 1);

                // Código
                org.apache.poi.ss.usermodel.Cell cellCodigo = row.createCell(0);
                cellCodigo.setCellValue(producto.getCodigo());
                cellCodigo.setCellStyle(dataStyle);

                // Nombre
                org.apache.poi.ss.usermodel.Cell cellNombre = row.createCell(1);
                cellNombre.setCellValue(producto.getNombre());
                cellNombre.setCellStyle(dataStyle);

                // Marca
                org.apache.poi.ss.usermodel.Cell cellMarca = row.createCell(2);
                cellMarca.setCellValue(producto.getMarca());
                cellMarca.setCellStyle(dataStyle);

                // Stock
                org.apache.poi.ss.usermodel.Cell cellStock = row.createCell(3);
                cellStock.setCellValue(producto.getStock());
                cellStock.setCellStyle(numberStyle);

                // Precio
                org.apache.poi.ss.usermodel.Cell cellPrecio = row.createCell(4);
                cellPrecio.setCellValue(producto.getPrecioVenta().doubleValue());
                cellPrecio.setCellStyle(currencyStyle);

                // Valor Total
                BigDecimal valorProducto = producto.getPrecioVenta().multiply(BigDecimal.valueOf(producto.getStock()));
                valorTotalInventario = valorTotalInventario.add(valorProducto);
                org.apache.poi.ss.usermodel.Cell cellValor = row.createCell(5);
                cellValor.setCellValue(valorProducto.doubleValue());
                cellValor.setCellStyle(currencyStyle);
            }

            // Agregar fila de totales
            Row totalRow = sheet.createRow(productos.size() + 2);
            
            org.apache.poi.ss.usermodel.Cell cellTotalLabel = totalRow.createCell(0);
            cellTotalLabel.setCellValue("TOTAL INVENTARIO:");
            cellTotalLabel.setCellStyle(headerStyle);

            org.apache.poi.ss.usermodel.Cell cellTotalValue = totalRow.createCell(5);
            cellTotalValue.setCellValue(valorTotalInventario.doubleValue());
            cellTotalValue.setCellStyle(currencyStyle);

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Agregar información del reporte
            Row infoRow1 = sheet.createRow(productos.size() + 4);
            org.apache.poi.ss.usermodel.Cell cellInfo1 = infoRow1.createCell(0);
            cellInfo1.setCellValue("Reporte generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            Row infoRow2 = sheet.createRow(productos.size() + 5);
            org.apache.poi.ss.usermodel.Cell cellInfo2 = infoRow2.createCell(0);
            cellInfo2.setCellValue("Total de productos: " + productos.size());

            Row infoRow3 = sheet.createRow(productos.size() + 6);
            org.apache.poi.ss.usermodel.Cell cellInfo3 = infoRow3.createCell(0);
            cellInfo3.setCellValue("Valor total del inventario: $" + String.format("%,.0f", valorTotalInventario.doubleValue()));

            // Guardar en ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private ProductoDTO convertToDTO(com.talleres.ovycar.entity.Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setMarca(producto.getMarca());
        dto.setStock(producto.getStock());
        dto.setPrecioVenta(producto.getPrecioVenta());
        return dto;
    }
} 