package org.camunda.dmn

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.camunda.dmn.DmnEngine._

class DmnEngineTest extends FlatSpec with Matchers {
  
  val engine = new DmnEngine
  
  def discountDecision = getClass.getResourceAsStream("/decisiontable/discount.dmn")
  def invalidExpressionDecision = getClass.getResourceAsStream("/decisiontable/invalid-expression.dmn")
  
  "A DMN engine" should "evaluate a decision table" in {
    
    engine.eval(discountDecision, "discount", Map("customer" -> "Business", "orderSize" -> 7)) should be(Right(Result(0.1)))
  }
  
  it should "parse and evaluate a decision table" in {
    
    val parseResult = engine.parse(discountDecision)
    
    parseResult.isRight should be(true)
    
    val parsedDmn = parseResult.right.get
    
    engine.eval(parsedDmn, "discount", Map("customer" -> "Business", "orderSize" -> 7)) should be(Right(Result(0.1)))
  }
  
  it should "report parse failures" in {
    
    val parseResult = engine.parse(invalidExpressionDecision)
    
    parseResult.isLeft should be(true)
    
    val failure = parseResult.left.get
    
    failure.message should include ("Failed to parse FEEL unary-tests '> 10L'")
  }
  
  it should "report parse failures on evaluation" in {
    
    val result = engine.eval(invalidExpressionDecision, "discount", Map[String, Any]())
    
    result.isLeft should be(true)
    
    val failure = result.left.get
    
    failure.message should include ("Failed to parse FEEL unary-tests '> 10L'")
  }
  
  it should "report an evaluation failure" in {
    
    val result = engine.eval(discountDecision, "discount", Map[String, Any]("customer" -> "Business", "orderSize" -> "foo"))
    
    result.isLeft should be(true)
    
    val failure = result.left.get
    
    failure.message should include ("failed to evaluate expression '< 10'")
  }
  
}