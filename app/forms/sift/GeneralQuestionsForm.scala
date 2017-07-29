/*
 * Copyright 2017 HM Revenue & Customs
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

package forms.sift

import connectors.exchange.sift.{ GeneralQuestionsAnswers, PostGradDegreeInfoAnswers, UndergradDegreeInfoAnswers }
import play.api.data.Forms._
import play.api.data.{ Form, FormError }
import play.api.data.format.Formatter

object UndergradDegreeInfoForm {

  val form = Form(
    mapping("name" -> text,
      "classification" -> text,
      "graduationYear" -> text,
      "moduleDetails" -> text
  )(UndergradDegreeInfoAnswers.apply)(UndergradDegreeInfoAnswers.unapply))

  val Classifications = Seq(
    "First-class honours",
    "Upper second-class honours",
    "Lower second-class honours",
    "Third-class honours",
    "Ordinary degree"
  )
}

object PostGradDegreeInfoForm {
   val form = Form(
    mapping("name" -> text,
      "graduationYear" -> text,
      "otherDetails" -> text,
      "projectDetails" -> text
  )(PostGradDegreeInfoAnswers.apply)(PostGradDegreeInfoAnswers.unapply))
}


object GeneralQuestionsForm {
  val form = Form(
    mapping("multiplePassports" -> boolean,
      "passportCountry" -> text,
      "undergradDegree" -> of(undergradDegreeInfoFormFormatter),
      "postgradDegree" -> of(postGradDegreeInfoFormFormatter)
  )(GeneralQuestionsAnswers.apply)(GeneralQuestionsAnswers.unapply))

  def undergradDegreeInfoFormFormatter = new Formatter[Option[UndergradDegreeInfoAnswers]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[UndergradDegreeInfoAnswers]] = {
        UndergradDegreeInfoForm.form.mapping.bind(data) match {
          case Right(success) => Right(Some(success))
          case Left(error) => Left(error)
        }
      }

    override def unbind(key: String, fastPassData: Option[UndergradDegreeInfoAnswers]): Map[String, String] =
      fastPassData.map(fpd => UndergradDegreeInfoForm.form.fill(fpd).data).getOrElse(Map(key -> ""))
  }

  def postGradDegreeInfoFormFormatter = new Formatter[Option[PostGradDegreeInfoAnswers]] {
    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Option[PostGradDegreeInfoAnswers]] = {
        PostGradDegreeInfoForm.form.mapping.bind(data) match {
          case Right(success) => Right(Some(success))
          case Left(error) => Left(error)
        }
      }

    override def unbind(key: String, fastPassData: Option[PostGradDegreeInfoAnswers]): Map[String, String] =
      fastPassData.map(fpd => PostGradDegreeInfoForm.form.fill(fpd).data).getOrElse(Map(key -> ""))
  }

   val Countries = Seq(
   "Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antigua & Deps", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan",

   "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bhutan", "Bolivia", "Bosnia Herzegovina",
   "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina", "Burundi",

   "Cambodia", "Cameroon", "Canada", "Cape Verde", "Central African Rep", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo",
   "Congo (Democratic Rep)", "Costa Rica", "Croatia", "Cuba", "Cyprus", "Czech Republic",

   "Denmark", "Djibouti", "Dominica", "Dominican Republic",

   "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia",

   "Fiji", "Finland", "France",

   "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",

   "Haiti", "Honduras", "Hungary",

   "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland (Republic)", "Israel", "Italy", "Ivory Coast",

   "Jamaica", "Japan", "Jordan",

   "Kazakhstan", "Kenya", "Kiribati", "Korea North", "Korea South", "Kosovo", "Kuwait", "Kyrgyzstan",

   "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg",

   "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico",
   "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Morocco", "Mozambique", "Myanmar, (Burma)",

   "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway",

   "Oman",

   "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal",

   "Qatar",

   "Romania", "Russian Federation", "Rwanda",

   "St Kitts & Nevis", "St Lucia", "Saint Vincent & the Grenad", "Samoa", "San Marino", "Sao Tome & Principe", "Saudi Arabia", "Senegal",
   "Serbia", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Sudan",
   "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria",

   "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad & Tobago", "Tunisia", "Turkey", "Turkmenistan", "Tuvalu",

   "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan",

   "Vanuatu", "Vatican City", "Venezuela", "Vietnam",

   "Yemen",

   "Zambia", "Zimbabwe"
  )
}
