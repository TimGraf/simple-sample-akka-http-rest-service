package org.grafx.utils.cats

import cats.data.Xor
import cats.data.Xor._

package object xor {

  type Or[L, R] = Xor[L, R]
  type Bad[L] = Left[L]
  type Good[R] = Right[R]

  object Bad {
    def apply[L](left: L): L Or Nothing = Left(left)
    def unapply[L, R](or: L Or R): Option[L] = or.swap.toOption
  }

  object Good {
    def apply[R](right: R): Nothing Or R = Right(right)
    def unapply[L, R](or: L Or R): Option[R] = or.toOption
  }
}