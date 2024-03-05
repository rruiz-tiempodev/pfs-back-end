package com.fs.actor

import akka.actor.Actor
import com.fs.actor.IncomeActor.{AddIncome, GetIncome, GetIncomes, IncomeAdded, UpdateIncome}
import com.fs.entity.{FixedExpense, Income}

class IncomeActor extends Actor {
  override def receive: Receive = handleRequests(Map())

  def handleRequests(store: Map[String,Income]): Receive = {
    case GetIncome(id) =>
      sender() ! store(id)
      context.become(handleRequests(store))
    case AddIncome(income: Income) =>
      val newStore = store + (income.id -> income)
      sender() ! IncomeAdded(income.id)
      context.become(handleRequests(newStore))
    case UpdateIncome(id, expense: FixedExpense) =>
      store(id)
      val newStore = store + (id -> expense)
      sender() ! ""
      context.become(handleRequests(newStore))
    case GetIncomes =>
      sender() ! store.values.toList
  }
}

object IncomeActor {
  case class GetIncome(id: String)
  case class AddIncome(income: Income)
  case class IncomeAdded(id: String)
  case class UpdateIncome(id: String, income: Income)
  object GetIncomes
}



