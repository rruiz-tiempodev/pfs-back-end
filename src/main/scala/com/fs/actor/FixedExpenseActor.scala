package com.fs.actor

import akka.actor.Actor
import com.fs.actor.FixedExpenseActor.{AddFixedExpense, FixedExpenseAdded, GetFixedExpense, GetFixedExpenses, UpdateFixedExpense}
import com.fs.entity.FixedExpense
import com.fs.entity.IncomeExpenseType.IncomeExpenseType

class FixedExpenseActor extends Actor {
  override def receive: Receive = handleRequests(Map())

  def handleRequests(store: Map[String,FixedExpense]): Receive = {
    case GetFixedExpense(id) =>
      sender() ! store(id)
      context.become(handleRequests(store))
    case AddFixedExpense(expense: FixedExpense) =>
      val newStore = store + (expense.id -> expense)
      sender() ! FixedExpenseAdded(expense.id)
      context.become(handleRequests(newStore))
    case UpdateFixedExpense(id, expense: FixedExpense) =>
      store(id)
      val newStore = store + (id -> expense)
      sender() ! ""
      context.become(handleRequests(newStore))
    case GetFixedExpenses =>
      sender() ! store.values.toList
  }
}

object FixedExpenseActor {
  case class GetFixedExpense(id: String)
  case class AddFixedExpense(expense: FixedExpense)
  case class FixedExpenseAdded(id: String)
  case class GetFixedExpensesByType(expenseType: IncomeExpenseType)
  case class UpdateFixedExpense(id: String, expense: FixedExpense)
  object GetFixedExpenses
}

