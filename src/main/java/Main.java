import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCsv = "data.csv";
        String fileNameXml = "data.xml";
        String jsonFileNameCsv = "data_csv.json";
        String jsonFileNameXml = "data_xml.json";

        List<Employee> listCsv = parseCSV(columnMapping, fileNameCsv);
        String json = listToJson(listCsv);
        writeString(json, jsonFileNameCsv);

        List<Employee> listXml = parseXML(fileNameXml);
        String json1 = listToJson(listXml);
        writeString(json1, jsonFileNameXml);

        String fromFile = readString(jsonFileNameCsv);
        List<Employee> employeeList = jsonToList(fromFile);
        for(Employee employee : employeeList){
            System.out.println(employee);
        }
    }

    public static List<Employee> parseCSV(String[] column, String fileName) {
        List<Employee> employeeList;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(column);
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

    public static void writeString(String jsonString, String outFile) {
        try (FileWriter fr = new FileWriter(outFile)) {
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
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element employee = (Element) node;
                    NodeList nlId = employee.getElementsByTagName("id");
                    for (int j = 0; j < nlId.getLength(); j++) {
                        employeeList.add(new Employee(Long.parseLong(nlId.item(j).getTextContent()),
                                employee.getElementsByTagName("firstName").item(j).getTextContent(),
                                employee.getElementsByTagName("lastName").item(j).getTextContent(),
                                employee.getElementsByTagName("country").item(j).getTextContent(),
                                Integer.parseInt(employee.getElementsByTagName("age").item(j).getTextContent())));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        return employeeList;
    }

    public static String readString(String fileName){
        StringBuilder sb = new StringBuilder();
        String s;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    public static List<Employee> jsonToList(String stringFromFile){
        List<Employee> employees = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        try{
            Object obj = jsonParser.parse(stringFromFile);
            JsonElement jsonElement = gson.fromJson(stringFromFile, JsonElement.class);
            JsonArray array = jsonElement.getAsJsonArray();
            Iterator<JsonElement> iterator = array.iterator();
            while(iterator.hasNext()){
                JsonElement json2 = iterator.next();
                Employee employee = gson.fromJson(json2, Employee.class);
                employees.add(employee);
            }
        } catch (ParseException e) {
            System.out.println(e);
        }
        return employees;
    }
}
