package edu.brown.cs.student.main.DataSource;

/**
 * Quick Summary:
 * Represents an exception related to a data source operation.
 * Provides information about the error message and the root cause.
 */

/**
 * Represents an exception related to a data source operation that communicates that something
 * went wrong with a requested datasource.
 * The original cause is wrapped as a field, which helps with debugging, but also allows the caller
 * to handle the issue uniformly if they wish, without looking internally.
 */
public class DatasourceException extends Exception {
    // the root cause of this datasource problem
    private final Throwable cause;

    /**
     * Constructs a DatasourceException with the specified error message and no root cause.
     *
     * @param message - the error message.
     */
    public DatasourceException(String message) {
        super(message); // exception message
        this.cause = null;
    }

    /**
     * Constructs a DatasourceException with the specified error message and root cause.
     *
     * @param message - the error message.
     * @param cause - the root cause of the exception.
     */
    public DatasourceException(String message, Throwable cause) {
        super(message); // exception message
        this.cause = cause;
    }

    /**
     * Returns the Throwable provided (if any) as the root cause of
     * this exception. We don't make a defensive copy here because
     * we don't anticipate mutation of the Throwable to be any issue,
     * and because this is mostly implemented for debugging support.
     * @return the root cause Throwable
     */

    /**
     * Returns the Throwable provided (if any) as the root cause of this exception.
     * No defensive copy is made here because we do not anticipate mutation of the Throwable to be
     * of any issue, and because this is mostly implemented to help with debugging.
     *
     * @return - the root cause Throwable.
     */
    public Throwable getCause() { return this.cause; }
}