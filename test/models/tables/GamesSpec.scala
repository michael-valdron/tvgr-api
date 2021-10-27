package models.tables

import models.Game
import org.scalatestplus.play.PlaySpec
import slick.jdbc.H2Profile.api._

final class GamesSpec extends PlaySpec {
  "testFilterById" should {
    "create filter by id query entity for finding record '243425' in 'games' relation" in {
      val entryId = 243425
      val result = Games.filterById(entryId)
      assert(result.isInstanceOf[Query[Games, Game, Seq]])
    }
  }
}
