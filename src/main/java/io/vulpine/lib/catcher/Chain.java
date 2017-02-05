package io.vulpine.lib.catcher;

import io.vulpine.lib.jcfi.CheckedFunction;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Chain < T >
{
  private final T value;

  private final Consumer < ? super Exception > handler;

  private final Exception exception;

  public Chain(
    final T value,
    final Consumer < ? super Exception > handler,
    final Exception exception
  )
  {
    this.value = value;
    this.handler = handler;
    this.exception = exception;
  }

  /**
   * Shows whether or not the current Chain value is empty.
   *
   * @see #present() Inverse shortcut method showing if value is non-empty
   *
   * @return if the Chain is empty
   */
  public boolean empty()
  {
    return value == null;
  }

  /**
   * Shows whether or not the current Chain value is non-empty
   *
   * @see #empty() Inverse shortcut method showing if value is empty
   *
   * @return if the Chain holds a value.
   */
  public boolean present()
  {
    return value != null;
  }

  /**
   * Return the contained possible value as a Java option type.
   *
   * @return Possibly empty option representing this Chain's current value.
   */
  public Optional < T > asOptional()
  {
    return Optional.ofNullable(value);
  }

  /**
   * Gets the currently contained value.
   *
   * @return The value currently contained in this Chain
   *
   * @throws RuntimeException thrown if there is no value present.  To prevent
   *   this, consider checking against the {@link #empty()} or
   *   {@link #present()} methods to verify that this chain currently contains
   *   an available value.
   */
  public T get() throws RuntimeException
  {
    if ( value == null ) {
      throw new RuntimeException("Get attempted on an empty Catcher result.");
    }

    return value;
  }

  /**
   * Returns the current value or the given alternative if no value is present.
   *
   * @param alternative Alternative value to returned in the event that this
   *                    chain currently contains no value.
   *
   * @return current T or given alternative T
   */
  public T orElse( final T alternative )
  {
    return value == null ? alternative : value;
  }

  /**
   * Returns the current value or the result of the given supplier if no value
   * is present.
   *
   * @param supplier Supplier of an alternative value to be returned in the case
   *                 where this Chain contains no value.
   *
   * @return current T or result of given T supplier.
   */
  public T orElse( final Supplier < T > supplier )
  {
    return value == null ? supplier.get() : value;
  }

  /**
   * Returns the current value or throws the result of the given {@link Supplier}.
   *
   * @param supplier Exception supplier
   *
   * @param < R > Capture type of the expected thrown Exception
   *
   * @return current value if present.
   *
   * @throws R the result of calling supplier.{@link #get()}
   */
  public < R extends Exception > T orElseThrow( final Supplier < R > supplier )
  throws R
  {
    throw supplier.get();
  }

  /**
   * Applies the current value to the given method.
   *
   * @param step function used to transform the current value (if any)
   *
   * @param <R> Transformed type returned from the given method after the Chain
   *            value is applied.
   *
   * @return A new Chain of the return type of the given function.
   */
  public < R > Chain < R > apply( final CheckedFunction < T, R > step )
  {
    // No value, just pass through
    if ( value == null ) {
      return new Chain <>(null, handler, exception);
    }

    try {

      return new Chain <>(step.apply(value), handler, null);

    } catch ( final Exception e ) {

      if ( handler != null ) {

        handler.accept(e);
        return new Chain <>(null, null, null);

      }

      return new Chain <>(null, null, e);
    }
  }

  /**
   * Appends an exception handler to the Chain
   *
   * If an exception has already occurred in this chain previous to this call,
   * the given handler will fired immediately.
   *
   * This method is provided with the intent to allow logger injection to a call
   * chain.
   *
   * @param handler a {@link Consumer} for exception types.
   *
   * @return The current Chain with no modification to it's value.
   */
  public Chain < T > handle( final Consumer < Exception > handler )
  {
    if (exception != null) {
      handler.accept(exception);
      return new Chain<>(null, null, null);
    }

    return new Chain <>(value, handler, null);
  }
}
