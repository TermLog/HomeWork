package com.alexzandr.myapplication.exception;

/**
 * Created by anekrasov on 27.03.15.
 */
public class CheckConnectionException extends RuntimeException {
    public CheckConnectionException(String msg){
        super(msg);
    }
}
