package com.example.luminacal.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luminacal.model.*
import com.example.luminacal.ui.screens.onboarding.*
import com.example.luminacal.ui.theme.LuminaCalTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI tests for Onboarding flow
 */
@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomePage_displaysCorrectTitle() {
        composeTestRule.setContent {
            LuminaCalTheme {
                WelcomePage()
            }
        }

        composeTestRule.onNodeWithText("Welcome to").assertIsDisplayed()
        composeTestRule.onNodeWithText("LuminaCal").assertIsDisplayed()
    }

    @Test
    fun welcomePage_displaysDescription() {
        composeTestRule.setContent {
            LuminaCalTheme {
                WelcomePage()
            }
        }

        composeTestRule.onNodeWithText("AI-powered", substring = true).assertExists()
    }

    @Test
    fun aboutYouPage_displaysGenderOptions() {
        composeTestRule.setContent {
            LuminaCalTheme {
                AboutYouPage(
                    gender = Gender.MALE,
                    onGenderChange = {},
                    age = 25,
                    onAgeChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Gender").assertExists()
        composeTestRule.onNodeWithText("Male").assertExists()
        composeTestRule.onNodeWithText("Female").assertExists()
    }

    @Test
    fun aboutYouPage_displaysAgeSlider() {
        composeTestRule.setContent {
            LuminaCalTheme {
                AboutYouPage(
                    gender = Gender.MALE,
                    onGenderChange = {},
                    age = 25,
                    onAgeChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Age", substring = true).assertExists()
        composeTestRule.onNodeWithText("25", substring = true).assertExists()
    }

    @Test
    fun aboutYouPage_genderSelectionCallsCallback() {
        var selectedGender = Gender.MALE

        composeTestRule.setContent {
            LuminaCalTheme {
                AboutYouPage(
                    gender = selectedGender,
                    onGenderChange = { selectedGender = it },
                    age = 25,
                    onAgeChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Female").performClick()
        
        assert(selectedGender == Gender.FEMALE)
    }

    @Test
    fun bodyMetricsPage_displaysWeightAndHeight() {
        composeTestRule.setContent {
            LuminaCalTheme {
                BodyMetricsPage(
                    weight = 70f,
                    onWeightChange = {},
                    height = 170f,
                    onHeightChange = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Body Metrics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Weight").assertExists()
        composeTestRule.onNodeWithText("70 kg").assertExists()
        composeTestRule.onNodeWithText("Height").assertExists()
        composeTestRule.onNodeWithText("170 cm").assertExists()
    }

    @Test
    fun bodyMetricsPage_displaysBMIFeedback() {
        composeTestRule.setContent {
            LuminaCalTheme {
                BodyMetricsPage(
                    weight = 70f,
                    onWeightChange = {},
                    height = 175f,
                    onHeightChange = {}
                )
            }
        }

        // BMI = 70 / (1.75)^2 = 22.86 (Normal)
        composeTestRule.onNodeWithText("Normal", substring = true).assertExists()
        composeTestRule.onNodeWithText("BMI", substring = true).assertExists()
    }

    @Test
    fun bodyMetricsPage_showsUnderweightStatus() {
        composeTestRule.setContent {
            LuminaCalTheme {
                BodyMetricsPage(
                    weight = 45f,
                    onWeightChange = {},
                    height = 175f,
                    onHeightChange = {}
                )
            }
        }

        // BMI = 45 / (1.75)^2 = 14.7 (Underweight)
        composeTestRule.onNodeWithText("Underweight", substring = true).assertExists()
    }

    @Test
    fun bodyMetricsPage_showsObeseStatus() {
        composeTestRule.setContent {
            LuminaCalTheme {
                BodyMetricsPage(
                    weight = 95f,
                    onWeightChange = {},
                    height = 175f,
                    onHeightChange = {}
                )
            }
        }

        // BMI = 95 / (1.75)^2 = 31.0 (Obese)
        composeTestRule.onNodeWithText("Obese", substring = true).assertExists()
    }

    @Test
    fun goalsPage_displaysActivityLevelSection() {
        composeTestRule.setContent {
            LuminaCalTheme {
                GoalsPage(
                    activityLevel = ActivityLevel.MODERATE,
                    onActivityChange = {},
                    fitnessGoal = FitnessGoal.MAINTAIN,
                    onGoalChange = {},
                    tdee = 2000
                )
            }
        }

        composeTestRule.onNodeWithText("Your Goals").assertIsDisplayed()
        composeTestRule.onNodeWithText("Activity Level").assertExists()
    }

    @Test
    fun goalsPage_displaysFitnessGoalSection() {
        composeTestRule.setContent {
            LuminaCalTheme {
                GoalsPage(
                    activityLevel = ActivityLevel.MODERATE,
                    onActivityChange = {},
                    fitnessGoal = FitnessGoal.MAINTAIN,
                    onGoalChange = {},
                    tdee = 2000
                )
            }
        }

        composeTestRule.onNodeWithText("Fitness Goal").assertExists()
    }

    @Test
    fun goalsPage_displaysTDEEPreview() {
        composeTestRule.setContent {
            LuminaCalTheme {
                GoalsPage(
                    activityLevel = ActivityLevel.MODERATE,
                    onActivityChange = {},
                    fitnessGoal = FitnessGoal.MAINTAIN,
                    onGoalChange = {},
                    tdee = 2500
                )
            }
        }

        composeTestRule.onNodeWithText("Your Daily Target").assertExists()
        composeTestRule.onNodeWithText("2500").assertExists()
    }

    @Test
    fun onboardingScreen_displaysNextButton() {
        composeTestRule.setContent {
            LuminaCalTheme {
                OnboardingScreen(
                    onComplete = {},
                    onSkip = {}
                )
            }
        }

        // Welcome page should have Next button
        composeTestRule.onNodeWithText("Next").assertExists()
    }

    @Test
    fun onboardingScreen_displaysSkipButton() {
        composeTestRule.setContent {
            LuminaCalTheme {
                OnboardingScreen(
                    onComplete = {},
                    onSkip = {}
                )
            }
        }

        // Skip button visible on first page
        composeTestRule.onNodeWithText("Skip").assertExists()
    }

    @Test
    fun onboardingScreen_showsWelcomePage() {
        composeTestRule.setContent {
            LuminaCalTheme {
                OnboardingScreen(
                    onComplete = {},
                    onSkip = {}
                )
            }
        }

        composeTestRule.onNodeWithText("LuminaCal").assertIsDisplayed()
    }

    @Test
    fun onboardingScreen_navigatesToNextPage() {
        composeTestRule.setContent {
            LuminaCalTheme {
                OnboardingScreen(
                    onComplete = {},
                    onSkip = {}
                )
            }
        }

        // Click Next to go to About You page
        composeTestRule.onNodeWithText("Next").performClick()
        
        // Should now be on About You page
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tell us about you").assertExists()
    }
}
