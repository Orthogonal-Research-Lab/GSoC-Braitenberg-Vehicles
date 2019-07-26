class WrongLengthException(what: String, has: Int, should: Int) :
    Exception("$what should have length $should, instead has $has!")

class AdjacencyMatrixCircuitsPossible: Exception("Matrix should not contain any values in the upper diagonal!")