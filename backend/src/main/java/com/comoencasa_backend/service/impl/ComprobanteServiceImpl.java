package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dto.ComprobanteDTO;
import com.comoencasa_backend.dto.PedidoDTO;
import com.comoencasa_backend.model.Comprobante;
import com.comoencasa_backend.model.DetallePedido;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.TipoComprobante;
import com.comoencasa_backend.repository.ComprobanteRepository;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.service.ComprobanteService;
import com.comoencasa_backend.service.PedidoService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {

        private final ComprobanteRepository comprobanteRepo;
        private final PedidoRepository pedidoRepo;
        private PedidoService pedidoService;

        public ComprobanteServiceImpl(
                        ComprobanteRepository comprobanteRepo,
                        PedidoRepository pedidoRepo,
                        PedidoService pedidoService) {
                this.comprobanteRepo = comprobanteRepo;
                this.pedidoRepo = pedidoRepo;
                this.pedidoService = pedidoService;
        }

        @Override
        @Transactional
        public ComprobanteDTO generarComprobante(Long pedidoId, TipoComprobante tipo) {
                Pedido pedido = pedidoRepo.findById(pedidoId)
                                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));

                // Generar numeración secuencial por tipo
                long count = comprobanteRepo.countByTipo(tipo);
                String serie = String.format("%03d", count + 1);
                String numero = String.format("%08d", count + 1);

                Comprobante c = new Comprobante();
                c.setPedido(pedido);
                c.setTipo(tipo);
                c.setNumeroSerie(serie);
                c.setNumeroComprobante(numero);
                c.setSubtotal(pedido.getSubtotal());
                c.setTotal(pedido.getCostoTotal());

                Comprobante saved = comprobanteRepo.save(c);
                log.info("Comprobante generado ID={} para pedido {}", saved.getId(), pedidoId);
                return toDto(saved);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ComprobanteDTO> listarComprobantes(Optional<LocalDateTime> desde,
                        Optional<LocalDateTime> hasta,
                        Optional<String> clienteDocumento,
                        Optional<Long> pedidoId) {

                // Usar el nuevo método que carga eagerly las relaciones
                List<Comprobante> lista = comprobanteRepo.findAllWithPedidoAndUsuario();

                if (pedidoId.isPresent()) {
                        lista = lista.stream()
                                        .filter(c -> c.getPedido().getId().equals(pedidoId.get()))
                                        .collect(Collectors.toList());
                }

                if (clienteDocumento.isPresent()) {
                        lista = lista.stream()
                                        .filter(c -> clienteDocumento.get().equalsIgnoreCase(
                                                        Optional.ofNullable(
                                                                        c.getPedido().getUsuario().getNumeroDocumento())
                                                                        .orElse("")))
                                        .collect(Collectors.toList());
                }

                if (desde.isPresent() && hasta.isPresent()) {
                        lista = lista.stream()
                                        .filter(c -> !c.getFechaEmision().isBefore(desde.get()) &&
                                                        !c.getFechaEmision().isAfter(hasta.get()))
                                        .collect(Collectors.toList());
                }

                return lista.stream()
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ByteArrayInputStream generarExcel(Long id) throws IOException {
                Comprobante c = comprobanteRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado: " + id));

                try (Workbook wb = new XSSFWorkbook();
                                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        CreationHelper createHelper = wb.getCreationHelper();
                        Sheet sheet = wb.createSheet("Comprobante");

                        // Título (usando la clase de POI explícita)
                        org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 16);
                        CellStyle titleStyle = wb.createCellStyle();
                        titleStyle.setFont(titleFont);
                        titleStyle.setAlignment(HorizontalAlignment.CENTER);

                        // Encabezado
                        org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
                        headerFont.setBold(true);
                        headerFont.setColor(IndexedColors.WHITE.getIndex());
                        CellStyle headerStyle = wb.createCellStyle();
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(IndexedColors.PINK.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(HorizontalAlignment.CENTER);
                        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                        // Fecha
                        CellStyle dateStyle = wb.createCellStyle();
                        dateStyle.setDataFormat(
                                        createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));

                        // Moneda
                        CellStyle moneyStyle = wb.createCellStyle();
                        moneyStyle.setDataFormat(
                                        createHelper.createDataFormat().getFormat("\"S/ \"#,##0.00"));
                        moneyStyle.setAlignment(HorizontalAlignment.RIGHT);

                        // Fila de título
                        Row row0 = sheet.createRow(0);
                        Cell cell0 = row0.createCell(0);
                        cell0.setCellValue("COMPROBANTE DE PAGO");
                        cell0.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

                        // Encabezados
                        String[] headers = {
                                        "Cliente", "Documento", "Correo",
                                        "Fecha Emisión", "Tipo", "Serie",
                                        "Número", "Subtotal", "Total"
                        };
                        Row headerRow = sheet.createRow(2);
                        for (int i = 0; i < headers.length; i++) {
                                Cell h = headerRow.createCell(i);
                                h.setCellValue(headers[i]);
                                h.setCellStyle(headerStyle);
                        }

                        // Datos
                        Row dataRow = sheet.createRow(3);
                        String cliente = c.getPedido().getUsuario().getNombre()
                                        + " " + c.getPedido().getUsuario().getApellido();
                        dataRow.createCell(0).setCellValue(cliente);
                        dataRow.createCell(1).setCellValue(
                                        Optional.ofNullable(c.getPedido().getUsuario().getNumeroDocumento())
                                                        .orElse("-"));
                        dataRow.createCell(2).setCellValue(c.getPedido().getUsuario().getEmail());

                        Cell fcell = dataRow.createCell(3);
                        fcell.setCellValue(
                                        java.util.Date.from(
                                                        c.getFechaEmision().atZone(ZoneId.systemDefault())
                                                                        .toInstant()));
                        fcell.setCellStyle(dateStyle);

                        dataRow.createCell(4).setCellValue(c.getTipo().name());
                        dataRow.createCell(5).setCellValue(c.getNumeroSerie());
                        dataRow.createCell(6).setCellValue(c.getNumeroComprobante());

                        Cell scell = dataRow.createCell(7);
                        scell.setCellValue(c.getSubtotal().doubleValue());
                        scell.setCellStyle(moneyStyle);

                        Cell tcell = dataRow.createCell(8);
                        tcell.setCellValue(c.getTotal().doubleValue());
                        tcell.setCellStyle(moneyStyle);

                        // Auto-size
                        for (int i = 0; i < headers.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        wb.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        @Transactional(readOnly = true)
        public ByteArrayInputStream generarPdf(Long id) throws IOException {
                Comprobante c = comprobanteRepo.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado: " + id));

                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                        Document document = new Document(PageSize.A4, 36, 36, 90, 36);
                        PdfWriter writer = PdfWriter.getInstance(document, out);
                        document.open();

                        // Fonts y colores
                        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18,
                                        BaseColor.DARK_GRAY);
                        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12,
                                        BaseColor.WHITE);
                        com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11,
                                        BaseColor.DARK_GRAY);
                        com.itextpdf.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11,
                                        BaseColor.DARK_GRAY);
                        BaseColor pinkBg = new BaseColor(255, 107, 166);
                        BaseColor lightBg = new BaseColor(255, 228, 240);

                        // Logo y encabezado
                        Image logo = Image.getInstance("./frontend/src/assets/logo.png");
                        logo.scaleToFit(120, 60);
                        logo.setAlignment(Element.ALIGN_LEFT);

                        PdfPTable headerTable = new PdfPTable(2);
                        headerTable.setWidths(new float[] { 1, 2 });
                        headerTable.setWidthPercentage(100);
                        PdfPCell logoCell = new PdfPCell(logo, false);
                        logoCell.setBorder(PdfPCell.NO_BORDER);
                        PdfPCell titleCell = new PdfPCell(new Phrase("COMPROBANTE DE PAGO", titleFont));
                        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        titleCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                        titleCell.setBorder(PdfPCell.NO_BORDER);

                        headerTable.addCell(logoCell);
                        headerTable.addCell(titleCell);
                        document.add(headerTable);

                        document.add(Chunk.NEWLINE);

                        // Datos de la empresa
                        Paragraph empresa = new Paragraph(
                                        "Como En Casa\nAv. Universitaria 123, Lima\nTel: (51) 972-166-643   Email: comoencasa@gmail.com",
                                        normalFont);
                        empresa.setAlignment(Element.ALIGN_LEFT);
                        document.add(empresa);

                        document.add(Chunk.NEWLINE);

                        // Datos del comprobante y fecha
                        PdfPTable infoComp = new PdfPTable(2);
                        infoComp.setWidths(new float[] { 1, 1 });
                        infoComp.setWidthPercentage(100);

                        PdfPCell cell1 = new PdfPCell(new Phrase("N° Serie: " + c.getNumeroSerie(), boldFont));
                        PdfPCell cell2 = new PdfPCell(new Phrase(
                                        "Fecha: " + c.getFechaEmision()
                                                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                                        boldFont));
                        PdfPCell cell3 = new PdfPCell(
                                        new Phrase("N° Comprobante: " + c.getNumeroComprobante(), boldFont));
                        PdfPCell cell4 = new PdfPCell(new Phrase("Tipo: " + c.getTipo().name(), boldFont));

                        for (PdfPCell cell : Arrays.asList(cell1, cell2, cell3, cell4)) {
                                cell.setPadding(6);
                                cell.setBorder(PdfPCell.NO_BORDER);
                        }
                        infoComp.addCell(cell1);
                        infoComp.addCell(cell2);
                        infoComp.addCell(cell3);
                        infoComp.addCell(cell4);
                        document.add(infoComp);

                        document.add(Chunk.NEWLINE);

                        // Datos del cliente
                        PdfPTable clienteTbl = new PdfPTable(1);
                        clienteTbl.setWidthPercentage(100);
                        PdfPCell clienteHeader = new PdfPCell(new Phrase("DATOS DEL CLIENTE", boldFont));
                        clienteHeader.setBackgroundColor(pinkBg);
                        clienteHeader.setPadding(8);
                        clienteHeader.setBorder(PdfPCell.NO_BORDER);
                        clienteHeader.setHorizontalAlignment(Element.ALIGN_LEFT);

                        String nombre = c.getPedido().getUsuario().getNombre() + " "
                                        + c.getPedido().getUsuario().getApellido();
                        PdfPCell clienteData = new PdfPCell(new Phrase(
                                        "Nombre: " + nombre + "\n" +
                                                        "Documento: "
                                                        + Optional.ofNullable(
                                                                        c.getPedido().getUsuario().getNumeroDocumento())
                                                                        .orElse("-")
                                                        + "\n" +
                                                        "Correo: " + c.getPedido().getUsuario().getEmail(),
                                        normalFont));
                        clienteData.setPadding(8);
                        clienteData.setBorder(PdfPCell.NO_BORDER);

                        clienteTbl.addCell(clienteHeader);
                        clienteTbl.addCell(clienteData);
                        document.add(clienteTbl);

                        document.add(Chunk.NEWLINE);

                        // Tabla de líneas de pedido
                        PdfPTable tbl = new PdfPTable(new float[] { 4, 1.5f, 1.5f, 1.5f });
                        tbl.setWidthPercentage(100);
                        for (String h : new String[] { "Producto", "Cant.", "P.Unit.", "Subtotal" }) {
                                PdfPCell cHdr = new PdfPCell(new Phrase(h, headerFont));
                                cHdr.setBackgroundColor(pinkBg);
                                cHdr.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cHdr.setPadding(6);
                                tbl.addCell(cHdr);
                        }

                        for (DetallePedido det : c.getPedido().getDetallePedidos()) {
                                tbl.addCell(new PdfPCell(new Phrase(det.getProducto().getNombre(), normalFont)));
                                tbl.addCell(alignedCell(det.getCantidad().toString(), normalFont,
                                                Element.ALIGN_CENTER));
                                tbl.addCell(alignedCell("S/ " + det.getPrecioUnitario(), normalFont,
                                                Element.ALIGN_RIGHT));
                                BigDecimal sub = det.getPrecioUnitario().multiply(
                                                new java.math.BigDecimal(det.getCantidad()));
                                tbl.addCell(alignedCell("S/ " + sub, normalFont, Element.ALIGN_RIGHT));
                        }

                        // Totales
                        PdfPCell span = new PdfPCell(new Phrase(""));
                        span.setColspan(2);
                        span.setBorder(PdfPCell.NO_BORDER);
                        tbl.addCell(span);

                        tbl.addCell(alignedCell("TOTAL:", boldFont, Element.ALIGN_RIGHT));
                        tbl.addCell(alignedCell("S/ " + c.getTotal(), boldFont, Element.ALIGN_RIGHT));

                        document.add(tbl);
                        document.add(Chunk.NEWLINE);

                        // Footer de agradecimiento
                        Paragraph footer = new Paragraph(
                                        "¡Gracias por su compra! Si tiene dudas, contáctenos al (51) 972-166-643 o comoencasa@gmail.com",
                                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
                        footer.setAlignment(Element.ALIGN_CENTER);
                        document.add(footer);

                        document.close();
                        return new ByteArrayInputStream(out.toByteArray());
                } catch (DocumentException de) {
                        throw new IOException("Error generando PDF", de);
                }
        }

        private PdfPCell alignedCell(String text, com.itextpdf.text.Font font, int alignment) {
                PdfPCell cell = new PdfPCell(new Phrase(text, font));
                cell.setHorizontalAlignment(alignment);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(6);
                return cell;
        }

        private static PdfPCell cellNoBorder(String text, com.itextpdf.text.Font f) {
                PdfPCell c = new PdfPCell(new Phrase(text, f));
                c.setBorder(PdfPCell.NO_BORDER);
                c.setPadding(4);
                return c;
        }

        private ComprobanteDTO toDto(Comprobante c) {
                ComprobanteDTO dto = new ComprobanteDTO();
                dto.setId(c.getId());
                dto.setPedidoId(c.getPedido().getId());
                dto.setTipo(c.getTipo().name());
                dto.setFechaEmision(c.getFechaEmision());
                dto.setNumeroSerie(c.getNumeroSerie());
                dto.setNumeroComprobante(c.getNumeroComprobante());
                dto.setSubtotal(c.getSubtotal());
                dto.setTotal(c.getTotal());

                String nombreCompleto = c.getPedido().getUsuario().getNombre() +
                                " " + c.getPedido().getUsuario().getApellido();
                dto.setClienteNombre(nombreCompleto);
                dto.setClienteDocumento(c.getPedido().getUsuario().getNumeroDocumento());
                dto.setClienteEmail(c.getPedido().getUsuario().getEmail());

                return dto;
        }

        @Override
        public ByteArrayInputStream generarReporteVentasExcel(Optional<LocalDateTime> desde,
                        Optional<LocalDateTime> hasta) throws IOException {
                List<PedidoDTO> pedidos = pedidoService.findAll();

                // Filtrar por fechas si se proporcionan
                if (desde.isPresent() || hasta.isPresent()) {
                        pedidos = pedidos.stream()
                                        .filter(p -> {
                                                LocalDateTime fechaPedido = p.getFechaCreacion();

                                                // Si hay fecha desde, verificar que la fecha del pedido sea posterior o
                                                // igual
                                                if (desde.isPresent() && fechaPedido.isBefore(desde.get())) {
                                                        return false;
                                                }

                                                // Si hay fecha hasta, verificar que la fecha del pedido sea anterior o
                                                // igual
                                                if (hasta.isPresent() && fechaPedido.isAfter(hasta.get())) {
                                                        return false;
                                                }

                                                return true;
                                        })
                                        .collect(Collectors.toList());
                }

                try (Workbook workbook = new XSSFWorkbook()) {
                        Sheet sheet = workbook.createSheet("Reporte de Ventas");

                        // Crear estilo para el encabezado
                        CellStyle headerStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setFontHeightInPoints((short) 12);
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                        // Crear estilo para números
                        CellStyle currencyStyle = workbook.createCellStyle();
                        currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("S/. #,##0.00"));

                        // Título del reporte
                        Row titleRow = sheet.createRow(0);
                        Cell titleCell = titleRow.createCell(0);
                        titleCell.setCellValue("REPORTE DE VENTAS - COMO EN CASA");

                        CellStyle titleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 16);
                        titleStyle.setFont(titleFont);
                        titleCell.setCellStyle(titleStyle);

                        // Combinar celdas para el título
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

                        // Información de fechas del reporte
                        if (desde.isPresent() || hasta.isPresent()) {
                                Row dateRow = sheet.createRow(1);
                                String dateInfo = "Período: ";
                                if (desde.isPresent()) {
                                        dateInfo += "Desde "
                                                        + desde.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                }
                                if (hasta.isPresent()) {
                                        if (desde.isPresent()) {
                                                dateInfo += " hasta ";
                                        } else {
                                                dateInfo += "Hasta ";
                                        }
                                        dateInfo += hasta.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                }
                                dateRow.createCell(0).setCellValue(dateInfo);
                        }

                        // Encabezados de columnas
                        Row header = sheet.createRow(3);
                        String[] columnas = { "ID Pedido", "Cliente", "Fecha Pedido", "Estado", "Total", "Productos" };
                        for (int i = 0; i < columnas.length; i++) {
                                Cell cell = header.createCell(i);
                                cell.setCellValue(columnas[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Datos de pedidos
                        int rowIdx = 4;
                        BigDecimal totalGeneral = BigDecimal.ZERO;

                        for (PedidoDTO p : pedidos) {
                                Row row = sheet.createRow(rowIdx++);
                                row.createCell(0).setCellValue(p.getId());
                                row.createCell(1).setCellValue(
                                                p.getUsuarioNombre() != null ? p.getUsuarioNombre() : "N/A");
                                row.createCell(2).setCellValue(p.getFechaCreacion()
                                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                                row.createCell(3).setCellValue(p.getEstado());

                                // Aplicar formato de moneda al total
                                Cell totalCell = row.createCell(4);
                                totalCell.setCellValue(p.getCostoTotal().doubleValue());
                                totalCell.setCellStyle(currencyStyle);

                                // Agregar productos del pedido con validación null-safe
                                String productos = "";
                                if (p.getDetalles() != null && !p.getDetalles().isEmpty()) {
                                        productos = p.getDetalles().stream()
                                                        .map(detalle -> detalle.getNombreProducto() + " (x"
                                                                        + detalle.getCantidad() + ")")
                                                        .collect(Collectors.joining(", "));
                                } else {
                                        productos = "Sin detalles";
                                }
                                row.createCell(5).setCellValue(productos);

                                totalGeneral = totalGeneral.add(p.getCostoTotal());
                        }

                        // Fila de total general
                        Row totalRow = sheet.createRow(rowIdx + 1);
                        Cell totalLabelCell = totalRow.createCell(3);
                        totalLabelCell.setCellValue("TOTAL GENERAL:");
                        totalLabelCell.setCellStyle(headerStyle);

                        Cell totalValueCell = totalRow.createCell(4);
                        totalValueCell.setCellValue(totalGeneral.doubleValue());
                        totalValueCell.setCellStyle(currencyStyle);

                        // Fila de resumen
                        Row summaryRow = sheet.createRow(rowIdx + 3);
                        summaryRow.createCell(0).setCellValue("Total de pedidos: " + pedidos.size());
                        summaryRow.createCell(2).setCellValue("Fecha de generación: "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                        // Auto-ajustar columnas
                        for (int i = 0; i < columnas.length; i++) {
                                sheet.autoSizeColumn(i);
                        }

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }

        @Override
        @Transactional(readOnly = true)
        public ByteArrayInputStream generarReporteComprobantesExcel(Optional<LocalDateTime> desde,
                        Optional<LocalDateTime> hasta, TipoComprobante tipo) throws IOException {
                List<ComprobanteDTO> comprobantes = listarComprobantes(desde, hasta, Optional.empty(),
                                Optional.empty());

                // Filtrar por tipo de comprobante
                comprobantes = comprobantes.stream()
                                .filter(c -> c.getTipo().equals(tipo.name()))
                                .collect(Collectors.toList());
                System.out.println("📊 Comprobantes encontrados de tipo " + tipo + ": " + comprobantes.size());

                try (Workbook workbook = new XSSFWorkbook()) {
                        Sheet sheet = workbook.createSheet("Reporte de " + tipo.name() + "s");

                        // ================================
                        // ESTILOS PROFESIONALES MEJORADOS
                        // ================================

                        // Estilo para título principal - Más llamativo
                        CellStyle titleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                        titleFont.setBold(true);
                        titleFont.setFontHeightInPoints((short) 18);
                        titleFont.setColor(IndexedColors.WHITE.getIndex());
                        titleStyle.setFont(titleFont);
                        titleStyle.setAlignment(HorizontalAlignment.CENTER);
                        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        titleStyle.setBorderBottom(BorderStyle.THICK);
                        titleStyle.setBorderTop(BorderStyle.THICK);
                        titleStyle.setBorderLeft(BorderStyle.THICK);
                        titleStyle.setBorderRight(BorderStyle.THICK);

                        // Estilo para subtítulo/información de fechas
                        CellStyle subtitleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font subtitleFont = workbook.createFont();
                        subtitleFont.setItalic(true);
                        subtitleFont.setFontHeightInPoints((short) 11);
                        subtitleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                        subtitleStyle.setFont(subtitleFont);
                        subtitleStyle.setAlignment(HorizontalAlignment.CENTER);
                        subtitleStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
                        subtitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                        // Estilo para encabezados de columnas - Más elegante
                        CellStyle headerStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setColor(IndexedColors.WHITE.getIndex());
                        headerFont.setFontHeightInPoints((short) 12);
                        headerStyle.setFont(headerFont);
                        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
                        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        headerStyle.setAlignment(HorizontalAlignment.CENTER);
                        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
                        headerStyle.setBorderTop(BorderStyle.MEDIUM);
                        headerStyle.setBorderLeft(BorderStyle.THIN);
                        headerStyle.setBorderRight(BorderStyle.THIN);
                        headerStyle.setWrapText(true);

                        // Estilo para datos normales - Alternando colores
                        CellStyle dataStyle1 = workbook.createCellStyle();
                        dataStyle1.setAlignment(HorizontalAlignment.LEFT);
                        dataStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
                        dataStyle1.setBorderBottom(BorderStyle.THIN);
                        dataStyle1.setBorderTop(BorderStyle.THIN);
                        dataStyle1.setBorderLeft(BorderStyle.THIN);
                        dataStyle1.setBorderRight(BorderStyle.THIN);

                        // Estilo para filas alternas
                        CellStyle dataStyle2 = workbook.createCellStyle();
                        dataStyle2.cloneStyleFrom(dataStyle1);
                        dataStyle2.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
                        dataStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                        // Estilo para números de moneda - Mejorado
                        CellStyle currencyStyle1 = workbook.createCellStyle();
                        currencyStyle1.cloneStyleFrom(dataStyle1);
                        currencyStyle1.setDataFormat(workbook.createDataFormat().getFormat("\"S/. \"#,##0.00"));
                        currencyStyle1.setAlignment(HorizontalAlignment.RIGHT);

                        CellStyle currencyStyle2 = workbook.createCellStyle();
                        currencyStyle2.cloneStyleFrom(dataStyle2);
                        currencyStyle2.setDataFormat(workbook.createDataFormat().getFormat("\"S/. \"#,##0.00"));
                        currencyStyle2.setAlignment(HorizontalAlignment.RIGHT);

                        // Estilo para fechas
                        CellStyle dateStyle1 = workbook.createCellStyle();
                        dateStyle1.cloneStyleFrom(dataStyle1);
                        dateStyle1.setAlignment(HorizontalAlignment.CENTER);

                        CellStyle dateStyle2 = workbook.createCellStyle();
                        dateStyle2.cloneStyleFrom(dataStyle2);
                        dateStyle2.setAlignment(HorizontalAlignment.CENTER);

                        // Estilo para totales - Más destacado
                        CellStyle totalStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
                        totalFont.setBold(true);
                        totalFont.setColor(IndexedColors.WHITE.getIndex());
                        totalFont.setFontHeightInPoints((short) 12);
                        totalStyle.setFont(totalFont);
                        totalStyle.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
                        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        totalStyle.setAlignment(HorizontalAlignment.RIGHT);
                        totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        totalStyle.setBorderBottom(BorderStyle.THICK);
                        totalStyle.setBorderTop(BorderStyle.THICK);
                        totalStyle.setBorderLeft(BorderStyle.MEDIUM);
                        totalStyle.setBorderRight(BorderStyle.MEDIUM);
                        totalStyle.setDataFormat(workbook.createDataFormat().getFormat("\"S/. \"#,##0.00"));

                        // Estilo para etiquetas de totales
                        CellStyle totalLabelStyle = workbook.createCellStyle();
                        totalLabelStyle.cloneStyleFrom(totalStyle);
                        totalLabelStyle.setAlignment(HorizontalAlignment.CENTER);

                        // Estilo para resumen
                        CellStyle summaryStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font summaryFont = workbook.createFont();
                        summaryFont.setItalic(true);
                        summaryFont.setFontHeightInPoints((short) 10);
                        summaryFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                        summaryStyle.setFont(summaryFont);
                        summaryStyle.setAlignment(HorizontalAlignment.LEFT);

                        // ================================
                        // CONTENIDO DEL REPORTE
                        // ================================

                        // Título principal con altura de fila y logo emoji
                        Row titleRow = sheet.createRow(0);
                        titleRow.setHeightInPoints(35);
                        Cell titleCell = titleRow.createCell(0);
                        titleCell.setCellValue("🏢 PASTELERÍA COMO EN CASA 🧁");
                        titleCell.setCellStyle(titleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

                        // Subtítulo con tipo de reporte
                        Row subtitleReportRow = sheet.createRow(1);
                        subtitleReportRow.setHeightInPoints(25);
                        Cell subtitleReportCell = subtitleReportRow.createCell(0);

                        // Agregar emoji específico según tipo
                        String emoji = tipo == TipoComprobante.Factura ? "🧾" : "📄";
                        subtitleReportCell.setCellValue(
                                        emoji + " REPORTE DE " + tipo.name().toUpperCase() + "S " + emoji);

                        // Estilo especial para subtítulo de reporte
                        CellStyle reportSubtitleStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font reportSubtitleFont = workbook.createFont();
                        reportSubtitleFont.setBold(true);
                        reportSubtitleFont.setFontHeightInPoints((short) 14);
                        reportSubtitleFont.setColor(IndexedColors.WHITE.getIndex());
                        reportSubtitleStyle.setFont(reportSubtitleFont);
                        reportSubtitleStyle.setAlignment(HorizontalAlignment.CENTER);
                        reportSubtitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        reportSubtitleStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
                        reportSubtitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        reportSubtitleStyle.setBorderBottom(BorderStyle.MEDIUM);
                        reportSubtitleStyle.setBorderTop(BorderStyle.MEDIUM);
                        reportSubtitleStyle.setBorderLeft(BorderStyle.MEDIUM);
                        reportSubtitleStyle.setBorderRight(BorderStyle.MEDIUM);

                        subtitleReportCell.setCellStyle(reportSubtitleStyle);
                        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 8));

                        int currentRow = 2;

                        // Información de fechas con mejor estilo
                        if (desde.isPresent() || hasta.isPresent()) {
                                Row dateRow = sheet.createRow(currentRow);
                                dateRow.setHeightInPoints(20);
                                Cell dateCell = dateRow.createCell(0);
                                String dateInfo = "📅 Período: ";
                                if (desde.isPresent()) {
                                        dateInfo += "Desde "
                                                        + desde.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                }
                                if (hasta.isPresent()) {
                                        if (desde.isPresent()) {
                                                dateInfo += " hasta ";
                                        } else {
                                                dateInfo += "Hasta ";
                                        }
                                        dateInfo += hasta.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                                } else if (!desde.isPresent()) {
                                        dateInfo = "📅 Todos los registros";
                                }
                                dateCell.setCellValue(dateInfo);
                                dateCell.setCellStyle(subtitleStyle);
                                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
                                currentRow++;
                        } else {
                                Row dateRow = sheet.createRow(currentRow);
                                dateRow.setHeightInPoints(20);
                                Cell dateCell = dateRow.createCell(0);
                                dateCell.setCellValue("📅 Todos los registros disponibles");
                                dateCell.setCellStyle(subtitleStyle);
                                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, 8));
                                currentRow++;
                        }

                        // Línea en blanco para separación
                        currentRow++;

                        // Encabezados con mejor estilo
                        int headerRowIndex = currentRow;
                        Row header = sheet.createRow(headerRowIndex);
                        header.setHeightInPoints(25);
                        String[] columnas = {
                                        "🆔 ID", "📄 Serie", "🔢 Número", "👤 Cliente",
                                        "📋 Documento", "📧 Email", "📅 Fecha",
                                        "💰 Subtotal", "💵 Total"
                        };

                        for (int i = 0; i < columnas.length; i++) {
                                Cell cell = header.createCell(i);
                                cell.setCellValue(columnas[i]);
                                cell.setCellStyle(headerStyle);
                        }

                        // Datos con filas alternas y mejor formato
                        int rowIdx = headerRowIndex + 1;
                        BigDecimal totalGeneral = BigDecimal.ZERO;
                        BigDecimal subtotalGeneral = BigDecimal.ZERO;

                        int dataRowCounter = 0;
                        for (ComprobanteDTO c : comprobantes) {
                                Row row = sheet.createRow(rowIdx++);
                                row.setHeightInPoints(18);

                                // Alternar estilos para mejor legibilidad
                                boolean isEvenRow = (dataRowCounter % 2 == 0);
                                CellStyle currentDataStyle = isEvenRow ? dataStyle1 : dataStyle2;
                                CellStyle currentCurrencyStyle = isEvenRow ? currencyStyle1 : currencyStyle2;
                                CellStyle currentDateStyle = isEvenRow ? dateStyle1 : dateStyle2;

                                // ID
                                Cell idCell = row.createCell(0);
                                idCell.setCellValue(c.getId());
                                idCell.setCellStyle(currentDataStyle);

                                // Serie
                                Cell serieCell = row.createCell(1);
                                serieCell.setCellValue(c.getNumeroSerie() != null ? c.getNumeroSerie() : "N/A");
                                serieCell.setCellStyle(currentDataStyle);

                                // Número
                                Cell numeroCell = row.createCell(2);
                                numeroCell.setCellValue(
                                                c.getNumeroComprobante() != null ? c.getNumeroComprobante() : "N/A");
                                numeroCell.setCellStyle(currentDataStyle);

                                // Cliente
                                Cell clienteCell = row.createCell(3);
                                clienteCell.setCellValue(c.getClienteNombre() != null ? c.getClienteNombre() : "N/A");
                                clienteCell.setCellStyle(currentDataStyle);

                                // Documento
                                Cell documentoCell = row.createCell(4);
                                documentoCell.setCellValue(
                                                c.getClienteDocumento() != null ? c.getClienteDocumento() : "N/A");
                                documentoCell.setCellStyle(currentDataStyle);

                                // Email
                                Cell emailCell = row.createCell(5);
                                emailCell.setCellValue(c.getClienteEmail() != null ? c.getClienteEmail() : "N/A");
                                emailCell.setCellStyle(currentDataStyle);

                                // Fecha
                                Cell fechaCell = row.createCell(6);
                                fechaCell.setCellValue(c.getFechaEmision()
                                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                                fechaCell.setCellStyle(currentDateStyle);

                                // Subtotal
                                Cell subtotalCell = row.createCell(7);
                                subtotalCell.setCellValue(c.getSubtotal().doubleValue());
                                subtotalCell.setCellStyle(currentCurrencyStyle);

                                // Total
                                Cell totalCell = row.createCell(8);
                                totalCell.setCellValue(c.getTotal().doubleValue());
                                totalCell.setCellStyle(currentCurrencyStyle);

                                subtotalGeneral = subtotalGeneral.add(c.getSubtotal());
                                totalGeneral = totalGeneral.add(c.getTotal());
                        }

                        // Línea separadora antes de totales
                        Row separatorRow = sheet.createRow(rowIdx);
                        Cell separatorCell = separatorRow.createCell(0);
                        separatorCell.setCellValue("");

                        // Totales con mejor diseño y altura
                        Row totalRow = sheet.createRow(rowIdx + 1);
                        totalRow.setHeightInPoints(25);

                        // Estilo mejorado para etiqueta de totales
                        CellStyle totalLabelEnhanced = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font totalLabelFont = workbook.createFont();
                        totalLabelFont.setBold(true);
                        totalLabelFont.setColor(IndexedColors.WHITE.getIndex());
                        totalLabelFont.setFontHeightInPoints((short) 13);
                        totalLabelEnhanced.setFont(totalLabelFont);
                        totalLabelEnhanced.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                        totalLabelEnhanced.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        totalLabelEnhanced.setAlignment(HorizontalAlignment.CENTER);
                        totalLabelEnhanced.setVerticalAlignment(VerticalAlignment.CENTER);
                        totalLabelEnhanced.setBorderBottom(BorderStyle.THICK);
                        totalLabelEnhanced.setBorderTop(BorderStyle.THICK);
                        totalLabelEnhanced.setBorderLeft(BorderStyle.THICK);
                        totalLabelEnhanced.setBorderRight(BorderStyle.THICK);

                        Cell totalLabelCell = totalRow.createCell(6);
                        totalLabelCell.setCellValue("💰 TOTALES");
                        totalLabelCell.setCellStyle(totalLabelEnhanced);

                        // Estilo mejorado para valores de totales
                        CellStyle totalValueEnhanced = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font totalValueFont = workbook.createFont();
                        totalValueFont.setBold(true);
                        totalValueFont.setColor(IndexedColors.WHITE.getIndex());
                        totalValueFont.setFontHeightInPoints((short) 12);
                        totalValueEnhanced.setFont(totalValueFont);
                        totalValueEnhanced.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
                        totalValueEnhanced.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        totalValueEnhanced.setAlignment(HorizontalAlignment.RIGHT);
                        totalValueEnhanced.setVerticalAlignment(VerticalAlignment.CENTER);
                        totalValueEnhanced.setBorderBottom(BorderStyle.THICK);
                        totalValueEnhanced.setBorderTop(BorderStyle.THICK);
                        totalValueEnhanced.setBorderLeft(BorderStyle.THICK);
                        totalValueEnhanced.setBorderRight(BorderStyle.THICK);
                        totalValueEnhanced.setDataFormat(workbook.createDataFormat().getFormat("\"S/. \"#,##0.00"));

                        Cell subtotalValueCell = totalRow.createCell(7);
                        subtotalValueCell.setCellValue(subtotalGeneral.doubleValue());
                        subtotalValueCell.setCellStyle(totalValueEnhanced);

                        Cell totalValueCell = totalRow.createCell(8);
                        totalValueCell.setCellValue(totalGeneral.doubleValue());
                        totalValueCell.setCellStyle(totalValueEnhanced);

                        // Estilo para sección de resumen con fondo
                        CellStyle summaryHeaderStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font summaryHeaderFont = workbook.createFont();
                        summaryHeaderFont.setBold(true);
                        summaryHeaderFont.setColor(IndexedColors.WHITE.getIndex());
                        summaryHeaderFont.setFontHeightInPoints((short) 11);
                        summaryHeaderStyle.setFont(summaryHeaderFont);
                        summaryHeaderStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
                        summaryHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        summaryHeaderStyle.setAlignment(HorizontalAlignment.LEFT);
                        summaryHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        summaryHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
                        summaryHeaderStyle.setBorderTop(BorderStyle.MEDIUM);
                        summaryHeaderStyle.setBorderLeft(BorderStyle.MEDIUM);
                        summaryHeaderStyle.setBorderRight(BorderStyle.MEDIUM);

                        // Estilo para valores del resumen
                        CellStyle summaryValueStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font summaryValueFont = workbook.createFont();
                        summaryValueFont.setFontHeightInPoints((short) 10);
                        summaryValueFont.setColor(IndexedColors.DARK_BLUE.getIndex());
                        summaryValueStyle.setFont(summaryValueFont);
                        summaryValueStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                        summaryValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        summaryValueStyle.setAlignment(HorizontalAlignment.LEFT);
                        summaryValueStyle.setBorderBottom(BorderStyle.THIN);
                        summaryValueStyle.setBorderTop(BorderStyle.THIN);
                        summaryValueStyle.setBorderLeft(BorderStyle.THIN);
                        summaryValueStyle.setBorderRight(BorderStyle.THIN);

                        // Título de sección de resumen
                        Row summaryHeaderRow = sheet.createRow(rowIdx + 3);
                        summaryHeaderRow.setHeightInPoints(22);
                        Cell summaryHeaderCell = summaryHeaderRow.createCell(0);
                        summaryHeaderCell.setCellValue("📊 RESUMEN EJECUTIVO");
                        summaryHeaderCell.setCellStyle(summaryHeaderStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowIdx + 3, rowIdx + 3, 0, 3));

                        // Resumen con mejor formato
                        Row summaryRow1 = sheet.createRow(rowIdx + 4);
                        summaryRow1.setHeightInPoints(18);
                        Cell summaryCell1 = summaryRow1.createCell(0);
                        summaryCell1.setCellValue("📋 Total de " + tipo.name().toLowerCase() + "s procesados:");
                        summaryCell1.setCellStyle(summaryValueStyle);
                        Cell summaryValue1 = summaryRow1.createCell(2);
                        summaryValue1.setCellValue(comprobantes.size() + " documentos");
                        summaryValue1.setCellStyle(summaryValueStyle);

                        Row summaryRow2 = sheet.createRow(rowIdx + 5);
                        summaryRow2.setHeightInPoints(18);
                        Cell summaryCell2 = summaryRow2.createCell(0);
                        summaryCell2.setCellValue("💵 Monto promedio por documento:");
                        summaryCell2.setCellStyle(summaryValueStyle);
                        Cell summaryValue2 = summaryRow2.createCell(2);
                        summaryValue2.setCellValue("S/. " + (comprobantes.size() > 0
                                        ? String.format("%.2f", totalGeneral.doubleValue() / comprobantes.size())
                                        : "0.00"));
                        summaryValue2.setCellStyle(summaryValueStyle);

                        // Estilo especial para información de generación
                        CellStyle generatedStyle = workbook.createCellStyle();
                        org.apache.poi.ss.usermodel.Font generatedFont = workbook.createFont();
                        generatedFont.setItalic(true);
                        generatedFont.setFontHeightInPoints((short) 9);
                        generatedFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
                        generatedStyle.setFont(generatedFont);
                        generatedStyle.setAlignment(HorizontalAlignment.RIGHT);

                        Row summaryRow3 = sheet.createRow(rowIdx + 7);
                        Cell summaryCell3 = summaryRow3.createCell(6);
                        summaryCell3.setCellValue("🕒 Generado: " +
                                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                        summaryCell3.setCellStyle(generatedStyle);
                        sheet.addMergedRegion(new CellRangeAddress(rowIdx + 7, rowIdx + 7, 6, 8));

                        // Auto-ajustar columnas con anchos mínimos y máximos
                        for (int i = 0; i < columnas.length; i++) {
                                sheet.autoSizeColumn(i);
                                // Establecer anchos mínimos para mejor presentación
                                int currentWidth = sheet.getColumnWidth(i);
                                int minWidth = 0;
                                switch (i) {
                                        case 0:
                                                minWidth = 2000;
                                                break; // ID
                                        case 1:
                                                minWidth = 2500;
                                                break; // Serie
                                        case 2:
                                                minWidth = 2500;
                                                break; // Número
                                        case 3:
                                                minWidth = 4000;
                                                break; // Cliente
                                        case 4:
                                                minWidth = 3000;
                                                break; // Documento
                                        case 5:
                                                minWidth = 5000;
                                                break; // Email
                                        case 6:
                                                minWidth = 4000;
                                                break; // Fecha
                                        case 7:
                                                minWidth = 3000;
                                                break; // Subtotal
                                        case 8:
                                                minWidth = 3000;
                                                break; // Total
                                }
                                sheet.setColumnWidth(i, Math.max(currentWidth, minWidth));
                        }

                        // Establecer zoom para mejor visualización
                        sheet.setZoom(85);

                        // Congelar paneles para mantener encabezados visibles
                        sheet.createFreezePane(0, headerRowIndex + 1);

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        workbook.write(out);
                        return new ByteArrayInputStream(out.toByteArray());
                }
        }
}