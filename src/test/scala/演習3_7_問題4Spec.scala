import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object 演習3_7_問題4Spec extends Properties("演習3_7_問題4Spec") {
  property("辞書のマージ") = forAll { (listA: List[(String, Int)], listB: List[(String, Int)]) =>
    val merged = listA.toMap ++ listB.toMap.map { case (k, v) =>
      k -> listA.toMap.getOrElse(k, v)
    }

    extractKeys(merged.toList.sortBy(_._1)) ==
      (extractKeys(listA) ++ extractKeys(listB)).distinct.sortBy(identity)

    // valueの検証がなかったので追加した。しかしlistAかlistBの内容に同じkeyの要素がある場合にコケる(toMapしたときにたぶん後の要素が消える)
    // したがって、引数でListではなくSetをもらうようにするか、Listをもらった場合の重複要素の扱いの仕様を決める必要がある。List => Set は型の制約が強くなるわけなので、それはそうだよね。これをシグネチャから気付けなかったのが悔しいね。
//    extractValues(merged.toList.sortBy(_._1)) ==
//      (extractValues(listA) ++ extractValues(listB)).distinct.sortBy(identity)
  }

  def extractKeys[K, V](list: List[(K, V)]): List[K] =
    list.map(_._1)

  def extractValues[K, V](list: List[(K, V)]): List[V] =
    list.map(_._2)
}
