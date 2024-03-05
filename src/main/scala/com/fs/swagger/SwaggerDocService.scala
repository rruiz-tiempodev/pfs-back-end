package com.fs.swagger

import com.fs.controller.{ExpenseController, FixedExpenseController, BudgetController, IncomeController}
import com.github.swagger.akka.model.Info
import com.github.swagger.akka.SwaggerHttpService
import com.github.swagger.akka.ui.SwaggerHttpWithUiService
import io.swagger.v3.oas.models.ExternalDocumentation

object SwaggerDocService extends SwaggerHttpWithUiService {
  override val apiClasses = Set(classOf[BudgetController], classOf[FixedExpenseController],
    classOf[ExpenseController], classOf[IncomeController])
  override val host = "localhost:8080"
  override val apiDocsPath = "api-docs"
  override val info: Info = Info(version = "1.0")
}
