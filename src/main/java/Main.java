import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";

        List<Employee> listCsv = parseCSV(columnMapping, fileNameCsv);
        String json = listToJson(listCsv);
        writeString(json);

        List<Employee> listXml = parseXML(fileNameXml);
        String json1 = listToJson(listXml);
        writeString(json1);
    }

    public static List<Employee> parseCSV(String[] column, String fileName) {
        List<Employee> employeeList;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader).
                    withMappingStrategy(strategy).
                    build();
            employeeList = csv.parse();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return employeeList;
    }

    public static String listToJson(List<Employee> employeeList) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(employeeList, listType);
    }

    public static void writeString(String jsonString) {
        try (FileWriter fr = new FileWriter("data.json")) {
            fr.write(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> employeeList = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fileName);
            Node root = doc.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                short nType = node.getNodeType();
                if (Node.ELEMENT_NODE == nType) {
                    Element employee = (Element) node;
                    NamedNodeMap map = employee.getAttributes();
                    for(int j = 0; j < map.getLength(); j++){
                        String id = map.item(j).getNodeName();
                        String id1 = map.item(j).getNodeValue();
/*                        employeeList.add(new Employee(Long.getLong(id),
                                employee.getAttribute("firstName"),
                                employee.getAttribute("lastName"),
                                employee.getAttribute("country"),
                                Integer.getInteger(employee.getAttribute("age"))));*/
                    }

                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return employeeList;
    }
}
