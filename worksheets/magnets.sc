trait ArrayReply {
  def message: String
}

class ArrayReply[T](val array: Array[T]) {

}

new ArrayReply(Array(1,2,3,4))