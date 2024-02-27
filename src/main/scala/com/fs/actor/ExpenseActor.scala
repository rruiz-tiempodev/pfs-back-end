package com.fs.actor

import akka.actor.Actor
import com.fs.actor.ExpenseActor.GetExpense
import com.fs.entity.Expense

class ExpenseActor extends Actor {

  override def receive: Receive = handleExpenses(Map())

  def handleExpenses(store: Map[String, Expense]): Receive = {
    case GetExpense(id) =>
      sender() ! store.get(id)
      context.become(handleExpenses(store))
  }
}

object ExpenseActor {
  case class GetExpense(id: String)
}


