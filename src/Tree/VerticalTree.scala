package Tree

import com.fasterxml.jackson.core.`type`.TypeReference
import jj.{Comparator, UserType}
import java.util.Random
import scala.collection.mutable

class VerticalTree(var sample: UserType = null) {
  var root: VerticalTreeNode = null
  def size: Int = {
    if (root == null){
      0
    }else{
      root.getSubtreeSize
    }
  }

  def add(v: UserType): Unit = {
    if (root == null) root = new VerticalTreeNode(v)
    else root.add(v, sample.getTypeComparator)
  }

  def get(index: Int): UserType = {
    if (root == null || index < 0 || index >= root.getSubtreeSize) throw new IndexOutOfBoundsException
    var curr = root.copy
    for (i <- 0 until index) {
      curr = curr.upSift
    }
    curr.data
  }

  def remove(index: Int): UserType = {
    if (root == null || index < 0 || index >= root.getSubtreeSize) throw new IndexOutOfBoundsException
    val size = root.getSubtreeSize
    var curr = root.copy
    root = null
    var result: UserType = null
    for (i <- 0 until size) {
      if (i == index) result = curr.data
      else add(curr.data)
      curr = curr.upSift
    }
    result
  }

  def balance(): Unit = {
    var curr = root.copy
    root = null
    while ( {
      curr != null
    }) {
      addBalanced(curr.data)
      curr = curr.upSift
    }
  }

  private def addBalanced(v: UserType): Unit = {
    if (root == null) root = new VerticalTreeNode(v)
    else root.addBalanced(v, sample.getTypeComparator)
  }

  import com.fasterxml.jackson.annotation.JsonAutoDetect
  import com.fasterxml.jackson.annotation.PropertyAccessor
  import com.fasterxml.jackson.core.JsonProcessingException
  import com.fasterxml.jackson.databind.ObjectMapper

  def serialize: String = try {
    val mapper = new ObjectMapper
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
    mapper.writeValueAsString(this)
  } catch {
    case e: JsonProcessingException =>
      throw new RuntimeException(e)
  }

  import com.fasterxml.jackson.annotation.JsonAutoDetect
  import com.fasterxml.jackson.annotation.PropertyAccessor
  import com.fasterxml.jackson.core.JsonProcessingException

  import com.fasterxml.jackson.databind.ObjectMapper
  import com.fasterxml.jackson.databind.module.SimpleModule
  import jj.UserType

  def deserialize[T <: UserType](s: String, clazz: Class[UserType]): UserType = try {
    val mapper = new ObjectMapper
    mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
    mapper.registerModule(new SimpleModule().addAbstractTypeMapping(classOf[UserType], clazz))
    mapper.readValue(s, new TypeReference[UserType]() {})
  } catch {
    case e: JsonProcessingException =>
      throw new RuntimeException(e)
  }
}

class VerticalTreeNode(var data: UserType) {
  private val random = new Random();
  private var subtreeSize = 1

  import java.util

  private val children = new util.ArrayList[VerticalTreeNode]

  def getSubtreeSize = subtreeSize

  def add(a: UserType, comparator: Comparator): Unit = {

    var v = a

    if (comparator.compare(data, v) > 0) {
      val sw = data
      data = v
      v = sw
    }
    val childIndex = random.nextInt(children.size + 1)
    if (childIndex == children.size) children.add(new VerticalTreeNode(v))
    else children.get(childIndex).add(v, comparator)
    subtreeSize += 1
  }

  def addBalanced(a: UserType, comparator: Comparator): Unit = {

    var v = a

    if (comparator.compare(data, v) > 0) {
      val sw = data
      data = v
      v = sw
    }
    if (children.size < 2) children.add(new VerticalTreeNode(v))
    else if (children.get(0).subtreeSize > children.get(1).subtreeSize) children.get(1).addBalanced(v, comparator)
    else children.get(0).addBalanced(v, comparator)
    subtreeSize += 1
  }
  import scala.collection.JavaConverters._
  def copy: VerticalTreeNode = {
    var result = new VerticalTreeNode(data)
    result.subtreeSize = subtreeSize

    for (child: VerticalTreeNode <- asScala(children)) {
      result.children.add(child.copy)
    }
    result
  }

  def upSift: VerticalTreeNode = {
    if (children.isEmpty) return null
    var minIndex = -1
    var i = 0
    while ( {
      i < children.size
    }) {
      val child = children.get(i)
      if (minIndex == -1 || data.getTypeComparator.compare(children.get(minIndex).data, child.data) > 0) minIndex = i

      i += 1
    }
    data = children.get(minIndex).data
    val siftedChild = children.get(minIndex).upSift
    if (siftedChild == null) children.remove(minIndex)
    this
  }

  def toString(builder: mutable.StringBuilder, depth: Int): Unit = {
    if (depth > 0) {
      for (i <- 0 until depth - 1) {
        builder.append("\t")
      }
      builder.append("|-- ")
    }
    builder.append(data.toString).append(System.lineSeparator)
    import scala.collection.JavaConverters
    for (child: VerticalTreeNode <- asScala(children)) {
      child.toString(builder, depth + 1)
    }
  }
}
