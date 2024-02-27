package com.fs.entity

import com.fs.entity.ExpenseType.ExpenseType

case class FixedExpense(id:String, expenseType:ExpenseType, amount: BigDecimal)

