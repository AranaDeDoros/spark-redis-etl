package example
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Main {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.WARN)
    Logger.getLogger("akka").setLevel(Level.WARN)

    val redis = RedisConfig
    val spark = SparkSession.builder()
      .appName("SparkRedisETL")
      .master("local[*]")
      .config("spark.redis.host", redis.host)
      .config("spark.redis.port", redis.port)
      .config("spark.redis.auth", redis.password)
      .getOrCreate()

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("Online Retail.csv")

    val dfWithTimestamp = df.withColumn(
      "InvoiceDate",
      to_date(col("InvoiceDate"), "dd/MM/yyyy H:m")
    )
    dfWithTimestamp.printSchema()

    val dfSept2011 =    dfWithTimestamp
      .filter(year(col("InvoiceDate")) === 2011)
      .filter(month(col("InvoiceDate")) === 9)
      .filter(col("CustomerID").isNotNull)
      .withColumn(
        "redisKey",
          concat(
          lit("customer:"),
          col("CustomerID").cast("string"),
          lit(":"),
          date_format(col("InvoiceDate"), "yyyy-MM-dd")
        )
      )
      .filter(col("redisKey").isNotNull)
      .select("redisKey", "CustomerID", "Description", "UnitPrice", "InvoiceDate")

    dfSept2011.printSchema()
    dfSept2011.show(5, false)
    println("NULL redisKeys: " + dfSept2011.filter(col("redisKey").isNull).count())

    dfSept2011.write.format("org.apache.spark.sql.redis")
      .option("table", "sept2011_purchases")
      .option("key.column", "redisKey")
      .mode("overwrite")      .save()

    val dfFromRedis = spark.read.format("org.apache.spark.sql.redis")
      .option("table", "sept2011_purchases")
      .option("key.column", "redisKey")
      .load()
    println("|||||||||||||||")
    dfFromRedis.show(5)

    spark.stop()

  }
}
