# Friends-in-a-Scandal-Project

**Read the data file**

- Read the valid mail files in the Enron dataset.
- Filter valid mail files and unique email addresses representing an individual.
- Represent the messages in the dataset as a friendship graph: the vertices in a friendship graph represent people.

**Identify and print connectors**

- Identify connectors
- Using DFS by keeping track of two additional quantities for every vertex v.
- After finishing identifying connectors, print all the connectors out to the screen and file if it is provided.

**Provide details of each person**

- Respond to user questions about individual email addresses: 
  - The number of unique email addresses to whom the individual sent messages.
  - The number of unique email addresses from whom the individual received messages.
  - The number of email addresses in the same “team” as the individual.

______________________

**Data Structure used:**

- Graph, Queue, Map, Set, List.

**Algorithm used:**

- Depth-first search algorithm: keeping track of two additional quantities for every vertex v dfsnum(v) and back(v).

**Others:**

- Pattern, Matcher, ReentrantLock, ExecutorService, TimeUnit, Executors, BufferedReader.
- Reading DirectoryStream https://www.baeldung.com/java-list-directory-files.

**Running time:**
- O(n), where n is the total number of mail files in the mail directory.
- The program runs through all directory paths to filter valid mail and then process data in valid mail files.
