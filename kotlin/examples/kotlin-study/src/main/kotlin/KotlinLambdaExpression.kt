
class KotlinLambdaExpression {
    fun anyFunctionsToNumber7 (lambda : (Int) -> Int) : Int {
        return lambda(7)
    }

    fun plus () : Int {
        return anyFunctionsToNumber7 { number -> number + number }
    }
}