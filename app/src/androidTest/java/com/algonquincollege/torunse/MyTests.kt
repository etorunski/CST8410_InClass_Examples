package com.algonquincollege.torunse

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTests {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun testThisComposable()
    {


        composeTestRule.setContent {

            //get the database:
            val db = Room.databaseBuilder(LocalContext.current, ItemDatabase::class.java, "TheFilename.db").build()
            //retrieve the DAO:
            val mDAO = db.getMyDAO()

            ListItems(mod = Modifier.padding(5.dp), mDAO)
        }
        Thread.sleep(200)
        val node = composeTestRule.onNodeWithTag("Input")
        node.performTextInput("MyFirstMessage")

        val addNode = composeTestRule.onNodeWithTag("add")
        addNode.performClick()

        //wait for the UI to update before continuing the tests:
        composeTestRule.mainClock.autoAdvance = true // Default
        composeTestRule.waitForIdle() // Advances the clock until Compose is idle.

        val insertedNode = composeTestRule.onNodeWithTag("item0", useUnmergedTree = true)  //.onNodeWithText("MyFirstMessage", true)

        insertedNode.assertExists()


//        composeTestRule.onNodeWithText("Your test tag").assertTextContains("This gets typed in")
 //       composeTestRule.onNodeWithText("Your test tag").assertDoesNotExist()

    }
}