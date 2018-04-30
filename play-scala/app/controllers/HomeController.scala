package controllers

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
class HomeController @Inject()(ws:WSClient,
                               bitfinex:Bitfinex) extends Controller {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  def index = Action.async { implicit request =>
    val bftickers:Future[List[Ticker]] = bitfinex.getBitfinexTicker
    getKrakenTicker
    bftickers.map(t => Ok(views.html.index(t)))
  }
  def getKrakenTicker = {
    val apikey = "IdMhJPGozQfCmXWl7+DaMuVtotW4zLEirPVPQIziQG26Q2yG0/YMuwUt"
    val pairResponse:Future[WSResponse] = ws.url("https://api.kraken.com/0/public/AssetPairs").get()
    val fjs:Future[JsValue] = pairResponse.map(_.json)
    fjs.map{js =>
      val errors:JsValue = (js \ "error").get
      val jsres:JsValue = (js \ "result").get
      val keys = allKeys(jsres)
    }
  }
  def allKeys(json: JsValue): collection.Set[String] = json match {
    case o: JsObject => o.keys ++ o.values.flatMap(allKeys)
    case JsArray(as) => as.flatMap(allKeys).toSet
    case _ => Set()
  }
}
