package example
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Main {
  def main(args: Array[String]): Unit = {

    // Reduce logs de Spark
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

    //val customer = df.filter("CustomerID = 17850").show(10)
    //val fromUK = df.filter("Country = 'France'").show(10)
    ///val mostExpensiveByCustomer = df.groupBy("CustomerID").max("UnitPrice").alias("highest unit price").show(10)

    val dfWithTimestamp = df.withColumn(
      "InvoiceDate",
      to_date(col("InvoiceDate"), "dd/MM/yyyy H:m")
    )

    val dfSept2011 = dfWithTimestamp
      .filter(year(col("ts")) === 2011)
      .filter(month(col("ts")) === 9)
      .select("CustomerID", "Description", "UnitPrice", "InvoiceDate" )

    dfSept2011.show(5)
    df.printSchema()
    spark.stop()

  }
}
