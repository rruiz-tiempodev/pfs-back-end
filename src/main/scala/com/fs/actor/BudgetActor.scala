package com.fs.actor

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.HttpRequest
import com.fs.LocalUtils
import com.fs.actor.BudgetActor.GetBudgetByMonth
import com.fs.entity.{Budget, FixedExpense, Income, IncomeExpenseType}
import com.fs.entity.IncomeExpenseType.IncomeExpenseType

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Instant, ZonedDateTime}
import java.util.UUID.randomUUID

class BudgetActor extends Actor {

  override def receive: Receive = handleRequests(Map())

  implicit val system = context.system
  implicit val executionContext = context.dispatcher


  def handleRequests(store: Map[String, Income]): Receive = {
    case GetBudgetByMonth(month, incomeExpenseType) =>
      val response = Http().singleRequest(HttpRequest(uri = "https://free.currconv.com/api/v7/convert?q=USD_MXN&compact=ultra&apiKey=10f6e25f5f39ee453139")).mapTo[Map[String, BigDecimal]]
      response.map(res => {
        val usdToMxn = res.get("USD_MXN").get
        val mxToUsd = res.get("MXN_USD").get
        if (incomeExpenseType == IncomeExpenseType.WEEKLY)
          sender() ! getWeeklyBudgetByMonth(month, LocalUtils.incomes, LocalUtils.fixedExpenses)
        else
          sender() ! getMonthlyBudgetByMonth(month, LocalUtils.incomes, LocalUtils.fixedExpenses)
      })
  }

  def getWeeklyBudgetByMonth(month: ZonedDateTime, incomes:List[Income], fixedExpenses: List[FixedExpense]) = {
    val incomesByType = incomes.filter(_.incomeType == IncomeExpenseType.WEEKLY)
    val expenseByType = fixedExpenses.filter(_.expenseType == IncomeExpenseType.WEEKLY)
    val sumExpenses = expenseByType.map(_.amount).reduce((a,b) => a+b)
    val sumIncome = incomesByType.map(_.amount).reduce((a,b) => a+b)
    val total = sumIncome - sumExpenses
    getWeeks(month).map(friday => Budget(randomUUID.toString, incomesByType,expenseByType, IncomeExpenseType.WEEKLY,
        friday.format(DateTimeFormatter.BASIC_ISO_DATE ), total)).toList
  }

  def getMonthlyBudgetByMonth(month: ZonedDateTime, incomes:List[Income], fixedExpenses: List[FixedExpense]) = {
    val incomesByType = incomes.filter(_.incomeType == IncomeExpenseType.MONTHLY)
    val expenseByType = fixedExpenses.filter(_.expenseType == IncomeExpenseType.MONTHLY)
    val sumExpenses = expenseByType.map(_.amount).reduce((a,b) => a+b)
    val sumIncome = incomesByType.map(_.amount).reduce((a,b) => a+b)
    val total = sumIncome - sumExpenses
    List(Budget(randomUUID.toString, incomesByType,
      expenseByType, IncomeExpenseType.MONTHLY, month.format(DateTimeFormatter.BASIC_ISO_DATE ), total))
  }

  def getWeeks(month: ZonedDateTime) =
    (for {
      day <- Range.inclusive(1, month.toLocalDate.lengthOfMonth())
    } yield ZonedDateTime.from(month).withDayOfMonth(day)).filter(_.getDayOfWeek == DayOfWeek.FRIDAY)
}

object BudgetActor {
  case class GetBudgetByMonth(month: ZonedDateTime, incomeExpenseType: IncomeExpenseType)
}
