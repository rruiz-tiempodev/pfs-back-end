package com.fs.entity

import com.fs.entity.IncomeExpenseType.IncomeExpenseType

case class Budget(id: String, incomes: List[Income], expenses: List[FixedExpense], incomeExpenseType: IncomeExpenseType, description: String)
