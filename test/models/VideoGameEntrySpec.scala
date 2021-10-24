package models

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.Json

class VideoGameEntrySpec extends PlaySpec {

  "VideoGameEntrySpec" should {
    "create entry from json using fromJson" in {
      val json = Json.parse("{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"}")
      val Some(result) = VideoGameEntry.fromJson(json)
      result.id mustEqual 5345325
      result.title mustEqual "Some Game"
      result.genre mustEqual "Adventure"
      result.description mustEqual "Just some game."
      result.releaseDate mustEqual "2021-02-17"
    }

    "create entry from apply" in {
      val result = VideoGameEntry.apply(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      result.id mustEqual 5345325
      result.title mustEqual "Some Game"
      result.genre mustEqual "Adventure"
      result.description mustEqual "Just some game."
      result.releaseDate mustEqual "2021-02-17"
    }

    "create json from single entry by using toJson" in {
      val entry = VideoGameEntry(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      val expected = Json.parse("{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"}")
      val result = VideoGameEntry.toJson(entry)
      result mustEqual expected
    }

    "create json from multiple entries by using toJson" in {
      val entries = Array(
        VideoGameEntry(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17"),
        VideoGameEntry(3484504, "Another Game", "Adventure", "Just another game to play.", "2020-07-11"),
      )
      val expected = Json.parse("[{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"},{\"id\":3484504," +
        "\"title\":\"Another Game\",\"genre\":\"Adventure\",\"description\":\"Just another game to play.\"," +
        "\"releaseDate\":\"2020-07-11\"}]")
      val result = VideoGameEntry.toJson(entries.toIndexedSeq)
      result mustEqual expected
    }

    "create tuple from entry with unapply" in {
      val entry = VideoGameEntry(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17")
      val Some(result) = VideoGameEntry.unapply(entry)
      result._1 mustEqual 5345325
      result._2 mustEqual "Some Game"
      result._3 mustEqual "Adventure"
      result._4 mustEqual "Just some game."
      result._5 mustEqual "2021-02-17"
    }

    "create entries from json using fromJsonArray" in {
      val json = Json.parse("[{\"id\":5345325,\"title\":\"Some Game\",\"genre\":\"Adventure\"," +
        "\"description\":\"Just some game.\",\"releaseDate\":\"2021-02-17\"},{\"id\":3484504," +
        "\"title\":\"Another Game\",\"genre\":\"Adventure\",\"description\":\"Just another game to play.\"," +
        "\"releaseDate\":\"2020-07-11\"}]")
      val expected = Array(
        VideoGameEntry(5345325, "Some Game", "Adventure", "Just some game.", "2021-02-17"),
        VideoGameEntry(3484504, "Another Game", "Adventure", "Just another game to play.", "2020-07-11"),
      )
      val Some(result) = VideoGameEntry.fromJsonArray(json)
      result mustEqual expected
    }
  }
}
