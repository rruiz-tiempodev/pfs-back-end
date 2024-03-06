package com.fs.actor

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.fs.{CustomJsonProtocols, LocalUtils}
import com.fs.actor.BudgetActor.GetBudgetByMonth
import com.fs.entity.{Budget, Currency, FixedExpense, Income, IncomeExpenseType}
import com.fs.entity.IncomeExpenseType.IncomeExpenseType
import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol.jsonFormat2

import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Instant, ZonedDateTime}
import java.util.UUID.randomUUID
import scala.concurrent.Future
import scala.util.{Failure, Success}


class BudgetActor extends Actor with CustomJsonProtocols with ActorLogging {

  override def receive: Receive = handleRequests(Map())

  implicit val system = context.system
  implicit val executionContext = context.dispatcher


  def handleRequests(store: Map[String, Income]): Receive = {
    case GetBudgetByMonth(month, incomeExpenseType) =>
      retrieveExchangeRateAndCreateContext.map { context =>
        context.month = month
        createIncomesEndExpenses(context, LocalUtils.incomes, LocalUtils.fixedExpenses)
        if (incomeExpenseType == IncomeExpenseType.WEEKLY)
          sender() ! getWeeklyBudgetByMonth(context)
        else
          sender() ! getMonthlyBudgetByMonth(context)
      }
  }

  def retrieveExchangeRateAndCreateContext: Future[Context] = {
    val response = Http().singleRequest(HttpRequest(uri = "https://free.currconv.com/api/v7/convert?q=USD_MXN,MXN_USD&compact=ultra&apiKey=10f6e25f5f39ee453139"))
    response.flatMap { res =>
      Unmarshal(res.entity).to[String].flatMap { exchange =>
        Unmarshal(exchange).to[Map[String, BigDecimal]].map { map =>
          Context(map.get("USD_MXN").get, map.get("MXN_USD").get)
        }
      }
    }
  }

  def createIncomesEndExpenses(context: Context, incomes:List[Income], fixedExpenses: List[FixedExpense]) = {
    context.weeklyExpenses = fixedExpenses.filter(_.expenseType == IncomeExpenseType.WEEKLY)
    context.monthlyExpenses = fixedExpenses.filter(_.expenseType == IncomeExpenseType.MONTHLY)
    context.weeklyIncomes = incomes.filter(_.incomeType == IncomeExpenseType.WEEKLY)
    context.monthlyIncomes = incomes.filter(_.incomeType == IncomeExpenseType.MONTHLY)
  }

  def getWeeklyBudgetByMonth(context: Context) = {
    val total = context.sumWeeklyIncomes - context.sumWeeklyExpenses
    getWeeks(context.month).map(friday => Budget(randomUUID.toString, context.weeklyIncomes, context.weeklyExpenses,
      IncomeExpenseType.WEEKLY, friday.format(DateTimeFormatter.BASIC_ISO_DATE ), total)).toList
  }

  def getMonthlyBudgetByMonth(context: Context) = {
    val total = context.sumMonthlyIncomes - context.sumMonthlyExpenses
    List(Budget(randomUUID.toString, context.monthlyIncomes, context.monthlyExpenses,
      IncomeExpenseType.MONTHLY, context.month.format(DateTimeFormatter.BASIC_ISO_DATE ), total))
  }

  def getWeeks(month: ZonedDateTime) =
    (for {
      day <- Range.inclusive(1, month.toLocalDate.lengthOfMonth())
    } yield ZonedDateTime.from(month).withDayOfMonth(day)).filter(_.getDayOfWeek == DayOfWeek.FRIDAY)
}

object BudgetActor {
  case class GetBudgetByMonth(month: ZonedDateTime, incomeExpenseType: IncomeExpenseType)
}

case class Context(usdToMxn: BigDecimal, mxToUsd: BigDecimal) {
  private var iweeklyIncomes: List[Income] = List()
  private var imonthlyIncomes: List[Income] = List()
  private var iweeklyExpenses: List[FixedExpense] = List()
  private var imonthlyExpenses: List[FixedExpense] = List()
  private var imonth: ZonedDateTime = ZonedDateTime.now()

  def month = imonth
  def month_=(value: ZonedDateTime): Unit = imonth = value

  def weeklyIncomes = iweeklyIncomes
  def weeklyIncomes_=(value: List[Income]): Unit = iweeklyIncomes = value.map(convertIncomeToMxn)

  def monthlyIncomes = imonthlyIncomes
  def monthlyIncomes_=(value: List[Income]): Unit = imonthlyIncomes = value.map(convertIncomeToMxn)

  def weeklyExpenses = iweeklyExpenses
  def weeklyExpenses_=(value: List[FixedExpense]): Unit = iweeklyExpenses = value.map(convertExpenseToMxn)

  def monthlyExpenses = imonthlyExpenses
  def monthlyExpenses_=(value: List[FixedExpense]): Unit = imonthlyExpenses = value.map(convertExpenseToMxn)

  def sumWeeklyExpenses = {
    iweeklyExpenses.map(_.amount).reduce((a,b) => a+b)
  }

  def sumWeeklyIncomes = {
    iweeklyIncomes.map(_.amount).reduce((a,b) => a+b)
  }

  def sumMonthlyExpenses = {
    imonthlyExpenses.map(_.amount).reduce((a,b) => a+b)
  }

  def sumMonthlyIncomes = {
    imonthlyIncomes.map(_.amount).reduce((a,b) => a+b)
  }

  def convertIncomeToMxn(income:Income): Income = {
    if (income.currency == Currency.USD)
      Income(income.id, income.amount * usdToMxn, income.incomeType, income.description, income.currency)
    else
      income
  }

  def convertExpenseToMxn(expense:FixedExpense): FixedExpense = {
    if (expense.currency == Currency.USD)
      FixedExpense(expense.id, expense.amount * usdToMxn, expense.expenseType, expense.description, expense.currency)
    else
      expense
  }
}