package com.fs


import com.fs.entity.{Currency, FixedExpense, Income, IncomeExpenseType}

import java.util.UUID.randomUUID

object LocalUtils {
  val fixedExpenses = List(
    FixedExpense(randomUUID.toString, 6000, IncomeExpenseType.WEEKLY, "Pensión", Currency.MXP),
    FixedExpense(randomUUID.toString, 10000, IncomeExpenseType.MONTHLY, "Colegiatura Lu", Currency.MXP),
    FixedExpense(randomUUID.toString, 15000, IncomeExpenseType.MONTHLY, "Colegiatura Kiti", Currency.MXP),
    FixedExpense(randomUUID.toString, 24700, IncomeExpenseType.MONTHLY, "Renta", Currency.MXP),
    FixedExpense(randomUUID.toString, 10000, IncomeExpenseType.WEEKLY, "Fin de Semana", Currency.MXP)
  )

  val incomes = List(
    Income(randomUUID.toString, 18000, IncomeExpenseType.WEEKLY, "AutoZone", Currency.MXP),
    Income(randomUUID.toString, 8000, IncomeExpenseType.MONTHLY, "Payoneer", Currency.USD),
  )


}
