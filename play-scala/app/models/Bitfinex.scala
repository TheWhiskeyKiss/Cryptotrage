package models

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.ws._
import scala.concurrent.Future
import scala.concurrent.duration._
import play.api.http.HttpEntity
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import scala.concurrent.ExecutionContext

import models._

import play.api.libs.json._
import org.joda.time._
import org.joda.time.format._

@Singleton
class Bitfinex @Inject()(ws:WSClient)  {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  /*
  def index = Action.async { implicit request =>
    val bftickers:Future[List[Ticker]] = getBitfinexTicker
    bftickers.map(t => Ok(views.html.index(t)))
  }
  */
  def allKeys(json: JsValue): collection.Set[String] = json match {
    case o: JsObject => o.keys ++ o.values.flatMap(allKeys)
    case JsArray(as) => as.flatMap(allKeys).toSet
    case _ => Set()
  }
  def getBitfinexTicker:Future[List[Ticker]] ={
    val symresponse:Future[WSResponse] = ws.url("https://api.bitfinex.com/v1/symbols").get()
    val fsymbols:Future[Array[String]] = symresponse.map(_.body.init.tail.filter(_ != '"').split(','))
    fsymbols.flatMap{syms =>
      val urls = syms.map(sym => "https://api.bitfinex.com/v1/pubticker/" + sym)
      val responses:Array[Future[WSResponse]] = urls.map(url => ws.url(url).get())
      val bodies:Array[Future[JsValue]] = responses.map(_.map(_.json))
      val jsmap:Array[(String,Future[JsValue])] = syms.zip(bodies)
      Future.sequence(jsmap.map{tup =>
        val sym = tup._1
        val fjs:Future[JsValue] = tup._2
        fjs.map{js => 
          val mid = (js \ "mid").as[String]
          val bid = (js \ "bid").as[String]
          val ask = (js \ "ask").as[String]
          val last_price = (js \ "last_price").as[String]
          val low = (js \ "low").as[String]
          val high= (js \ "high").as[String]
          val volume= (js \ "volume").as[String]
          val timestamp= (js \ "timestamp").as[String]
          val ms:Long = timestamp.split('.').head.toLong * 1000
          val dt = new DateTime(ms)
          Ticker(sym,mid,bid,ask,last_price,low,high,volume,dt.toString("yyyy-MM-dd HH:mm:ss z"))
        }
      }.toList)
    }
  }
}
