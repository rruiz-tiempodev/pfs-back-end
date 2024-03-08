package com.fs.actor

import akka.actor.{Actor, ActorLogging}
import com.fs.{LocalUtils}
import com.fs.actor.BudgetActor.GetBudgetByMonth
import com.fs.entity.{Budget, Currency, FixedExpense, Income, IncomeExpenseType}
import com.fs.entity.IncomeExpenseType.{IncomeExpenseType, MONTHLY}


import java.time.format.DateTimeFormatter
import java.time.{DayOfWeek, Instant, ZonedDateTime}
import java.util.UUID.randomUUID



class BudgetActor extends Actor with ActorLogging {

  override def receive: Receive = handleRequests(Map())

  implicit val system = context.system
  implicit val executionContext = context.dispatcher


  def handleRequests(store: Map[String, Income]): Receive = {
    case GetBudgetByMonth(month, incomeExpenseType) =>
      val theSender = sender()
      val budgetContext = BudgetContext()
      budgetContext.month = month
      createIncomesEndExpenses(budgetContext, LocalUtils.incomes, LocalUtils.fixedExpenses)
      if (incomeExpenseType == IncomeExpenseType.WEEKLY) {
        theSender ! getWeeklyBudgetByMonth(budgetContext)
        context.become(handleRequests(store))
      } else {
        theSender ! getMonthlyBudgetByMonth(budgetContext)
        context.become(handleRequests(store))
      }
  }

  def createIncomesEndExpenses(context: BudgetContext, incomes:List[Income], fixedExpenses: List[FixedExpense]) = {
    context.weeklyExpenses = fixedExpenses.filter(_.expenseType == IncomeExpenseType.WEEKLY)
    context.monthlyExpenses = fixedExpenses.filter(_.expenseType == IncomeExpenseType.MONTHLY)
    context.weeklyIncomes = incomes.filter(_.incomeType == IncomeExpenseType.WEEKLY)
    context.monthlyIncomes = incomes.filter(_.incomeType == IncomeExpenseType.MONTHLY)
  }

  def getWeeklyBudgetByMonth(context: BudgetContext) = {
    //val total = context.sumWeeklyIncomes - context.sumWeeklyExpenses
    getWeeks(context.month).map(friday => Budget(randomUUID.toString, context.weeklyIncomes, context.weeklyExpenses,
      IncomeExpenseType.WEEKLY, friday.format(DateTimeFormatter.BASIC_ISO_DATE ))).toList
  }

  def getMonthlyBudgetByMonth(context: BudgetContext) = {
    //val total = context.sumMonthlyIncomes - context.sumMonthlyExpenses
    List(Budget(randomUUID.toString, context.monthlyIncomes, context.monthlyExpenses,
      IncomeExpenseType.MONTHLY, context.month.format(DateTimeFormatter.BASIC_ISO_DATE )))
  }

  def getWeeks(month: ZonedDateTime) =
    (for {
      day <- Range.inclusive(1, month.toLocalDate.lengthOfMonth())
    } yield ZonedDateTime.from(month).withDayOfMonth(day)).filter(_.getDayOfWeek == DayOfWeek.FRIDAY)
}

object BudgetActor {
  case class GetBudgetByMonth(month: ZonedDateTime, incomeExpenseType: IncomeExpenseType)
}

case class BudgetContext(/*usdToMxn: BigDecimal, mxToUsd: BigDecimal*/) {
  private var iweeklyIncomes: List[Income] = List()
  private var imonthlyIncomes: List[Income] = List()
  private var iweeklyExpenses: List[FixedExpense] = List()
  private var imonthlyExpenses: List[FixedExpense] = List()
  private var imonth: ZonedDateTime = ZonedDateTime.now()

  def month = imonth
  def month_=(value: ZonedDateTime): Unit = imonth = value

  def weeklyIncomes = iweeklyIncomes
  def weeklyIncomes_=(value: List[Income]): Unit = iweeklyIncomes = value

  def monthlyIncomes = imonthlyIncomes
  def monthlyIncomes_=(value: List[Income]): Unit = imonthlyIncomes = value

  def weeklyExpenses = iweeklyExpenses
  def weeklyExpenses_=(value: List[FixedExpense]): Unit = iweeklyExpenses = value

  def monthlyExpenses = imonthlyExpenses
  def monthlyExpenses_=(value: List[FixedExpense]): Unit = imonthlyExpenses = value

  /*def sumWeeklyExpenses = {
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
  }*/

  /*def convertIncomeToMxn(income:Income): Income = {
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
  }*/
}