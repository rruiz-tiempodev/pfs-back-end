package com.fs.actor

import akka.actor.Actor
import com.fs.LocalUtils
import com.fs.actor.ExpenseActor.{GetExpense, GetExpensesByMonth}
import com.fs.entity.Expense
import com.fs.entity.IncomeExpenseType.IncomeExpenseType


class ExpenseActor extends Actor {

  override def receive: Receive = handleExpenses(Map())

  def handleExpenses(store: Map[String, Expense]): Receive = {
    case GetExpense(id) =>
      sender() ! store.get(id)
      context.become(handleExpenses(store))
    case GetExpensesByMonth(year, month, expenseType) =>
      val fixedExpenses = LocalUtils.fixedExpenses

  }
}

object ExpenseActor {
  case class GetExpense(id: String)
  case class GetExpensesByMonth(year: Int, month: Int, expenseType: IncomeExpenseType)
}


