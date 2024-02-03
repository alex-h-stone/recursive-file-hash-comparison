# Recursive Directory Comparison

#### Tool for determining with a high level of certainty whether 2 directory structures are identical, and highlighting any discrepancies.

For 2 directories to be considered identical the following conditions must be met:

1. For every subdirectory on the left of the comparison there must exist a subdirectory on the right with an identical
   name and identical path relative to it's base/working directory.
2. *Same criteria as 1. with opposite direction of comparison (switch left and right)
3. For every file on the left of the comparison there must exist a file on the right which matches.
   For 2 files to match the following conditions must be met:

- Identical filenames
- Identical filename extensions
- Identical file size in bytes
- Identical file hash values
- Identical paths relative to their base/working directories

4. *Same criteria as 3. with opposite direction of comparison (switch left and right)

### Components

#### Work item producer

Given an absolute path to a working directory, traverse the entire directory structure
and create a new `Work Item` for each file and each directory.
Each work item is then sent to a disk backed queue to be processed later by a
work item consumer.

#### Work item consumer

Responsible for dequeuing Work Item messages from the queue.
Each Work Item is processed to produce a Hash Result which contains a variety of file meta-data including the hash of
the file.

All hash results are deserialised into JSON and persisted in a MongoDB collection and indexed on a number of fields
including the hash.

## Extra

on startup publish and consumer to a test queue
and store and retrieve to a test collection

Comparison Report Generator
Given data stored in the MongoDb - generate comparison reports

### Requirements

- Java 21
- Mongo DB
- Apache Active MQ

### How to run

1. Start Apache Active MQ via:

``` powershell
./startActiveMQ.ps1
```

2. Verify Active MQ via the admin interface http://127.0.0.1:8161/admin/ with
   default login credentials `admin`/`admin`.
3. Verify Active MQ via the log file at
   `C:\Program Files\apache-activemq-5.18.3\data\activemq.log` has activity with recent
   timestamps.
4. Can run via Gradle bootRun e.g.
   bootRun --args=consumer
5. Or, can run from a jar e.g.
   java -jar abc.jar --consumer

The file hashes and other details are then stored in Mongo DB for subsequently producing a variety of reports including:

- Identical files; with identical name or hash
- Files with matching relative path and name but differing hashes
- Files with the same hash
