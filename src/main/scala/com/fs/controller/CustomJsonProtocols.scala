package com.fs.controller

import com.fs.DefaultJsonFormats
import com.fs.actor.FixedExpenseActor.FixedExpenseAdded
import com.fs.entity.{ExpenseType, FixedExpense}
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

trait CustomJsonProtocols extends DefaultJsonFormats {


  implicit object CurrencyJsonFormat extends RootJsonFormat[ExpenseType.ExpenseType ] {
    def write(obj: ExpenseType.ExpenseType): JsValue = JsString(obj.toString)

    def read(json: JsValue): ExpenseType.ExpenseType = json match {
      case JsString(str) => ExpenseType.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }

  implicit val fixedExpenseAdded = jsonFormat1(FixedExpenseAdded)
  implicit val fixedExpense = jsonFormat3(FixedExpense)
}
