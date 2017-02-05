package io.vulpine.lib.catcher;

import io.vulpine.lib.jcfi.CheckedSupplier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Catcher
{
  /**
   * Returns either the result of the given {@link java.util.function.Supplier},
   * or the result of the fallback {@link Function}
   *
   * @param func     Supplier to attempt
   * @param fallback Error Handler/Default value supplier
   *
   * @param <R> Return type of the given Supplier
   *
   * @return Either the result of the supplier or the fallback function.
   */
  public static < R > R call(
    CheckedSupplier < R > func,
    Function < Exception, R > fallback
  ) {
    Objects.requireNonNull(fallback);

    try {
      return func.get();
    } catch ( Exception e ) {
      return fallback.apply(e);
    }
  }

  /**
   * Retrieve from checked.
   *
   * Returns either the result of the given {@link CheckedSupplier}, or the
   * result of the given fallback {@link Supplier}.
   *
   * If an exception is thrown by the {@link CheckedSupplier}, the given handler
   * will be called with the thrown {@link Exception} as it's parameter.
   *
   * @param supplier Value supplier
   * @param handler  Exception handler
   * @param fallback Fallback value supplier
   *
   * @param <R> Supplier return type.
   *
   * @return Either the result of the {@link CheckedSupplier} or the fallback
   *         {@link Supplier}.
   */
  public static < R > R call(
    CheckedSupplier < R > supplier,
    Consumer < Exception > handler,
    Supplier < R > fallback
  ) {
    Objects.requireNonNull(handler);
    Objects.requireNonNull(fallback);

    try {
      return supplier.get();
    } catch ( Exception e ) {
      handler.accept(e);
      return fallback.get();
    }
  }


  /**
   * Creates a result chain with the given {@link CheckedSupplier} as the start.
   *
   * @param sup Checked value supplier
   *
   * @param <R> Supplier result type.
   *
   * @return Result Chain of the type returned by the given supplier.
   */
  public static < R > Chain < R > with( final CheckedSupplier < R > sup )
  {

    try {
      return new Chain <> (sup.get(), null, null);
    } catch ( final Exception e ) {
      return new Chain <> (null, null, e);
    }

  }
}
