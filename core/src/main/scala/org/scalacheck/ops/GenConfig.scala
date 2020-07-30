package org.scalacheck.ops

import org.scalacheck.Gen
import org.scalacheck.Gen.Parameters
import org.scalacheck.rng.Seed

final class GenConfig private (
  val seed: Seed,
  val params: Gen.Parameters,
  val retries: Int
) {

  private[this] def copy(
    seed: Seed = this.seed,
    params: Gen.Parameters = this.params,
    retries: Int = this.retries
  ): GenConfig = {
    new GenConfig(seed, params, retries)
  }

  def withSeed(seed: Seed): GenConfig = copy(seed = seed)
  def withParams(params: Gen.Parameters): GenConfig = copy(params = params)
  def withRetries(retries: Int): GenConfig = copy(retries = retries)
}

object GenConfig {

  def apply(params: Parameters, retries: Int, fallbackSeed: Seed): GenConfig = {
    new GenConfig(
      fallbackSeed,
      params,
      retries
    )
  }

  def apply(params: Parameters, retries: Int): GenConfig = GenConfig(params, retries, default.seed)

  def apply(params: Parameters): GenConfig = GenConfig(params, default.retries, default.seed)

  def apply(seed: Seed, maxSize: Int, retries: Int): GenConfig = {
    new GenConfig(seed, Gen.Parameters.default.withSize(maxSize), retries)
  }

  def apply(seed: Seed, maxSize: Int): GenConfig = {
    GenConfig(seed, maxSize, default.retries)
  }

  def apply(seed: Seed): GenConfig = {
    GenConfig(seed, default.params.size, default.retries)
  }

  val default: GenConfig = GenConfig(Seed(0), 6, 100)
}
