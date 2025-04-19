package org.example;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            try {
                File file = new File("public/index.html");
                byte[] response = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } catch (IOException e) {
                String response = "Erro ao carregar o arquivo: " + e.getMessage();
                exchange.sendResponseHeaders(500, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
            } finally {
                exchange.close();
            }
        });

        server.createContext("/api/local", exchange -> {
            try {
                InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("data.json");
                if (inputStream == null) {
                    throw new FileNotFoundException("Arquivo data.json não encontrado no classpath");
                }

                String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                byte[] response = jsonContent.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
            } catch (Exception e) {
                String response = "Erro ao processar o JSON: " + e.getMessage();
                System.err.println(response);
                exchange.sendResponseHeaders(500, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
            } finally {
                exchange.close();
                System.out.println(">>> Resposta finalizada");
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Servidor rodando na porta 8080");
    }
}