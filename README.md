Recursive File Hash Comparison

Tool for determining with a high level of certainty whether 2 folders are identical, and highlighting any discrepancies.


How to run
TODO


Uses RabbitMQ to manage the production and consumption/processing of all the files across all working directories.

The file hashes and other details are then stored in Mongo DB for subsequently producing a varoity of reports including:
- Identical files; with identical name or hash
- Files with matching relative path and name but differing hashes
- Files with the same hash
