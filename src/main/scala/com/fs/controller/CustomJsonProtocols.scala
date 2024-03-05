package com.fs.controller

import com.fs.DefaultJsonFormats
import com.fs.actor.FixedExpenseActor.{FixedExpenseAdded, UpdateFixedExpense}
import com.fs.actor.IncomeActor.IncomeAdded
import com.fs.entity.{Budget, Currency, Expense, FixedExpense, Income, IncomeExpenseType}
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

import java.time.ZonedDateTime

trait CustomJsonProtocols extends DefaultJsonFormats {

  implicit object ZonedDateTimeJsonFormat extends RootJsonFormat[ZonedDateTime] {
    def write(obj: ZonedDateTime): JsValue = JsString(obj.toString)

    def read(json: JsValue): ZonedDateTime = json match {
      case JsString(str) => ZonedDateTime.parse(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }

  implicit object CurrencyJsonFormat extends RootJsonFormat[Currency.Currency ] {
    def write(obj: Currency.Currency): JsValue = JsString(obj.toString)

    def read(json: JsValue): Currency.Currency = json match {
      case JsString(str) => Currency.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }

  implicit object IncomeExpenseTypeJsonFormat extends RootJsonFormat[IncomeExpenseType.IncomeExpenseType ] {
    def write(obj: IncomeExpenseType.IncomeExpenseType): JsValue = JsString(obj.toString)

    def read(json: JsValue): IncomeExpenseType.IncomeExpenseType = json match {
      case JsString(str) => IncomeExpenseType.withName(str)
      case _ => throw new DeserializationException("Enum string expected")
    }
  }


  implicit val fixedExpenseAdded = jsonFormat1(FixedExpenseAdded)
  implicit val fixedExpense = jsonFormat5(FixedExpense)
  implicit val expense = jsonFormat8(Expense)
  implicit val updateFixedExpense = jsonFormat2(UpdateFixedExpense)
  implicit val income = jsonFormat5(Income)
  implicit val incomeAdded = jsonFormat1(IncomeAdded)
  implicit val budget = jsonFormat6(Budget)
}
