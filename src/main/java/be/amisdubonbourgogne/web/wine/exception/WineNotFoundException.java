package be.amisdubonbourgogne.web.wine.exception;

public class WineNotFoundException extends RuntimeException{
    public WineNotFoundException(String message){
        super(message);
    }
}
