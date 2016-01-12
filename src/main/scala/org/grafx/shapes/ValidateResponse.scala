package org.grafx.shapes

case class ValidateResponse(valid: Boolean,
                           location: String,
                           internationalNumber: String,
                           internationalCallingCode: String,
                           numberType: String,
                           isMobile: Boolean,
                           localNumber: String,
                           countryCode: String)