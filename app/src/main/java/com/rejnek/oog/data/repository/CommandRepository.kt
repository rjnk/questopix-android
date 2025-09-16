package com.rejnek.oog.data.repository

import com.rejnek.oog.engine.commands.GenericCommandFactory
import com.rejnek.oog.engine.commands.callback.ButtonFactory
import com.rejnek.oog.engine.commands.callback.MultiChoiceFactory
import com.rejnek.oog.engine.commands.callback.QuestionFactory
import com.rejnek.oog.engine.commands.direct.factory.BoardFactory
import com.rejnek.oog.engine.commands.direct.factory.DistanceFactory
import com.rejnek.oog.engine.commands.direct.factory.FinishGameButtonFactory
import com.rejnek.oog.engine.commands.direct.factory.HeadingFactory
import com.rejnek.oog.engine.commands.direct.factory.ImageFactory
import com.rejnek.oog.engine.commands.direct.factory.PopUpFactory
import com.rejnek.oog.engine.commands.direct.factory.ShareButtonFactory
import com.rejnek.oog.engine.commands.direct.factory.ShowAllImagesFactory
import com.rejnek.oog.engine.commands.direct.factory.SimpleMapFactory
import com.rejnek.oog.engine.commands.direct.factory.TakePictureFactory
import com.rejnek.oog.engine.commands.direct.factory.TextFactory
import com.rejnek.oog.engine.commands.direct.simple.DebugPrint
import com.rejnek.oog.engine.commands.direct.simple.Refresh
import com.rejnek.oog.engine.commands.direct.simple.Save


/**
 * Repository responsible for managing game item factories
 */
class CommandRepository {

    val gameItems = arrayListOf<GenericCommandFactory>(
        DebugPrint(),
        QuestionFactory(),
        MultiChoiceFactory(),
        ButtonFactory(),
        TextFactory(),
        HeadingFactory(),
        DistanceFactory(),
        ImageFactory(),
        TakePictureFactory(),
        PopUpFactory(),
        FinishGameButtonFactory(),
        SimpleMapFactory(),
        BoardFactory(),
        ShowAllImagesFactory(),
        ShareButtonFactory(),
        Refresh(),
        Save()
    )

    fun getGameItemFactories(): List<GenericCommandFactory> {
        return gameItems
    }
}
