package utilities;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelManipulator {
		String excelFilePath=System.getProperty("user.dir")+ "/src/test/resources/Test Data/Input_Test_Data.xlsx";
		
	public Map<String,String> getDataFromExcel() throws Exception{
		XSSFWorkbook xlb=new XSSFWorkbook(new FileInputStream(excelFilePath));
		Map<String, String> inputData=new HashMap<String,String>(); 
		XSSFSheet xlsht=xlb.getSheet("Input_for_Signup");
		XSSFRow sourceRow = xlsht.getRow(xlsht.getLastRowNum());
		inputData.put("dname",sourceRow.getCell(0).toString());
		inputData.put("pname",sourceRow.getCell(1).toString());
		inputData.put("paytype",sourceRow.getCell(2).toString());
		inputData.put("env",sourceRow.getCell(3).toString());
		return inputData;
	}	
	 
	public void setDataIntoExcel(Map<String,String> outputData) throws Exception{		
		XSSFWorkbook xlb=new XSSFWorkbook(new FileInputStream(excelFilePath));
		XSSFSheet xlsht=xlb.getSheet("Input_for_Signup");		
		XSSFRow sourceRow = xlsht.getRow(xlsht.getLastRowNum());
		sourceRow.createCell(4).setCellValue(outputData.get("mid"));
		sourceRow.createCell(5).setCellValue(outputData.get("pwd"));
		sourceRow.createCell(6).setCellValue(outputData.get("mdn"));
		sourceRow.createCell(7).setCellValue(outputData.get("date"));
		FileOutputStream fos=new FileOutputStream(excelFilePath);
		xlb.write(fos);
		xlb.close();
	}	
}
