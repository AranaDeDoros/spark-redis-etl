package example
import java.net.URI
import com.typesafe.config.ConfigFactory

object RedisConfig {

  private val config = ConfigFactory.load()
  private val redisUri = new URI(config.getString("redis.url"))

  val host: String = redisUri.getHost
  val port: Int = redisUri.getPort
  val user: String = Option(redisUri.getUserInfo)
    .map(_.split(":")(0)).getOrElse("default")
  val password: String = Option(redisUri.getUserInfo).flatMap { ui =>
    ui.split(":").lift(1)
  }.getOrElse("")

  override def toString: String =
    s"Redis(host=$host, port=$port, user=$user, " +
      s"password=${if (password.nonEmpty) "***"
      else "(none)"})"

}
