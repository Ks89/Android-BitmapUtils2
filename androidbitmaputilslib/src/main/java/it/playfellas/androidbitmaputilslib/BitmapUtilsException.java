/*
Copyright 2016 Stefano Cappa

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package it.playfellas.androidbitmaputilslib;

/**
 * Exception for messages.
 * <p></p>
 * Created by Stefano Cappa on 01/03/15.
 */
public class BitmapUtilsException extends Exception {

    public enum Reason {WRONGINPUTPARAMETER}

    private Reason reason;

    public BitmapUtilsException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message String message
     * @param cause   The throwable object
     */
    public BitmapUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     *
     * @param message String message
     */
    public BitmapUtilsException(String message) {
        super(message);
    }

    /**
     * Constructor
     *
     * @param cause String message
     */
    public BitmapUtilsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     *
     * @param reason Enumeration that represents the exception's reason.
     */
    public BitmapUtilsException(Reason reason) {
        this.reason = reason;
    }

    /**
     * Constructor
     *
     * @param reason  Enumeration that represents the exception's reason.
     * @param message String that represents the error.
     */
    public BitmapUtilsException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }
}