import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Friends in a Scandal Java Project
 * Duc Pham
 * May 14, 2023 
 */


/**
 * Friendship Graph Class
 */ 
class Graph {
    
    private final Set<String> vertices = new HashSet<>();
    private final Map<String, Set<String>> sent = new HashMap<>();  
    private final Map<String, Set<String>> received = new HashMap<>(); 
    private final Set<String> connectors = new HashSet<>(); 
    private final List<Set<String>> teams = new ArrayList<>();   
    private int counter = 0;
    
    public Graph() {
    }

    /**
     * Add vertex to graph
     * @param vertex 
     */
    public void addVertex(String vertex) {
        vertices.add(vertex);
    }

    /**
     * Add edge to graph
     * @param from - src vertex
     * @param to - dst vertex
     */
    public void addEdge(String from, String to) {
        addVertex(from);
        addVertex(to);
        
        if (!sent.containsKey(from)) {
            sent.put(from, new HashSet<>());
        }
        sent.get(from).add(to);
       
        if (!received.containsKey(to)) {
            received.put(to, new HashSet<>());
        }
        received.get(to).add(from);
    }

    /**
     * Check if the graph has vertex
     * @param vertex 
     * @return true if has vertex, else return false
     */
    public boolean hasVertex(String vertex) {
        return vertices.contains(vertex);
    }

    /**
     * Get connectors
     * @return connectors
     */
    public Set<String> getConnectors() {
        return connectors;
    }

    /**
     * Calculate connectors and teams
     */
    public void calculate() {
       
        Set<String> visited = new HashSet<>();
        
        Map<String, Integer> dfs = new HashMap<>();
        
        Map<String, Integer> back = new HashMap<>();

        for (String vertex : vertices) {
            if (visited.contains(vertex)) {
                continue;
            }
            Set<String> team = new HashSet<>();
            DFS(vertex, visited, dfs, back, team);
            teams.add(team);
        }
    }

    /**
     * sentCount function:
     * Get the number of unique email addresses to whom the individual sent messages
     * 
     * @param vertex
     * @return the number of email addresses
     */
    public int sentCount(String vertex) {
        if (!sent.containsKey(vertex)) {
            return 0;
        }
        return sent.get(vertex).size();
    }

    /**
     * receivedCount function:
     * Get the number of unique email addresses from whom the individual received messages
     * 
     * @param vertex 
     * @return the number of email addresses
     */
    public int receivedCount(String vertex) {
        if (!received.containsKey(vertex)) {
            return 0;
        }
        return received.get(vertex).size();
    }

    /**
     * teamCount function:
     * Get the number of email addresses in the same team as the individual
     * @param vertex the vertex
     * @return the team member number
     */
    public int teamCount(String vertex) { 
        for (Set<String> team : teams) {
            if (team.contains(vertex)) {
                
                return team.size();
            }
        }
        return 0;
    }

    /**
     * Using DFS
     * @param root 
     * @param visited 
     * @param dfs 
     * @param back 
     * @param team 
     */
    private void DFS(String root, Set<String> visited, Map<String, Integer> dfs, Map<String, Integer> back, Set<String> team) {                  
        int children = 0;
        Stack<String> stack = new Stack<>();
        Map<String, String> parents = new HashMap<>();
        parents.put(root, null);
        stack.push(root);

        while (!stack.isEmpty()) {
            String u = stack.peek();

            if (!visited.contains(u)) {
                int n = ++counter;
                dfs.put(u, n);
                back.put(u, n);

                visited.add(u);

                team.add(u);
            }

            Set<String> neighbors = new HashSet<>();
            if (sent.containsKey(u)) {
                neighbors.addAll(sent.get(u));
            }

            if (received.containsKey(u)) {
                neighbors.addAll(received.get(u));
            }

            int processed = 0;
            for (String v : neighbors) {
                if (visited.contains(v)) {
                    continue;
                }
                ++processed;
                stack.push(v);
                parents.put(v, u);    
                break;
            }

            if (processed == 0) {
                stack.pop();
                String parent = parents.get(u);
                if (parent != null) {
                    int n = Math.min(back.get(parent), dfs.get(u));
                    back.put(parent, n);
                    if (parent.equals(root)) {
                        ++children;
                    }
                }
                for (String v : neighbors) {
                    if (v.equals(parent)) {
                        continue;
                    }
                    int n = Math.min(back.get(u), back.get(v));
                    back.put(u, n);
                }

                if (parent != null) { 
                    for (String v : neighbors) {
                        if (!u.equals(parents.get(v))) {
                            continue;
                        }
                        if (dfs.get(u) <= back.get(v)) {
                            connectors.add(u);
                            break;
                        }
                    }
                } 
                else { 
                    if (children > 1) {
                        connectors.add(u);
                    }
                }
            }
        }
    }
}

public class A3 {

    public static final String EMAILS_REGEX = "(?i)((From|To|Cc|Bcc):\\s*\\b[A-Za-z0-9._%+-]+" +
            "@[A-Za-z0-9.-]+\\.[A-Z]{2,}\\b|\\b[A-Za-z0-9._%+-]+@enron\\.com\\b)";
    public static final Pattern EMAILS_PATTERN = Pattern.compile(EMAILS_REGEX);
    public static final String EMAIL_REGEX = "\\b[A-Za-z0-9._%+-]+@enron\\.com\\b";
    public static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static int processedFileCount = 0;

    /**
     * readData function:
     * read the data file
     *
     * @param graph - friendship graph
     * @param root - mail directory
     */
    private static void readData(Graph graph, Path root) {
        
        final Queue<Path> queue = new LinkedList<>();
        queue.add(root);
        ReentrantLock mutex = new ReentrantLock();
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        while (!queue.isEmpty()) {
            Path dirPath = queue.poll();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        queue.add(path);
                        continue;
                    }

                    if (Files.isRegularFile(path)) {
                        Runnable task = () -> readFile(graph, path, mutex);
                        pool.execute(task);
                    }
                }
            } catch (IOException e) {
                System.err.println("Process '" + dirPath + "' failed: " + e.getMessage());
            }
        }
        pool.shutdown();
        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * readfile Function:
     * read data and represent the messages in the dataset as a friendship graph
     *
     * @param graph - friendship graph
     * @param filePath
     */
    private static void readFile(Graph graph, Path filePath, ReentrantLock mutex) {
        try {
            mutex.lock();
            ++processedFileCount;
            mutex.unlock();
            String from = null;
            String content = Files.readString(filePath, StandardCharsets.ISO_8859_1);
            Matcher matcher = EMAILS_PATTERN.matcher(content);
            
            while (matcher.find()) {
                String str = matcher.group();
                
                Matcher emailMatcher = EMAIL_PATTERN.matcher(str);
                if (!emailMatcher.find()) {
                    continue;
                }
                String email = emailMatcher.group();

                if (from == null && str.startsWith("From:")) {
                    from = email;
                    continue;
                }
                
                if (from == null) {
                    continue;
                }
                mutex.lock();
                graph.addEdge(from, email);
                mutex.unlock();
            }
        } catch (IOException e) {
            System.err.println("Read '" + filePath + "' failed: " + e.getMessage());
        }
    }

    /**
     * showConnectors function:
     * Output the connectors to the stdout
     * 
     * @param graph - friendship graph
     * @param filePath - file to print the output 
     */
    private static void showConnectors(Graph graph, Path filePath) {
    
        Set<String> connectors = graph.getConnectors();

        for (String connector : connectors) {
            System.out.println(connector);
        }
        System.out.println();
        
        if (filePath == null) {
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (String connector : connectors) {
                writer.write(connector);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /**
     * process function: 
     * Provide details of each person and output contents
     *
     * @param graph - friendship graph
     */
    private static void process(Graph graph) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("Email address of the individual (or EXIT to quit): ");
                String input = reader.readLine();

                if (input == null) {
                    break;
                }
                
                input = input.trim();
                if (input.equalsIgnoreCase("EXIT")) {
                    break;
                }

                if (!graph.hasVertex(input)) {
                    System.out.println("Email address (" + input + ") not found in the dataset.");
                    continue;
                }

                // Output
                System.out.println("* " + input + " has sent messages to " + graph.sentCount(input) + " others");
                System.out.println("* " + input + " has received messages from " + graph.receivedCount(input) + " others");
                System.out.println("* " + input + " is in a team with " + graph.teamCount(input) + " individuals");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR. Please enter mail directory.");
            return;
        }
    
        Path root = Paths.get(args[0]);
        if (!Files.exists(root) || !Files.isDirectory(root)) {
            System.err.println("Mail directory not file: " + args[0]);
            return;
        }
    
        Path filePath = null;
        if (args.length > 1) {
            filePath = Paths.get(args[1]);
        }
    
        Graph graph = new Graph();
        readData(graph, root);
        graph.calculate();
        showConnectors(graph, filePath);
        process(graph);
    }
}