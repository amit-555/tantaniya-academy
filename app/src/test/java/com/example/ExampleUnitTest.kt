package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testNegativeMarkingCalculation() {
    val correctCount = 8
    val incorrectCount = 2
    val negativeMarkingRate = 0.25

    val positiveMarks = correctCount * 1.0
    val negativeMarks = incorrectCount * negativeMarkingRate
    val finalScore = (positiveMarks - negativeMarks).coerceAtLeast(0.0)

    assertEquals(8.0, positiveMarks, 0.001)
    assertEquals(0.5, negativeMarks, 0.001)
    assertEquals(7.5, finalScore, 0.001)
  }

  @Test
  fun testConstable200QuestionsDistribution() {
    val questions = com.example.data.database.Constable200QuestionsGenerator.generateAll200Questions()
    assertEquals(200, questions.size)

    val reasoningCount = questions.count { it.subject == "રીઝનિંગ" }
    val mathCount = questions.count { it.subject == "ગણિત" }
    val constitutionCount = questions.count { it.subject == "ભારતીય બંધારણ" }
    val historyGeoCount = questions.count { it.subject == "ગુજરાત ઇતિહાસ & ભૂગોળ" }
    val scienceCount = questions.count { it.subject == "સામાન્ય વિજ્ઞાન" }
    val caCount = questions.count { it.subject == "કરંટ અફેર્સ" }
    val gkCount = questions.count { it.subject == "જનરલ નોલેજ (GK)" }

    assertEquals(30, reasoningCount)
    assertEquals(30, mathCount)
    assertEquals(30, constitutionCount)
    assertEquals(40, historyGeoCount)
    assertEquals(20, scienceCount)
    assertEquals(20, caCount)
    assertEquals(30, gkCount)

    assertTrue(questions.all { it.correctOption in listOf("A", "B", "C", "D") })
    assertTrue(questions.all { it.questionText.isNotBlank() })
    assertTrue(questions.all { it.optionA.isNotBlank() && it.optionB.isNotBlank() })
  }

  @Test
  fun testPrimaryAdminAuthConfig() {
    val primaryEmail = "gangalamit005@gmail.com"
    assertTrue(com.example.data.repository.AuthConfig.isPrimaryAdmin(primaryEmail))
    assertTrue(com.example.data.repository.AuthConfig.isPrimaryAdmin("GANGALAMIT005@GMAIL.COM"))
    assertFalse(com.example.data.repository.AuthConfig.isPrimaryAdmin("student@studypro.in"))
    assertFalse(com.example.data.repository.AuthConfig.isPrimaryAdmin("hacker_admin@yahoo.com"))

    val adminUser = com.example.data.entity.UserEntity(
      id = 1,
      name = "Amit Gangal",
      email = primaryEmail,
      password = "pass",
      role = "ADMIN"
    )
    val normalUser = com.example.data.entity.UserEntity(
      id = 2,
      name = "Normal Student",
      email = "student@studypro.in",
      password = "pass",
      role = "USER"
    )

    assertTrue(com.example.data.repository.AuthConfig.isAuthorizedAdmin(adminUser))
    assertFalse(com.example.data.repository.AuthConfig.isAuthorizedAdmin(normalUser))
    assertFalse(com.example.data.repository.AuthConfig.isAuthorizedAdmin(null))
  }
}
