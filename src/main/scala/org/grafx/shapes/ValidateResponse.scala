package org.grafx.shapes

case class ValidateResponse(valid: Boolean,
                           location: Option[String],
                           internationalNumber: Option[String],
                           internationalCallingCode: Option[String],
                           numberType: String,
                           isMobile: Option[Boolean],
                           localNumber: Option[String],
                           countryCode: Option[String])