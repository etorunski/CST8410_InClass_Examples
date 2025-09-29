package com.algonquincollege.torunse

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
            DisplayLighting(55.7f)
        }
        Thread.sleep(200)
        composeTestRule.onNodeWithText("Your test tag").assertExists()
        composeTestRule.onNodeWithText("Your test tag").performTextInput("This gets typed in")
        composeTestRule.onNodeWithText("Your test tag").performClick()
        composeTestRule.onNodeWithText("Your test tag").assertTextContains("This gets typed in")
        composeTestRule.onNodeWithText("Your test tag").assertDoesNotExist()

    }
}