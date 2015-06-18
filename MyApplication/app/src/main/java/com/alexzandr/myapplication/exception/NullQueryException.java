package com.alexzandr.myapplication.exception;

/**
 * Created by anekrasov on 15.06.15.
 */
public class NullQueryException extends CheckConnectionException {
    public NullQueryException(String msg){
        super(msg);
    }
}
