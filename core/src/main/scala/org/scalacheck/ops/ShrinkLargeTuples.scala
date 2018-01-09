package org.scalacheck.ops

import org.scalacheck.Shrink
import org.scalacheck.Shrink.shrink

// This is copy-pasta from scalacheck and is checked at compile-time. Skipping tests for this.
// $COVERAGE-OFF$
trait ShrinkLargeTuples {

  implicit def shrinkTuple10[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _))
  }

  implicit def shrinkTuple11[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _))
  }

  implicit def shrinkTuple12[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _))
  }

  implicit def shrinkTuple13[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _))
  }

  implicit def shrinkTuple14[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _))
  }

  implicit def shrinkTuple15[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _))
  }

  implicit def shrinkTuple16[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _))
  }

  implicit def shrinkTuple17[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _))
  }

  implicit def shrinkTuple18[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink, R: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q, r)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q, r)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q, r)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q, r)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q, r)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q, r)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q, r)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q, r)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q, r)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q, r)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _, r)) append
      shrink(r).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, _))
  }

  implicit def shrinkTuple19[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink, R: Shrink, S: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q, r, s)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q, r, s)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q, r, s)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q, r, s)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q, r, s)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q, r, s)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q, r, s)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q, r, s)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q, r, s)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _, r, s)) append
      shrink(r).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, _, s)) append
      shrink(s).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, _))
  }

  implicit def shrinkTuple20[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink, R: Shrink, S: Shrink, T: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q, r, s, t)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q, r, s, t)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q, r, s, t)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q, r, s, t)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q, r, s, t)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q, r, s, t)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q, r, s, t)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q, r, s, t)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _, r, s, t)) append
      shrink(r).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, _, s, t)) append
      shrink(s).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, _, t)) append
      shrink(t).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, _))
  }

  implicit def shrinkTuple21[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink, R: Shrink, S: Shrink, T: Shrink,
  U: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q, r, s, t, u)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q, r, s, t, u)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q, r, s, t, u)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q, r, s, t, u)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q, r, s, t, u)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q, r, s, t, u)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q, r, s, t, u)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _, r, s, t, u)) append
      shrink(r).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, _, s, t, u)) append
      shrink(s).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, _, t, u)) append
      shrink(t).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, _, u)) append
      shrink(u).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, _))
  }

  implicit def shrinkTuple22[
  A: Shrink, B: Shrink, C: Shrink, D: Shrink, E: Shrink,
  F: Shrink, G: Shrink, H: Shrink, I: Shrink, J: Shrink,
  K: Shrink, L: Shrink, M: Shrink, N: Shrink, O: Shrink,
  P: Shrink, Q: Shrink, R: Shrink, S: Shrink, T: Shrink,
  U: Shrink, V: Shrink
  ]: Shrink[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V)] = Shrink {
    case (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) =>
      shrink(a).map((_, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(b).map((a, _, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(c).map((a, b, _, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(d).map((a, b, c, _, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(e).map((a, b, c, d, _, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(f).map((a, b, c, d, e, _, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(g).map((a, b, c, d, e, f, _, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(h).map((a, b, c, d, e, f, g, _, i, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(i).map((a, b, c, d, e, f, g, h, _, j, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(j).map((a, b, c, d, e, f, g, h, i, _, k, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(k).map((a, b, c, d, e, f, g, h, i, j, _, l, m, n, o, p, q, r, s, t, u, v)) append
      shrink(l).map((a, b, c, d, e, f, g, h, i, j, k, _, m, n, o, p, q, r, s, t, u, v)) append
      shrink(m).map((a, b, c, d, e, f, g, h, i, j, k, l, _, n, o, p, q, r, s, t, u, v)) append
      shrink(n).map((a, b, c, d, e, f, g, h, i, j, k, l, m, _, o, p, q, r, s, t, u, v)) append
      shrink(o).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, _, p, q, r, s, t, u, v)) append
      shrink(p).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, _, q, r, s, t, u, v)) append
      shrink(q).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, _, r, s, t, u, v)) append
      shrink(r).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, _, s, t, u, v)) append
      shrink(s).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, _, t, u, v)) append
      shrink(t).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, _, u, v)) append
      shrink(u).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, _, v)) append
      shrink(v).map((a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, _))
  }
}
// $COVERAGE-ON$
