package com.comoencasa_backend.service.impl;

import com.comoencasa_backend.dto.ComprobanteDTO;
import com.comoencasa_backend.model.Comprobante;
import com.comoencasa_backend.model.DetallePedido;
import com.comoencasa_backend.model.Pedido;
import com.comoencasa_backend.model.TipoComprobante;
import com.comoencasa_backend.repository.ComprobanteRepository;
import com.comoencasa_backend.repository.PedidoRepository;
import com.comoencasa_backend.service.ComprobanteService;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
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

    public ComprobanteServiceImpl(ComprobanteRepository comprobanteRepo,
            PedidoRepository pedidoRepo) {
        this.comprobanteRepo = comprobanteRepo;
        this.pedidoRepo = pedidoRepo;
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
        List<Comprobante> lista = comprobanteRepo.findAll();

        if (pedidoId.isPresent()) {
            lista = comprobanteRepo.findByPedido_Id(pedidoId.get());
        }
        if (clienteDocumento.isPresent()) {
            lista = comprobanteRepo.findByPedido_Usuario_NumeroDocumento(clienteDocumento.get());
        }
        if (desde.isPresent() && hasta.isPresent()) {
            lista = comprobanteRepo.findByFechaEmisionBetween(desde.get(), hasta.get());
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
                    Optional.ofNullable(c.getPedido().getUsuario().getNumeroDocumento()).orElse("-"));
            dataRow.createCell(2).setCellValue(c.getPedido().getUsuario().getEmail());

            Cell fcell = dataRow.createCell(3);
            fcell.setCellValue(
                    java.util.Date.from(
                            c.getFechaEmision().atZone(ZoneId.systemDefault()).toInstant()));
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
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.DARK_GRAY);
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
                    "Fecha: " + c.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), boldFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("N° Comprobante: " + c.getNumeroComprobante(), boldFont));
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

            String nombre = c.getPedido().getUsuario().getNombre() + " " + c.getPedido().getUsuario().getApellido();
            PdfPCell clienteData = new PdfPCell(new Phrase(
                    "Nombre: " + nombre + "\n" +
                            "Documento: "
                            + Optional.ofNullable(c.getPedido().getUsuario().getNumeroDocumento()).orElse("-") + "\n" +
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
                tbl.addCell(alignedCell(det.getCantidad().toString(), normalFont, Element.ALIGN_CENTER));
                tbl.addCell(alignedCell("S/ " + det.getPrecioUnitario(), normalFont, Element.ALIGN_RIGHT));
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

    private PdfPCell alignedCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        return cell;
    }

    private static PdfPCell cellNoBorder(String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBorder(PdfPCell.NO_BORDER);
        c.setPadding(4);
        return c;
    }

    private ComprobanteDTO toDto(Comprobante c) {
        ComprobanteDTO dto = new ComprobanteDTO();
        dto.setId(c.getId());
        dto.setPedidoId(c.getPedido().getId());
        dto.setTipo(c.getTipo());
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
}