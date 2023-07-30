package com.debuggeando_ideas.best_travel.infraestructure.services;

import com.debuggeando_ideas.best_travel.domain.entities.CustomerEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class ExcelService implements ReportService {
    private final CustomerRepository customerRepository;

    // regresamo  nuestro archivo como un arreglo de bytes
    @Override
    public byte[] readFile() {
        // Leeremos el archivo y lo convertimos en un arreglo de bytes
        try {
            this.createReport();
            // toAbsolutePath() : Convierte el código previo de un "path relativo" a "path absoluto".
            var path = Paths.get(REPORTS_PATH, String.format(FILE_NAME, LocalDate.now().getMonth())).toAbsolutePath();
            // retornamos el archivo de la ruta indicada convertido en un arreglo de bytes
            return Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    private void createReport() {
        // La variable "workBook" representa el archivo excel
        //  new XSSFWorkbook() : Representa mi schema excel
        var workBook = new XSSFWorkbook();
        // ------- 1) Creación de una hoja de excel -------
        // creamos una hoja de excel indicando el nombre de la hoja.
        var sheet = workBook.createSheet(SHEET_NAME);

        // ------- 2) Configuración de columnas -------
        // configuramos las 3 primeras columnas de la hoja creada recientemente, asignándoles el ancho.
        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 5000);

        // ------- 3) Estilo de la primera fila (cabecera) -------
        // Creamos una fila y su estilo
        var header = sheet.createRow(0);
        // creamos el estilo para esta fila (header)
        var headerStyle = workBook.createCellStyle();
        // indicamos que el color de fondo del header será "violeta"
        headerStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        // indicamos que el patrón de relleno será "solido"
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ------- 4) Fuente de la primera fila (cabecera) -------
        // creamos el tipo de fuente
        var font = workBook.createFont();
        // indicamos que el tipo de fuente será "arial"
        font.setFontName(FONT_TYPE);
        // indicamos el tamaño de la letra
        font.setFontHeightInPoints((short) 16);
        // indicamos que estará en "negrita"
        font.setBold(true);
        // agregamos las configuraiones del "font" al "header"
        headerStyle.setFont(font);

        // ------- 5) Contenido de la primera fila (cabecera) -------
        // creamos las 3 celdas estáticos. Las dinámicas serán las filas generadas con data de la base de datos.
        var headerCell = header.createCell(0);
        headerCell.setCellValue(COLUMN_CUSTOMER_ID);
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue(COLUMN_CUSTOMER_NAME);
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue(COLUMN_CUSTOMER_PURCHASES);
        headerCell.setCellStyle(headerStyle);

        // creamos un estilo
        var style = workBook.createCellStyle();
        // indicamos que el texto tenga un "wrap"
        style.setWrapText(true);

        // ------- 6) Impresión de los datos de las demás filas -------
        // creamos el repositorio
        var customers = this.customerRepository.findAll();
        // inicializamos el contador para la iteración de los datos.
        // Iniciamos en "1", ya que la fila "0" la está ocupando el "header"
        var rowPos = 1;

        for (CustomerEntity customer : customers) {
            var row = sheet.createRow(rowPos);

            var cell = row.createCell(0);
            cell.setCellValue(customer.getDni()); // vamos asignanado a la celda cada valor de la vase de datos
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(customer.getFullName()); // vamos asignanado a la celda cada valor de la vase de datos
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(getTtotalPruchase(customer)); // vamos asignanado a la celda cada valor de la vase de datos
            cell.setCellStyle(style);
            // aumentamos el contador de cada fila
            rowPos++;
        }

        // ------- 7) Creación del archivo excel -------
        // Indicamos donde voy a encontrar el archivo y el nombre que le daré.
        // El archivo se llamará "reports/Sales-%s" reemplazando el "%s" por el segundo parámetro del "String.format()"
        var report = new File(String.format(REPORTS_PATH_WITH_NAME, LocalDate.now().getMonth()));
        // traemos a la "ruta absoluta"(relativo a la computadora, el relativo proviene de ser relativo al proyecto)
        var path = report.getAbsolutePath();
        // añadimos la extensión de excel al archivo
        var fileLocation = path + FILE_TYPE;
        // indicamos la salida del archivo en el try y dentro del "FileOutputStream()" desde dónde empezará a leer el archivo.
        try (var outputStream = new FileOutputStream(fileLocation)) {
            // creamos el archivo y guardamos el archivo en la ruta dada.
            workBook.write(outputStream);
            workBook.close();
        } catch (IOException ex) {
            log.error("Can't creat Excel", ex);
            throw new RuntimeException();
        }
    }

    // Método que obtiene el total de compras(reservation, ticket, tour) del cliente dado
    private static int getTtotalPruchase(CustomerEntity customer) {
        return customer.getTotalLodgings() + customer.getTotalFlights() + customer.getTotalTours();
    }

    // Constantes para configurar el diseño de nuestra hoja excel
    private static final String SHEET_NAME = "Customer total sales";
    private static final String FONT_TYPE = "Arial";
    private static final String COLUMN_CUSTOMER_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "name";
    private static final String COLUMN_CUSTOMER_PURCHASES = "purchases";
    // El "%s" será reemplazado por una cadena de texto indcado en el segundo parámetro del método "String.format()"
    private static final String REPORTS_PATH_WITH_NAME = "reports/Sales-%s";
    private static final String REPORTS_PATH = "reports";
    private static final String FILE_TYPE = ".xlsx";
    private static final String FILE_NAME = "Sales-%s.xlsx";
}
