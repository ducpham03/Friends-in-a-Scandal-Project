# Coursework Project: Friends in a Scandal Java Project

The Enron dataset is a large collection of email messages and other documents that were collected during the bankruptcy of Enron Corporation. The dataset is a valuable resource for researchers who are interested in studying organizational behavior, corporate fraud, and other social phenomena.

One of the key questions that researchers have asked about the Enron dataset is how information flows within organizations. Connectors are individuals who play a critical role in information flow by connecting different groups of people. Identifying connectors can help us to understand how information spreads within organizations and how it can be manipulated.

______________________

**Methods**

This coursework project aims to identify connectors in the Enron dataset using a depth-first search (DFS) algorithm. The following steps were taken:

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

- Pattern, Matcher, ReentrantLock, ExecutorService, TimeUnit, Executors, BufferedReader, Reading DirectoryStream.

**Running time:**
- O(n), where n is the total number of mail files in the mail directory.
- The program runs through all directory paths to filter valid mail and then process data in valid mail files.

______________________
**Conclusion**

This coursework project has demonstrated the effectiveness of DFS algorithms for identifying connectors in the Enron dataset. The list of connectors identified in this study can be used by researchers to further study information flow within organizations and how it can be manipulated.
