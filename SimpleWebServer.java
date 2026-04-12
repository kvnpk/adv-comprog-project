import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleWebServer {

    public static void main(String[] args) throws IOException {
        // 1. Start the server on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 2. Route for the HTML page (Frontend)
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Read the index.html file and send it to the browser
                File htmlFile = new File("index.html");
                if (htmlFile.exists()) {
                    byte[] response = Files.readAllBytes(Paths.get("index.html"));
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                } else {
                    String error = "index.html not found! Make sure it's in your project folder.";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.getResponseBody().close();
                }
            }
        });

        // 3. Route to GET students (Called by JS when the page loads)
        server.createContext("/api/getStudents", exchange -> {
            List<Student> students = ExcelDataManager.loadStudentsFromExcel();
            
            // Manually build a simple JSON string since we aren't using external libraries
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                json.append(String.format("{\"name\":\"%s\", \"id\":\"%s\"}", s.getName(), s.getId()));
                if (i < students.size() - 1) json.append(",");
            }
            json.append("]");

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        });

        // 4. Route to CREATE a student (Called by JS when clicking "Create User")
        server.createContext("/api/createStudent", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the incoming form data
                InputStream is = exchange.getRequestBody();
                String formData = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseFormData(formData);

                // Use the Builder Pattern!
                Student newStudent = new Student.StudentBuilder(params.get("name"), params.get("id"))
                        .setMajor(params.get("major"))
                        .setScholarship(Boolean.parseBoolean(params.get("scholarship")))
                        .build();

                // Save to Excel
                List<Student> allStudents = ExcelDataManager.loadStudentsFromExcel();
                allStudents.add(newStudent);
                ExcelDataManager.saveStudentsToExcel(allStudents);

                String response = "Student saved successfully!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("Server is running! Open your browser and go to: http://localhost:8080");
    }

    // Helper method to parse "name=John&id=123" into a Map
    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                map.put(
                    URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8), 
                    URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                );
            } else {
                map.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8), "");
            }
        }
        return map;
    }
}