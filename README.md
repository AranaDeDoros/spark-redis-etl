# Spark + Redis ETL 

A Scala test project that processes transaction data using **Apache Spark** and stores the results in **Redis** for real-time access by a REST API.  Dataset [here](https://archive.ics.uci.edu/dataset/352/online+retail).

---

##  Overview

This ETL (Extract, Transform, Load) reads a CSV file containing historical retail transactions, filters records for **September 2011**, cleans and transforms the data, and stores it into Redis using composite keys per customer and date. Tweak it to your heart's content.

**Architecture**
```text
CSV → Spark ETL → Redis (key-value store) 
```

**Conf file example**
```text
redis {
url = "redis://user:passwd@host:port"
}
```

# API #
You can find the API  [here](https://github.com/AranaDeDoros/redis-etl-play-api).
