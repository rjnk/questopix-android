package com.rejnek.oog.data.repository

import com.rejnek.oog.data.gameItems.GenericItemFactory
import com.rejnek.oog.data.gameItems.callback.ButtonFactory
import com.rejnek.oog.data.gameItems.callback.MultiChoiceFactory
import com.rejnek.oog.data.gameItems.direct.commands.DebugPrint
import com.rejnek.oog.data.gameItems.direct.factory.HeadingFactory
import com.rejnek.oog.data.gameItems.callback.QuestionFactory
import com.rejnek.oog.data.gameItems.direct.commands.Refresh
import com.rejnek.oog.data.gameItems.direct.commands.Save
import com.rejnek.oog.data.gameItems.direct.factory.DistanceFactory
import com.rejnek.oog.data.gameItems.direct.factory.FinishGameButtonFactory
import com.rejnek.oog.data.gameItems.direct.factory.ImageFactory
import com.rejnek.oog.data.gameItems.direct.factory.SimpleMapFactory
import com.rejnek.oog.data.gameItems.direct.factory.TakePictureFactory
import com.rejnek.oog.data.gameItems.direct.factory.TextFactory

/**
 * Repository responsible for managing game item factories
 */
class GameItemRepository {

    val gameItems = arrayListOf<GenericItemFactory>(
        DebugPrint(),
        QuestionFactory(),
        MultiChoiceFactory(),
        ButtonFactory(),
        TextFactory(),
        HeadingFactory(),
        DistanceFactory(),
        ImageFactory(),
        TakePictureFactory(),
        FinishGameButtonFactory(),
        SimpleMapFactory(),
        Refresh(),
        Save()
    )

    fun getGameItemFactories(): List<GenericItemFactory> {
        return gameItems
    }
}
