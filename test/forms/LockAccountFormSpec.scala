/*
 * Copyright 2019 HM Revenue & Customs
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

package forms

import controllers.UnitSpec
import testkit.UnitWithAppSpec

class LockAccountFormSpec extends UnitWithAppSpec {

  import LockAccountForm.{ form => lockAccountForm }

  "Lock account form" should {
    "be valid for non empty email" in {
      val form = lockAccountForm.bind(Map("email" -> "testemail987@mailinator.com"))
      form.hasErrors must be(false)
      form.hasGlobalErrors must be(false)
    }

    "be invalid for empty email" in {
      val form = lockAccountForm.bind(Map("email" -> ""))
      form.hasErrors must be(true)
      form.hasGlobalErrors must be(false)
    }

    "be invalid for invalid email" in {
      val form = lockAccountForm.bind(Map("email" -> "ABCDEFG"))
      form.hasErrors must be(true)
      form.hasGlobalErrors must be(false)
    }
  }
}
