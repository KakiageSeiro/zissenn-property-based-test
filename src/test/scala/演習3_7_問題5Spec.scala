import org.scalacheck.Prop.{ forAll, propBoolean }
import org.scalacheck.{ Gen, Prop, Properties }
import 演習3_7_問題5Spec.ジェネレータ.ひらがなカタカナ漢字アルファベット数字

object 演習3_7_問題5Spec extends Properties("演習3_7_問題5Spec") {
  // forAllにテスト用データのジェネレータを渡すことができる
  property("ラウンドトリップ") = forAll(Gen.nonEmptyListOf(ひらがなカタカナ漢字アルファベット数字)) { (words: List[String]) =>
    // このテストは テスト用データとして複数の単語を用意 → 空白で結合 → テスト対象に渡す の流れだが、テスト対象の中でsplitしていることを知っているので、マッチポンプのように感じてしまう...
    // 一旦の答えとして、このテストによって「どのような文字でも正常に動作する」ことは確認できている。
    // 空白が1つではなく2つ連続するなどのケースはテストできないが、このプロパティがあることによって文字自体は何でもいけることの確度が高くなっていることに価値がある
    val text = words.mkString(" ")
    targetFunc(text) == words.length

    // collectは検証と(Genとかで生成した)値の収集を一緒にやる関数。第２引数がPropだが、Booleanを渡せる。たぶんimplicit
    // ちなみに値はめちゃ文字化けしたようなものが大量にログに出力されるのでコメントアウト
    //
    // val result = targetFunc(text) == words.length
    // Prop.collect(text)(result)
  }

  property("不変性質: 前後の空白は単語数に影響しない") = forAll { (text: String) =>
    targetFunc(text) == targetFunc(text.trim)
  }

  property("不変性質: 空白が一つでなくとも単語数に影響しない") = forAll(Gen.alphaStr, Gen.choose(2, 10)) { (str: String, n: Int) =>
    val space = " " * n
    // 空白を、空白n連にしても、単語の数は変わらない
    targetFunc(str) == targetFunc(str.replace(" ", space))
  }

  // ARG_0: ""
  // のときにコケていた(現在は修正済み)。これはプロパティベースドテストで未知のバグを発見できたってことだと思う！いいぞ！
  property("不変性質: 前後に空白を追加しても、結果は変わらない") = forAll { (str: String) =>
    // strの前後に空白を追加しても、結果は変わらない
    targetFunc(str) == targetFunc(" " + str + " ")
  }

  property("結合の性質: s1とs2を空白でつなげた文字列の文字数と、s1の文字数とs2の文字数を足した数値は同一") = forAll { (s1: String, s2: String) =>
    // ==>で生成するデータの条件を指定できる。ただし、条件を厳しくしすぎると警告がでる。十分なケース数を確保できず、プロパティベースドテストの意味がなくなってしまうため。
    (s1.trim.nonEmpty && s2.trim.nonEmpty) ==> {
      targetFunc(s1 + " " + s2) == targetFunc(s1) + targetFunc(s2)
    }
  }

  property("オラクルパターン") = forAll { (text: String) =>
    def altWordCount(str: String): Int = {
      def go(chars: List[Char], inWord: Boolean): Int = chars match {
        // 終了条件。直前が空白でなかったら(inWord = true)文字数をインクリメント
        case Nil         => if (inWord) 1 else 0
        // 空白が来た場合、直前空白ではなかったら(inWord = true)文字数をインクリメント。直前空白だったら空白が連続しているので無視して再起
        case ' ' :: rest => if (inWord) 1 + go(rest, false) else go(rest, false)
        // 単語の途中の文字が_に入っていたら再起
        case _ :: rest   => go(rest, true)
      }

      go(str.toList, false)
    }

    targetFunc(text) == altWordCount(text)
  }

  def targetFunc(str: String): Int = {
    // str.split(" ").toList.size

    // 「不変性質: 前後に空白を追加しても、結果は変わらない」で発見したバグ修正後の実装
    str.split(" ").count(_.nonEmpty)
  }

  object ジェネレータ {
    // Gen.chooseで指定した範囲の中から生成する
    private val ひらがな: Gen[Char] = Gen.choose('\u3040', '\u309F') // ひらがな
    private val カタカナ: Gen[Char] = Gen.choose('\u30A0', '\u30FF') // カタカナ
    private val 漢字: Gen[Char]   = Gen.choose('\u4E00', '\u9FFF') // 漢字
    // Gen.oneOfでどれか一つ選ぶ
    private val multilingualChar: Gen[Char] = Gen.oneOf(
      Gen.alphaNumChar, // アルファベット・数字
      ひらがな,
      カタカナ,
      漢字
    )
    val ひらがなカタカナ漢字アルファベット数字: Gen[String] = Gen.nonEmptyListOf(multilingualChar).map(_.mkString)
  }
}
