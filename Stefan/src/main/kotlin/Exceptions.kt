class WrongLengthException(what: String, has: Int, should: Int) :
    Exception("$what should have length $should, instead has $has!")