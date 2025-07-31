package com.talleres.ovycar.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.Image;
import com.itextpdf.io.image.ImageDataFactory;
import com.talleres.ovycar.dto.FacturaDTO;
import com.talleres.ovycar.dto.DetalleFacturaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PdfService {
    
    public byte[] generarFacturaPdf(FacturaDTO factura) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Configurar fuentes
        PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont fontItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
        
        // Crear logo usando elementos de iText
        Table logoTable = new Table(1).useAllAvailableWidth();
        logoTable.setMarginBottom(20);
        
        // Fondo del logo
        Cell logoCell = new Cell();
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
        
        // Línea separadora
        LineSeparator separator = new LineSeparator(new SolidLine());
        separator.setMarginBottom(20);
        document.add(separator);
        
        Paragraph facturaTitle = new Paragraph("FACTURA")
            .setFont(fontBold)
            .setFontSize(18)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(facturaTitle);
        
        // Información de la factura
        Table infoTable = new Table(2).useAllAvailableWidth();
        infoTable.setMarginBottom(20);
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        // Número de factura y fecha
        Cell cellNumeroLabel = new Cell().add(new Paragraph("Número de Factura:").setFont(fontBold).setFontSize(12));
        cellNumeroLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cellNumeroLabel.setWidth(UnitValue.createPercentValue(40));
        cellNumeroLabel.setPadding(8);
        infoTable.addCell(cellNumeroLabel);
        
        Cell cellNumeroValue = new Cell().add(new Paragraph(factura.getNumeroFactura()).setFont(fontNormal).setFontSize(12));
        cellNumeroValue.setWidth(UnitValue.createPercentValue(60));
        cellNumeroValue.setPadding(8);
        infoTable.addCell(cellNumeroValue);
        
        Cell cellFechaLabel = new Cell().add(new Paragraph("Fecha de Emisión:").setFont(fontBold).setFontSize(12));
        cellFechaLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cellFechaLabel.setPadding(8);
        infoTable.addCell(cellFechaLabel);
        
        Cell cellFechaValue = new Cell().add(new Paragraph(factura.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFont(fontNormal).setFontSize(12));
        cellFechaValue.setPadding(8);
        infoTable.addCell(cellFechaValue);
        
        if (factura.getFechaVencimiento() != null) {
            Cell cellVencimientoLabel = new Cell().add(new Paragraph("Fecha de Vencimiento:").setFont(fontBold).setFontSize(12));
            cellVencimientoLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cellVencimientoLabel.setPadding(8);
            infoTable.addCell(cellVencimientoLabel);
            
            Cell cellVencimientoValue = new Cell().add(new Paragraph(factura.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setFont(fontNormal).setFontSize(12));
            cellVencimientoValue.setPadding(8);
            infoTable.addCell(cellVencimientoValue);
        }
        
        Cell cellEstadoLabel = new Cell().add(new Paragraph("Estado:").setFont(fontBold).setFontSize(12));
        cellEstadoLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cellEstadoLabel.setPadding(8);
        infoTable.addCell(cellEstadoLabel);
        
        Cell cellEstadoValue = new Cell().add(new Paragraph(factura.getEstado()).setFont(fontNormal).setFontSize(12));
        cellEstadoValue.setPadding(8);
        infoTable.addCell(cellEstadoValue);
        
        document.add(infoTable);
        
        // Información del cliente
        Paragraph clienteHeader = new Paragraph("INFORMACIÓN DEL CLIENTE")
            .setFont(fontBold)
            .setFontSize(14)
            .setMarginBottom(10);
        document.add(clienteHeader);
        
        Table clienteTable = new Table(2).useAllAvailableWidth();
        clienteTable.setMarginBottom(20);
        clienteTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell cellClienteLabel = new Cell().add(new Paragraph("Cliente:").setFont(fontBold).setFontSize(12));
        cellClienteLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cellClienteLabel.setWidth(UnitValue.createPercentValue(30));
        cellClienteLabel.setPadding(8);
        clienteTable.addCell(cellClienteLabel);
        
        Cell cellClienteValue = new Cell().add(new Paragraph(factura.getClienteNombre()).setFont(fontNormal).setFontSize(12));
        cellClienteValue.setWidth(UnitValue.createPercentValue(70));
        cellClienteValue.setPadding(8);
        clienteTable.addCell(cellClienteValue);
        
        // Agregar información del vehículo si hay mantenimiento asociado
        if (factura.getMantenimientoId() != null) {
            Cell cellMantenimientoLabel = new Cell().add(new Paragraph("Mantenimiento Asociado:").setFont(fontBold).setFontSize(12));
            cellMantenimientoLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cellMantenimientoLabel.setPadding(8);
            clienteTable.addCell(cellMantenimientoLabel);
            
            Cell cellMantenimientoValue = new Cell().add(new Paragraph("ID: " + factura.getMantenimientoId()).setFont(fontNormal).setFontSize(12));
            cellMantenimientoValue.setPadding(8);
            clienteTable.addCell(cellMantenimientoValue);
        }
        
        document.add(clienteTable);
        
        // Detalles de la factura
        Paragraph detallesHeader = new Paragraph("DETALLES DE LA FACTURA")
            .setFont(fontBold)
            .setFontSize(14)
            .setMarginBottom(10);
        document.add(detallesHeader);
        
        if (factura.getDetalles() != null && !factura.getDetalles().isEmpty()) {
            Table detallesTable = new Table(5).useAllAvailableWidth();
            detallesTable.setMarginBottom(20);
            
            // Configurar anchos de columnas
            detallesTable.setWidth(UnitValue.createPercentValue(100));
            
            // Encabezados de la tabla
            Cell headerDescripcion = new Cell().add(new Paragraph("Descripción").setFont(fontBold).setFontSize(12));
            headerDescripcion.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerDescripcion.setTextAlignment(TextAlignment.CENTER);
            headerDescripcion.setWidth(UnitValue.createPercentValue(40));
            detallesTable.addHeaderCell(headerDescripcion);
            
            Cell headerTipo = new Cell().add(new Paragraph("Tipo").setFont(fontBold).setFontSize(12));
            headerTipo.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerTipo.setTextAlignment(TextAlignment.CENTER);
            headerTipo.setWidth(UnitValue.createPercentValue(15));
            detallesTable.addHeaderCell(headerTipo);
            
            Cell headerCantidad = new Cell().add(new Paragraph("Cantidad").setFont(fontBold).setFontSize(12));
            headerCantidad.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerCantidad.setTextAlignment(TextAlignment.CENTER);
            headerCantidad.setWidth(UnitValue.createPercentValue(15));
            detallesTable.addHeaderCell(headerCantidad);
            
            Cell headerPrecio = new Cell().add(new Paragraph("Precio Unit.").setFont(fontBold).setFontSize(12));
            headerPrecio.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerPrecio.setTextAlignment(TextAlignment.CENTER);
            headerPrecio.setWidth(UnitValue.createPercentValue(15));
            detallesTable.addHeaderCell(headerPrecio);
            
            Cell headerSubtotal = new Cell().add(new Paragraph("Subtotal").setFont(fontBold).setFontSize(12));
            headerSubtotal.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerSubtotal.setTextAlignment(TextAlignment.CENTER);
            headerSubtotal.setWidth(UnitValue.createPercentValue(15));
            detallesTable.addHeaderCell(headerSubtotal);
            
            // Filas de detalles
            for (DetalleFacturaDTO detalle : factura.getDetalles()) {
                String descripcion = "";
                if (detalle.getServicioNombre() != null) {
                    descripcion = detalle.getServicioNombre();
                } else if (detalle.getProductoNombre() != null) {
                    descripcion = detalle.getProductoNombre();
                } else {
                    descripcion = detalle.getDescripcion() != null ? detalle.getDescripcion() : "Sin descripción";
                }
                
                // Descripción
                Cell cellDescripcion = new Cell().add(new Paragraph(descripcion).setFont(fontNormal).setFontSize(11));
                cellDescripcion.setPadding(8);
                detallesTable.addCell(cellDescripcion);
                
                // Tipo
                Cell cellTipo = new Cell().add(new Paragraph(detalle.getTipoItem()).setFont(fontNormal).setFontSize(11));
                cellTipo.setTextAlignment(TextAlignment.CENTER);
                cellTipo.setPadding(8);
                detallesTable.addCell(cellTipo);
                
                // Cantidad
                Cell cellCantidad = new Cell().add(new Paragraph(String.valueOf(detalle.getCantidad())).setFont(fontNormal).setFontSize(11));
                cellCantidad.setTextAlignment(TextAlignment.CENTER);
                cellCantidad.setPadding(8);
                detallesTable.addCell(cellCantidad);
                
                // Precio Unitario
                Cell cellPrecio = new Cell().add(new Paragraph("$" + String.format("%,.0f", detalle.getPrecioUnitario().doubleValue())).setFont(fontNormal).setFontSize(11));
                cellPrecio.setTextAlignment(TextAlignment.RIGHT);
                cellPrecio.setPadding(8);
                detallesTable.addCell(cellPrecio);
                
                // Subtotal
                Cell cellSubtotal = new Cell().add(new Paragraph("$" + String.format("%,.0f", detalle.getSubtotal().doubleValue())).setFont(fontNormal).setFontSize(11));
                cellSubtotal.setTextAlignment(TextAlignment.RIGHT);
                cellSubtotal.setPadding(8);
                detallesTable.addCell(cellSubtotal);
            }
            
            document.add(detallesTable);
        } else {
            Paragraph noDetalles = new Paragraph("No hay detalles registrados para esta factura.")
                .setFont(fontItalic)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(noDetalles);
        }
        
        // Totales
        Table totalesTable = new Table(2).useAllAvailableWidth();
        totalesTable.setMarginTop(20);
        totalesTable.setMarginBottom(20);
        totalesTable.setWidth(UnitValue.createPercentValue(100));
        
        // Configurar anchos de columnas para totales
        totalesTable.setWidth(UnitValue.createPercentValue(100));
        
        // Subtotal
        Cell cellSubtotalLabel = new Cell().add(new Paragraph("Subtotal:").setFont(fontBold).setFontSize(12));
        cellSubtotalLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cellSubtotalLabel.setWidth(UnitValue.createPercentValue(70));
        cellSubtotalLabel.setPadding(10);
        totalesTable.addCell(cellSubtotalLabel);
        
        Cell cellSubtotalValue = new Cell().add(new Paragraph("$" + String.format("%,.0f", factura.getSubtotal() != null ? factura.getSubtotal().doubleValue() : 0.0)).setFont(fontNormal).setFontSize(12));
        cellSubtotalValue.setTextAlignment(TextAlignment.RIGHT);
        cellSubtotalValue.setWidth(UnitValue.createPercentValue(30));
        cellSubtotalValue.setPadding(10);
        totalesTable.addCell(cellSubtotalValue);
        
        // Impuestos (si aplica)
        if (factura.getImpuestos() != null && factura.getImpuestos().compareTo(BigDecimal.ZERO) > 0) {
            Cell cellImpuestosLabel = new Cell().add(new Paragraph("Impuestos:").setFont(fontBold).setFontSize(12));
            cellImpuestosLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cellImpuestosLabel.setPadding(10);
            totalesTable.addCell(cellImpuestosLabel);
            
            Cell cellImpuestosValue = new Cell().add(new Paragraph("$" + String.format("%,.0f", factura.getImpuestos().doubleValue())).setFont(fontNormal).setFontSize(12));
            cellImpuestosValue.setTextAlignment(TextAlignment.RIGHT);
            cellImpuestosValue.setPadding(10);
            totalesTable.addCell(cellImpuestosValue);
        }
        
        // Descuento (si aplica)
        if (factura.getDescuento() != null && factura.getDescuento().compareTo(BigDecimal.ZERO) > 0) {
            Cell cellDescuentoLabel = new Cell().add(new Paragraph("Descuento:").setFont(fontBold).setFontSize(12));
            cellDescuentoLabel.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cellDescuentoLabel.setPadding(10);
            totalesTable.addCell(cellDescuentoLabel);
            
            Cell cellDescuentoValue = new Cell().add(new Paragraph("$" + String.format("%,.0f", factura.getDescuento().doubleValue())).setFont(fontNormal).setFontSize(12));
            cellDescuentoValue.setTextAlignment(TextAlignment.RIGHT);
            cellDescuentoValue.setPadding(10);
            totalesTable.addCell(cellDescuentoValue);
        }
        
        // Total
        Cell cellTotalLabel = new Cell().add(new Paragraph("TOTAL:").setFont(fontBold).setFontSize(14));
        cellTotalLabel.setBackgroundColor(ColorConstants.DARK_GRAY);
        cellTotalLabel.setFontColor(ColorConstants.WHITE);
        cellTotalLabel.setPadding(12);
        totalesTable.addCell(cellTotalLabel);
        
        Cell cellTotalValue = new Cell().add(new Paragraph("$" + String.format("%,.0f", factura.getTotal().doubleValue())).setFont(fontBold).setFontSize(14));
        cellTotalValue.setTextAlignment(TextAlignment.RIGHT);
        cellTotalValue.setBackgroundColor(ColorConstants.DARK_GRAY);
        cellTotalValue.setFontColor(ColorConstants.WHITE);
        cellTotalValue.setPadding(12);
        totalesTable.addCell(cellTotalValue);
        
        document.add(totalesTable);
        
        // Observaciones
        if (factura.getObservaciones() != null && !factura.getObservaciones().trim().isEmpty()) {
            Paragraph obsHeader = new Paragraph("OBSERVACIONES")
                .setFont(fontBold)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10);
            document.add(obsHeader);
            
            Paragraph observaciones = new Paragraph(factura.getObservaciones())
                .setFont(fontNormal)
                .setFontSize(12)
                .setMarginBottom(20);
            document.add(observaciones);
        }
        
        // Pie de página
        LineSeparator footerSeparator = new LineSeparator(new SolidLine());
        footerSeparator.setMarginTop(30);
        footerSeparator.setMarginBottom(10);
        document.add(footerSeparator);
        
        Paragraph footer = new Paragraph("Gracias por confiar en Ovy Car - Su taller de confianza")
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
} 