# 📄 Apache POI - Implementación en Como en Casa

## 📋 Descripción General

Apache POI es una biblioteca Java que permite crear, modificar y mostrar documentos de Microsoft Office. En el proyecto "Como en Casa", se utiliza principalmente para generar archivos Excel (.xlsx) con reportes de ventas, comprobantes y datos de pedidos de forma profesional y automatizada.

## 🎯 ¿Qué es Apache POI?

Apache POI es una biblioteca de código abierto que permite a los desarrolladores Java trabajar con documentos de Microsoft Office sin necesidad de tener instalado Microsoft Office en el servidor.

### 💼 Beneficios en el Proyecto

- **📊 Reportes Automatizados**: Generación automática de reportes de ventas y comprobantes
- **💼 Formato Profesional**: Documentos Excel con estilos corporativos y estructura clara
- **🔄 Integración Completa**: Exportación directa desde la base de datos al formato Excel
- **📈 Análisis de Datos**: Facilita el análisis de datos por parte de los administradores

## 🔄 Flujo de Información con Apache POI

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Base de Datos │────│  Servicio Java  │────│   Archivo Excel │
│                 │    │                 │    │                 │
│ • Pedidos       │    │ • Consulta datos│    │ • Reporte final │
│ • Comprobantes  │    │ • Aplica estilos│    │ • Listo para    │
│ • Productos     │    │ • Genera Excel  │    │   descargar     │
│ • Usuarios      │    │ • Formatea datos│    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🏗️ Arquitectura en el Proyecto

```
Frontend (React)
       ↓
   Controllers
       ↓
   Services ← → Apache POI
       ↓
   Repository
       ↓
   Base de Datos
```

## 🛠️ Características Principales

### 1. 📊 Generación de Archivos Excel

- **Formato XLSX**: Utiliza XSSFWorkbook para archivos Excel modernos
- **Múltiples hojas**: Soporte para workbooks con múltiples worksheets
- **Estilos personalizados**: Aplicación de formato, colores y estilos corporativos
- **Fórmulas**: Soporte para fórmulas de Excel y cálculos automáticos

### 2. 📋 Manipulación de Celdas

- **Tipos de datos**: String, numérico, fecha, booleano
- **Formato de celdas**: Moneda (S/), fechas (dd/MM/yyyy), números con separadores
- **Combinación de celdas**: Merge de celdas para títulos y encabezados
- **Alineación**: Configuración de alineación horizontal y vertical

## 💻 Implementación en el Proyecto

### 📦 Dependencias Maven

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.3</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.3</version>
</dependency>
```

### 🔧 Configuración Principal

La implementación se encuentra en `ComprobanteServiceImpl.java`:

```java
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class ComprobanteServiceImpl implements ComprobanteService {

    @Override
    public ByteArrayInputStream generarExcel(Long comprobanteId) {
        Comprobante c = comprobanteRepo.findById(comprobanteId)
            .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado"));

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Comprobante");

            // Crear estilos
            CellStyle titleStyle = createTitleStyle(wb);
            CellStyle headerStyle = createHeaderStyle(wb);
            CellStyle dateStyle = createDateStyle(wb);
            CellStyle moneyStyle = createMoneyStyle(wb);

            // Agregar contenido
            addTitle(sheet, titleStyle);
            addHeaders(sheet, headerStyle);
            addData(sheet, c, dateStyle, moneyStyle);

            // Autosize columns
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }
}
```

### 🎨 Creación de Estilos

```java
private CellStyle createTitleStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 16);
    font.setBold(true);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    return style;
}

private CellStyle createHeaderStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    Font font = wb.createFont();
    font.setFontName("Arial");
    font.setFontHeightInPoints((short) 12);
    font.setBold(true);
    style.setFont(font);
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return style;
}

private CellStyle createMoneyStyle(Workbook wb) {
    CellStyle style = wb.createCellStyle();
    CreationHelper createHelper = wb.getCreationHelper();
    style.setDataFormat(createHelper.createDataFormat().getFormat("\"S/ \"#,##0.00"));
    style.setAlignment(HorizontalAlignment.RIGHT);
    return style;
}
```

### 📊 Generación de Reportes Específicos

#### 1. Reporte de Comprobantes Individuales

```java
@Override
public ByteArrayInputStream generarExcel(Long comprobanteId) {
    // Obtener datos del comprobante
    Comprobante c = comprobanteRepo.findById(comprobanteId)
        .orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado"));

    try (Workbook wb = new XSSFWorkbook()) {
        Sheet sheet = wb.createSheet("Comprobante");

        // Título del documento
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("COMPROBANTE DE PAGO");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        // Encabezados de datos
        String[] headers = {
            "Cliente", "Documento", "Correo", "Fecha Emisión",
            "Tipo", "Serie", "Número", "Subtotal", "Total"
        };

        Row headerRow = sheet.createRow(2);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos del comprobante
        Row dataRow = sheet.createRow(3);
        populateComprobanteData(dataRow, c, dateStyle, moneyStyle);

        return generateExcelOutput(wb);
    }
}
```

#### 2. Reporte de Ventas General

```java
@Override
public ByteArrayInputStream generarReporteVentasExcel(
    Optional<LocalDateTime> desde, Optional<LocalDateTime> hasta) {

    List<PedidoDTO> pedidos = desde.isPresent() && hasta.isPresent()
        ? pedidoService.findByFechaCreacionBetween(desde.get(), hasta.get())
        : pedidoService.findAll();

    try (Workbook wb = new XSSFWorkbook()) {
        Sheet sheet = wb.createSheet("Reporte de Ventas");

        // Título con período
        addReportTitle(sheet, "REPORTE DE VENTAS", desde, hasta);

        // Encabezados
        String[] headers = {
            "ID Pedido", "Cliente", "Fecha", "Estado",
            "Productos", "Subtotal", "Envío", "Total"
        };

        addHeaders(sheet, headers, 2);

        // Datos de pedidos
        int rowNum = 3;
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (PedidoDTO pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            populatePedidoData(row, pedido);
            totalGeneral = totalGeneral.add(pedido.getCostoTotal());
        }

        // Fila de totales
        addTotalRow(sheet, rowNum, totalGeneral);

        return generateExcelOutput(wb);
    }
}
```

### 🧪 Testing con Apache POI

```java
@Test
void testGenerarExcel() throws IOException {
    // Given
    Long comprobanteId = 1L;
    when(comprobanteRepository.findById(comprobanteId))
        .thenReturn(Optional.of(comprobanteTest));

    // When
    ByteArrayInputStream result = comprobanteService.generarExcel(comprobanteId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.available()).isGreaterThan(0);

    // Verificar que es un archivo Excel válido
    try (Workbook wb = new XSSFWorkbook(result)) {
        Sheet sheet = wb.getSheetAt(0);
        assertThat(sheet.getSheetName()).isEqualTo("Comprobante");
        assertThat(sheet.getRow(0).getCell(0).getStringCellValue())
            .isEqualTo("COMPROBANTE DE PAGO");
    }
}
```

## 📈 Casos de Uso Implementados

### 1. 📋 Comprobantes Individuales

- **Finalidad**: Generar comprobante específico para un pedido
- **Formato**: Excel con datos del cliente, productos y totales
- **Estilos**: Formato corporativo con colores y fuentes específicas

### 2. 📊 Reportes de Ventas

- **Finalidad**: Análisis de ventas por período
- **Filtros**: Rango de fechas personalizable
- **Métricas**: Totales generales y por período

### 3. 📄 Reportes de Comprobantes

- **Finalidad**: Listado completo de comprobantes emitidos
- **Filtros**: Por fecha, tipo, cliente
- **Datos**: Información completa de facturación

## 🔧 Utilidades y Helpers

### 🎨 Gestión de Estilos

```java
public class ExcelStyleHelper {

    public static CellStyle createCurrencyStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        CreationHelper helper = wb.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("\"S/ \"#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    public static CellStyle createDateStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        CreationHelper helper = wb.getCreationHelper();
        style.setDataFormat(helper.createDataFormat().getFormat("dd/MM/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    public static CellStyle createHeaderStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
```

### 📊 Manejo de Datos

```java
private void populateComprobanteData(Row row, Comprobante c,
                                   CellStyle dateStyle, CellStyle moneyStyle) {
    // Cliente
    String cliente = c.getPedido().getUsuario().getNombre() + " " +
                    c.getPedido().getUsuario().getApellido();
    row.createCell(0).setCellValue(cliente);

    // Documento
    row.createCell(1).setCellValue(
        Optional.ofNullable(c.getPedido().getUsuario().getNumeroDocumento())
               .orElse("-"));

    // Email
    row.createCell(2).setCellValue(c.getPedido().getUsuario().getEmail());

    // Fecha
    Cell dateCell = row.createCell(3);
    dateCell.setCellValue(Date.from(c.getFechaEmision()
        .atZone(ZoneId.systemDefault()).toInstant()));
    dateCell.setCellStyle(dateStyle);

    // Tipo y números
    row.createCell(4).setCellValue(c.getTipo().name());
    row.createCell(5).setCellValue(c.getNumeroSerie());
    row.createCell(6).setCellValue(c.getNumeroComprobante());

    // Montos
    Cell subtotalCell = row.createCell(7);
    subtotalCell.setCellValue(c.getSubtotal().doubleValue());
    subtotalCell.setCellStyle(moneyStyle);

    Cell totalCell = row.createCell(8);
    totalCell.setCellValue(c.getTotal().doubleValue());
    totalCell.setCellStyle(moneyStyle);
}
```

## 🎯 Mejores Prácticas Implementadas

### 1. 🔒 Gestión de Recursos

- **Try-with-resources**: Cierre automático de workbooks
- **ByteArrayOutputStream**: Manejo eficiente de memoria
- **Cleanup**: Liberación de recursos al finalizar

### 2. 🎨 Consistencia Visual

- **Estilos reutilizables**: Creación de estilos una vez y reutilización
- **Colores corporativos**: Paleta de colores consistente
- **Fuentes estándar**: Arial para todos los documentos

### 3. 📊 Rendimiento

- **Autosize columns**: Ajuste automático de ancho de columnas
- **Streaming**: Procesamiento eficiente de grandes volúmenes de datos
- **Caching**: Reutilización de estilos y formatos

### 4. 🛡️ Manejo de Errores

- **Validación de entrada**: Verificación de datos antes de procesamiento
- **Excepciones específicas**: Manejo diferenciado de errores POI
- **Logging**: Registro de operaciones para troubleshooting

## 🔧 Troubleshooting Común

### 1. 📁 Archivos Corruptos

```java
// Verificar integridad del archivo
try (Workbook wb = new XSSFWorkbook(inputStream)) {
    // Archivo válido
} catch (IOException e) {
    log.error("Archivo Excel corrupto: {}", e.getMessage());
    throw new RuntimeException("Error en archivo Excel", e);
}
```

### 2. 💾 Memoria Insuficiente

```java
// Para archivos grandes, usar SXSSFWorkbook
try (SXSSFWorkbook wb = new SXSSFWorkbook(1000)) { // Keep 1000 rows in memory
    // Procesar datos
    wb.dispose(); // Limpiar archivos temporales
}
```

### 3. 🎨 Estilos Duplicados

```java
// Reutilizar estilos en lugar de crear nuevos
private final Map<String, CellStyle> styleCache = new HashMap<>();

private CellStyle getCachedStyle(Workbook wb, String styleKey) {
    return styleCache.computeIfAbsent(styleKey, k -> createStyle(wb, k));
}
```

## 📈 Beneficios Alcanzados

### 1. 📊 Reportes Profesionales

- **Formato empresarial**: Documentos con apariencia profesional
- **Datos estructurados**: Información organizada y fácil de leer
- **Compatibilidad**: Archivos compatibles con Excel y LibreOffice

### 2. 🔄 Automatización

- **Generación automática**: Sin intervención manual
- **Datos actualizados**: Información siempre actual desde la base de datos
- **Múltiples formatos**: Soporte para diferentes tipos de reportes

### 3. 💼 Valor Empresarial

- **Toma de decisiones**: Información precisa para análisis
- **Ahorro de tiempo**: Generación instantánea de reportes
- **Escalabilidad**: Manejo de grandes volúmenes de datos

## 🔮 Roadmap Futuro

### 1. 📈 Características Avanzadas

- **Gráficos integrados**: Charts y visualizaciones en Excel
- **Macros**: Automatización avanzada con VBA
- **Plantillas**: Templates reutilizables para diferentes reportes

### 2. 🎯 Optimizaciones

- **Streaming**: Procesamiento de archivos muy grandes
- **Compresión**: Reducción del tamaño de archivos
- **Paralelización**: Generación concurrente de múltiples reportes

### 3. 🔗 Integraciones

- **Google Sheets**: Exportación directa a Google Sheets
- **Power BI**: Conectores para análisis avanzado
- **PDF**: Conversión automática Excel a PDF

---

## 📚 Recursos Adicionales

- **[Apache POI Documentation](https://poi.apache.org/)**
- **[XSSF Examples](https://poi.apache.org/components/spreadsheet/examples.html)**
- **[POI Best Practices](https://poi.apache.org/components/spreadsheet/how-to.html)**

---

_Esta documentación forma parte del proyecto "Como en Casa" - Sistema Web para Gestión de Pedidos y Clientes_
