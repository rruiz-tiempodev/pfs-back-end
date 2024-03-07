package com.fs.client


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.fs.CustomJsonProtocols
import com.fs.cache.FSCacheConfig.scalaCacheGuava
import scalacache.cachingF
import scalacache.modes.scalaFuture.mode
import scala.concurrent.{ExecutionContextExecutor, Future}

class CurrencyExchangeClient()(implicit system: ActorSystem, executionContext: ExecutionContextExecutor)  extends CustomJsonProtocols {

  def retrieveExchangeRate(currencies: String): Future[Map[String, BigDecimal]] = {
    cachingF("currencies", currencies)(None) {
      val response = Http().singleRequest(HttpRequest(uri = s"https://free.currconv.com/api/v7/convert?q=$currencies&compact=ultra&apiKey=10f6e25f5f39ee453139"))
      response.flatMap { res =>
        Unmarshal(res.entity).to[String].flatMap { exchange =>
          Unmarshal(exchange).to[Map[String, BigDecimal]].map { map =>
            map
          }
        }
      }
    }
  }
}
