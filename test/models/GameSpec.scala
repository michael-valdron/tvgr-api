package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class GameSpec extends PlaySpec {

  "fromMap" should {
    "create entry from Map" in {
      val map = Map(
        "id" -> 5345325,
        "title" -> "Some Game",
        "genre" -> "Adventure",
        "description" -> "Just some game.",
        "releaseDate" -> "2021-02-17"
      )
      val result = Game.fromMap(map)
      result.id mustEqual map("id")
      result.title mustEqual map("title")
      result.genre mustEqual map("genre")
      result.description mustEqual map("description")
      result.releaseDate mustEqual map("releaseDate")
    }
  }

  "toMap" should {
    "create Map from entry" in {
      val entry = Game(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      val result = Game.toMap(entry)
      result("id") mustEqual f"${entry.id}"
      result("title") mustEqual entry.title
      result("genre") mustEqual entry.genre
      result("description") mustEqual entry.description
      result("releaseDate") mustEqual entry.releaseDate
    }
  }

  "fromJson" should {
    "create entry from json" in {
      val json = Json.parse("{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"}")
      val Some(result) = Game.fromJson(json)
      result.id mustEqual 5345325
      result.title mustEqual "Some Game"
      result.genre mustEqual "Adventure"
      result.description mustEqual "Just some game."
      result.releaseDate mustEqual "2021-02-17"
    }
  }

  "apply" should {
    "create entry" in {
      val result = Game.apply(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      result.id mustEqual 5345325
      result.title mustEqual "Some Game"
      result.genre mustEqual "Adventure"
      result.description mustEqual "Just some game."
      result.releaseDate mustEqual "2021-02-17"
    }
  }

  "toJson" should {
    "create json from single entry" in {
      val entry = Game(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      val expected = Json.parse("{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"}")
      val result = Game.toJson(entry)
      result mustEqual expected
    }

    "create json from multiple entries" in {
      val entries = Array(
        Game(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17"),
        Game(3484504, "Another Game", "Adventure", "Just another game to play.", "2020-07-11"),
      )
      val expected = Json.parse("[{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"},{\"id\":3484504," +
        "\"title\":\"Another Game\",\"genre\":\"Adventure\",\"description\":\"Just another game to play.\"," +
        "\"releaseDate\":\"2020-07-11\"}]")
      val result = Game.toJson(entries.toIndexedSeq)
      result mustEqual expected
    }
  }

  "unapply" should {
    "create tuple from entry" in {
      val entry = Game(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      val Some(result) = Game.unapply(entry)
      result._1 mustEqual 5345325
      result._2 mustEqual "Some Game"
      result._3 mustEqual "Adventure"
      result._4 mustEqual "Just some game."
      result._5 mustEqual "2021-02-17"
    }
  }

  "fromJsonArray" should {
    "create entries from json" in {
      val json = Json.parse("[{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"},{\"id\":3484504," +
        "\"title\":\"Another Game\",\"genre\":\"Adventure\",\"description\":\"Just another game to play.\"," +
        "\"releaseDate\":\"2020-07-11\"}]")
      val expected = Array(
        Game(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17"),
        Game(3484504, "Another Game", "Adventure", "Just another game to play.", "2020-07-11"),
      )
      val Some(result) = Game.fromJsonArray(json)
      result mustEqual expected
    }
  }
}
