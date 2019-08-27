package zgdx.xxaq.domain.read;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;

//@Service
public class XlsxReaderService implements InitializingBean {

    @Value("${xlsx.isRead}")
    private Boolean xlsxIsRead;

    @Value("${xlsx.filePath}")
    private String xlsxFilePath;

    private void readDomainXlsx(String xlsxFilePath) throws Exception {
        Workbook workbook = new XSSFWorkbook(xlsxFilePath);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()) {
            Iterator<Cell> cellIterator = rowIterator.next().cellIterator();
            while(cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                cell.getCellComment();
                cell.getStringCellValue();
                break;
            }
            break;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (xlsxIsRead) {
            readDomainXlsx(xlsxFilePath);
        }
    }
}
