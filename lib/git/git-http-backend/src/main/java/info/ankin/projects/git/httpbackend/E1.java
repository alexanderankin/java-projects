package info.ankin.projects.git.httpbackend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Builder
@Value
public class E1 {
    private static final ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private static final ExecutorService executor = new ThreadPoolExecutor(4, 8,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);

    @Builder.Default
    int port = 8080;

    @NonNull
    @Builder.Default
    String command = "cat";

    @NonNull
    @Builder.Default
    ExecutorService executorService = executor;

    @Builder.Default
    boolean asyncProcess = true;

    public static void main(String[] args) {
        // String ghb = new GitHttpBackend.Config().ghb();
        // System.out.println("starting");
        // System.out.println(ghb);
        // E1.builder().command(ghb).build().run();
        // System.out.println("started");
        String a[] = {""};
        String b[] = {"b"};
        String c[] = {"b", "c"};

        String array[] = c;
        System.out.println(Arrays.toString(Arrays.copyOfRange(array, 1, array.length)));
    }

    @SneakyThrows
    void run() {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        httpServer.createContext("/", httpExchange -> {
            try {
                // Create a new ProcessBuilder instance
                ProcessBuilder processBuilder = new ProcessBuilder();

                // Prepare the ProcessBuilder with headers from the HttpExchange
                prepareCgiProcessBuilder(httpExchange, processBuilder);

                // Set the input and output stream redirection options for the ProcessBuilder
                prepareCgiPbStreams(processBuilder);

                // Configure the ProcessBuilder to launch the CGI application
                processBuilder.command(command);

                // Launch the CGI application
                Process process = processBuilder.start();

                if (asyncProcess)
                    executeAsync(httpExchange, process);
                else
                    executeSynchronously(httpExchange, process);

            } catch (IOException | ExecutionException | InterruptedException e) {
                // Handle exceptions
                byte[] errorMessage = e.getMessage().getBytes();
                httpExchange.sendResponseHeaders(500, errorMessage.length);
                try (OutputStream responseBody = httpExchange.getResponseBody()) {
                    responseBody.write(errorMessage);
                }
            } finally {
                httpExchange.close();
            }
        });

        httpServer.start();
    }

    private void executeAsync(HttpExchange httpExchange, Process process) throws ExecutionException, InterruptedException {

        // Write the request body to the process's input stream
        Future<Void> inputTransfer = executor.submit(() -> {
            try (InputStream requestBody = httpExchange.getRequestBody();
                 OutputStream processInput = process.getOutputStream()) {
                requestBody.transferTo(processInput);
            }
            return null;
        });

        // Read the process's output stream and write it to the HttpExchange response
        Future<Void> outputTransfer = executor.submit(() -> {
            try (InputStream processOutput = process.getInputStream();
                 OutputStream responseBody = httpExchange.getResponseBody()) {
                httpExchange.sendResponseHeaders(200, 0);
                processOutput.transferTo(responseBody);
            }
            return null;
        });

        inputTransfer.get();
        outputTransfer.get();
    }

    private void executeSynchronously(HttpExchange httpExchange, Process process) throws IOException {
        // Write the request body to the process's input stream
        try (InputStream requestBody = httpExchange.getRequestBody();
             OutputStream processInput = process.getOutputStream()) {
            requestBody.transferTo(processInput);
        }

        // Read the process's output stream and write it to the HttpExchange response
        try (InputStream processOutput = process.getInputStream();
             OutputStream responseBody = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(200, 0);
            processOutput.transferTo(responseBody);
        }
    }

    public void prepareCgiProcessBuilder(HttpExchange httpExchange, ProcessBuilder processBuilder) throws IOException {
        // Get the headers from the HttpExchange object
        Map<String, List<String>> headers = httpExchange.getRequestHeaders();

        // Set the environment variables for the ProcessBuilder
        Map<String, String> environment = processBuilder.environment();

        // Add the headers from the HttpExchange object to the ProcessBuilder environment
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            String headerName = header.getKey();
            List<String> headerValues = header.getValue();
            String headerValue = String.join(", ", headerValues);

            // Convert the header name to the CGI variable format (e.g., "Content-Type" -> "HTTP_CONTENT_TYPE")
            String cgiHeaderName = "HTTP_" + headerName.toUpperCase().replace('-', '_');

            environment.put(cgiHeaderName, headerValue);
        }

        // Set other CGI environment variables, such as REQUEST_METHOD, CONTENT_LENGTH, and QUERY_STRING
        environment.put("REQUEST_METHOD", httpExchange.getRequestMethod());
        environment.put("CONTENT_LENGTH", String.valueOf(httpExchange.getRequestBody().available()));
        Optional.ofNullable(httpExchange.getRequestURI().getQuery())
                .ifPresent(query -> environment.put("QUERY_STRING", query));
    }


    public void prepareCgiPbStreams(ProcessBuilder processBuilder) {
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);
    }
}
