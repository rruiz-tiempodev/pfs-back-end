package com.fs


import com.fs.entity.{Currency, ExpenseType, FixedExpense}

import java.util.UUID.randomUUID

object LocalUtils {
  val fixedExpenses = List(
    FixedExpense(randomUUID.toString, 6000, ExpenseType.WEEKLY, "Pensión", Currency.MXP),
    FixedExpense(randomUUID.toString, 10000, ExpenseType.MONTHLY, "Colegiatura Lu", Currency.MXP),
    FixedExpense(randomUUID.toString, 15000, ExpenseType.MONTHLY, "Colegiatura Kiti", Currency.MXP),
    FixedExpense(randomUUID.toString, 24700, ExpenseType.MONTHLY, "Renta", Currency.MXP),
    FixedExpense(randomUUID.toString, 10000, ExpenseType.WEEKLY, "Fin de Semana", Currency.MXP)
  )
}
