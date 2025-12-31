import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object 演習3_7_問題3Spec extends Properties("演習3_7_問題3Spec") {
  property("セットの和集合") = forAll { (listA: List[Int], listB: List[Int]) =>
    val setA = listA.toSet
    val setB = listB.toSet
    val setAB = setA ++ setB
    val listAB = setAB.toList
    val res = listAB.sorted

    // val modelUnion = (listA ++ listB).sorted // setで重複排除されるが、モデルは排除していないのでコケていた
    val modelUnion = (listA ++ listB).sorted.distinct
    res == modelUnion
  }

  // エラー時の出力
  // > ARG_0: List("-2147483648")
  // > ARG_1: List("-2147483648")
  // > ARG_0_ORIGINAL: List("1", "1442145878", "-2147483648", "-542749051", "0",
  //    "943070062", "89858260")
  // > ARG_1_ORIGINAL: List("-1783267541", "-1084075567", "-1338578474", "-21474
  //   83648", "301206747")
}
