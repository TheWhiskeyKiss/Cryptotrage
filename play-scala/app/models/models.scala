package models 

// Bitfinex
case class Ticker(symbol:String,mid:String,bid:String,ask:String,last_price:String,low:String,high:String,volume:String,timestamp:String)

// Kraken
case class AssetPair(name:String,altname:String,aclass_base:String,base:String,aclass_quote:String,quote:String,lot:String,pair_decimals:Int,lot_decimals:Int,lot_multiplier:Int,leverage_buy:List[Int],leverage_sell:List[Int],fees:List[List[Double]],fees_maker:Option[List[List[Double]]],fee_volume_currency:String,margin_call:Int,margin_stop:Int)
