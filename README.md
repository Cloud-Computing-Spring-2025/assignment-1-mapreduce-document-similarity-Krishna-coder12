[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-2e0aaae1b6195c2367325f4f02e2d04e9abb55f0b24a779b69b11b9e10269abc.svg)](https://classroom.github.com/online_ide?assignment_repo_id=18214763&assignment_repo_type=AssignmentRepo)
### **📌 Document Similarity Using Hadoop MapReduce**  

#### **Objective**  
The goal of this assignment is to compute the **Jaccard Similarity** between pairs of documents using **MapReduce in Hadoop**. You will implement a MapReduce job that:  
1. Extracts words from multiple text documents.  
2. Identifies which words appear in multiple documents.  
3. Computes the **Jaccard Similarity** between document pairs.  
4. Outputs document pairs with similarity **above 50%**.  

---

### **📥 Example Input**  

You will be given multiple text documents. Each document will contain several words. Your task is to compute the **Jaccard Similarity** between all pairs of documents based on the set of words they contain.  

#### **Example Documents**  

##### **doc1.txt**  
```
doc1 hadoop is a distributed system
```

##### **doc2.txt**  
```
doc2 hadoop is used for big data system processing.
```

##### **doc3.txt**  
```
doc3 hadoop is a big data system is important for analysis
```

---

# 📏 Jaccard Similarity Calculator

## Overview

The Jaccard Similarity is a statistic used to gauge the similarity and diversity of sample sets. It is defined as the size of the intersection divided by the size of the union of two sets.

## Formula

The Jaccard Similarity between two sets A and B is calculated as:

```
Jaccard Similarity = |A ∩ B| / |A ∪ B|
```

Where:
- `|A ∩ B|` is the number of words common to both documents
- `|A ∪ B|` is the total number of unique words in both documents

## Example Calculation

Consider two documents:
 
**doc2.txt words**: `{hadoop, is, used, for, big, data, system, processing}`
**doc3.txt words**: `{hadoop, is, a, big, data, system, is, important, for, analysis}`

- Common words: `{hadoop, is, big, data, system, for}`
- Total unique words: `{hadoop, is, used, for, big, data, system, processing, a, important, analysis}`

Jaccard Similarity calculation:
```
|A ∩ B| = 6 (common words)
|A ∪ B| = 11 (total unique words)

Jaccard Similarity = 6/11 = 0.54 or 54%
```

### **📤 Expected Output**  

The output should show the Jaccard Similarity between document pairs in the following format:  
```
(doc2, doc3)	54% 
```

---

### **🛠 Environment Setup: Running Hadoop in Docker**  

Since we are using **Docker Compose** to run a Hadoop cluster, follow these steps to set up your environment.  

#### **Step 1: Install Docker & Docker Compose**  
- **Windows**: Install **Docker Desktop** and enable WSL 2 backend.  
- **macOS/Linux**: Install Docker using the official guide: [Docker Installation](https://docs.docker.com/get-docker/)  

#### **Step 2: Start the Hadoop Cluster**  
Navigate to the project directory where `docker-compose.yml` is located and run:  
```sh
docker-compose up -d
```  
This will start the Hadoop NameNode, DataNode, and ResourceManager services.  

#### **Step 3: Access the Hadoop Container**  
Once the cluster is running, enter the **Hadoop master node** container:  
```sh
docker exec -it hadoop-master /bin/bash
```

---

### **📦 Building and Running the MapReduce Job with Maven**  

#### **Step 1: Build the JAR File**  
Ensure Maven is installed, then navigate to your project folder and run:  
```sh
mvn clean package
```  
This will generate a JAR file inside the `target` directory.  

#### **Step 2: Copy the JAR File to the Hadoop Container**  
Move the compiled JAR into the running Hadoop container:  
```sh
docker cp target/similarity.jar hadoop-master:/opt/hadoop-3.2.1/share/hadoop/mapreduce/similarity.jar
```

---

### **📂 Uploading Data to HDFS**  

#### **Step 1: Create an Input Directory in HDFS**  
Inside the Hadoop container, create the directory where input files will be stored:  
```sh
hdfs dfs -mkdir -p /input
```

#### **Step 2: Upload Dataset to HDFS**  
Copy your local dataset into the Hadoop cluster’s HDFS:  
```sh
hdfs dfs -put /path/to/local/input/* /input/
```

---

### **🚀 Running the MapReduce Job**  

Run the Hadoop job using the JAR file inside the container:  
```sh
hadoop jar similarity.jar DocumentSimilarityDriver /input /output_similarity /output_final
```

---

### **📊 Retrieving the Output**  

To view the results stored in HDFS:  
```sh
hdfs dfs -cat /output_final/part-r-00000
```

If you want to download the output to your local machine:  
```sh
hdfs dfs -get /output_final /path/to/local/output
```
---


## 🚧 Challenges Faced & Solutions

### 1. Controller Error in the Hadoop Cluster
- Problem: The MapReduce job failed due to an issue with the controller in the Hadoop setup.
- Solution: Restarted the entire Hadoop cluster by executing docker-compose down and then docker-compose up -d, ensuring all services were correctly initialized 
  before retrying the job.

### 2. Mapper Not Emitting Correct Output
- Problem: The Mapper was failing to emit the expected key-value pairs.
- Solution: Investigated the issue through logs and confirmed that the input text was being tokenized properly.

### 3. Memory Overflow in the Reducer
- Problem: Large sets of documents led to memory overflow errors in the reducer.
- Solution: Optimized the reducer logic to process documents in smaller batches to avoid memory overload.

### 4. Permission Issues on HDFS Write
- Problem: Write permissions were not granted for the output directory in HDFS.
- Solution: Fixed the issue by running hdfs dfs -chmod -R 777 /output_final to update permissions.

### 5. Inconsistent Jaccard Similarity Calculations
- Problem: Jaccard similarity calculations were producing inconsistent results due to leading and trailing spaces in words.
- Solution: Removed unnecessary spaces from the words before adding them to the sets to ensure accurate calculations.

### 6. Output Formatting Issues
- Problem: The similarity percentages in the output were not formatted correctly.
- Solution: Adjusted the reducer logic to round the percentages to two decimal places, ensuring proper formatting.
