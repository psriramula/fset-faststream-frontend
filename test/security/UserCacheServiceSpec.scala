/*
 * Copyright 2016 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package security

import java.util.UUID

import config.{ CSRCache, SecurityEnvironmentImpl }
import connectors.ApplicationClient.ApplicationNotFound
import connectors.UserManagementClient.InvalidCredentialsException
import connectors.exchange.{ ApplicationResponse, ProgressResponse, UserResponse }
import connectors.{ ApplicationClient, UserManagementClient }
import controllers.BaseSpec
import models.ApplicationData.ApplicationStatus
import models._
import org.mockito.Matchers.{ eq => eqTo, _ }
import org.mockito.Mockito._
import org.scalatest.MustMatchers
import org.scalatest.concurrent.ScalaFutures
import play.api.libs.json.{ JsString, Reads }
import play.api.mvc.Request
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.cache.client.KeyStoreEntryValidationException
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.language.postfixOps

class UserCacheServiceSpec extends BaseSpec with MustMatchers with ScalaFutures {

  "refreshCachedUser" should {
    "return a user and application CachedData when both are found" in new TestFixture {
      val result = userAndApplicationCacheService.refreshCachedUser(testUserId).futureValue

      result mustBe an[CachedData]
      result.user mustBe expectedCachedDataUser
      result.application mustBe Some(expectedCachedDataApplication)
    }

    "return a user only when no application is found" in new TestFixture {
      val result = userOnlyCacheService.refreshCachedUser(testUserId).futureValue

      result mustBe an[CachedData]
      result.application mustBe None
      result.user mustBe expectedCachedDataUser
    }

    "throw an Invalid Credentials exception when no user is found" in new TestFixture {
      val result = noUserCacheService.refreshCachedUser(testUserId).failed.futureValue

      result mustBe an[InvalidCredentialsException]
    }
  }

  trait TestFixture {

    val testUserId = UniqueIdentifier(UUID.randomUUID())
    val testApplicationId = UniqueIdentifier(UUID.randomUUID())
    val testUserResponse = UserResponse(
      "Barry",
      "Smith",
      Some("Barry"),
      isActive = true,
      testUserId,
      "barry@smith.com",
      "UNLOCKED",
      "candidate"
    )
    val testApplicationResponse = ApplicationResponse(
      testApplicationId,
      "SUBMITTED",
      Some(ApplicationRoutes.FASTSTREAM.toString),
      testUserId,
      ProgressResponse(testApplicationId.toString()),
      None
    )
    val expectedCachedDataUser = CachedUser(
      testUserId,
      testUserResponse.firstName,
      testUserResponse.lastName,
      testUserResponse.preferredName,
      testUserResponse.email,
      testUserResponse.isActive,
      testUserResponse.lockStatus
    )
    val expectedCachedDataApplication = ApplicationData(
      testApplicationId,
      testUserId,
      ApplicationStatus.withName(testApplicationResponse.applicationStatus),
      ApplicationRoutes.FASTSTREAM,
      Progress.fromProgressRespToAppProgress(testApplicationResponse.progressResponse),
      None
    )

    implicit val request = FakeRequest(GET, "")

    implicit val hc = new HeaderCarrier()

    val mockApplicationClient = mock[ApplicationClient]
    val mockUserManagementClient = mock[UserManagementClient]

    lazy val noUserCacheService = makeUserCacheService {
      when(mockUserManagementClient.findByUserId(any[UniqueIdentifier]())(any[HeaderCarrier]())).thenReturn(Future.failed(
        new InvalidCredentialsException()
      ))
    }

    lazy val userOnlyCacheService = makeUserCacheService {
      when(mockUserManagementClient.findByUserId(any[UniqueIdentifier]())(any[HeaderCarrier]())).thenReturn(Future.successful(
        testUserResponse
      ))
      when(mockApplicationClient.findApplication(any[UniqueIdentifier](), any[String]())(any[HeaderCarrier]())).thenReturn(Future.failed(
        new ApplicationNotFound()
      ))
    }

    lazy val userAndApplicationCacheService = makeUserCacheService {
      when(mockUserManagementClient.findByUserId(any[UniqueIdentifier]())(any[HeaderCarrier]())).thenReturn(Future.successful(
        testUserResponse
      ))
      when(mockApplicationClient.findApplication(any[UniqueIdentifier](), any[String]())(any[HeaderCarrier]())).thenReturn(Future.successful(
        testApplicationResponse
      ))
    }

    def makeUserCacheService(mockSetup: => Unit): UserCacheService = {
      mockSetup
      new UserCacheService(mockApplicationClient, mockUserManagementClient) {
        override def save(user: CachedData)(implicit hc: HeaderCarrier): Future[CachedData] = Future.successful(user)
      }
    }
  }
}