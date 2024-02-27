package com.fs.actor

import akka.actor.{Actor}
import com.fs.actor.FixedExpenseActor.{AddFixedExpense, FixedExpenseAdded, GetFixedExpense}
import com.fs.entity.FixedExpense

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
  }
}

object FixedExpenseActor {
  case class GetFixedExpense(id: String)
  case class AddFixedExpense(expense: FixedExpense)
  case class FixedExpenseAdded(id: String)
}

