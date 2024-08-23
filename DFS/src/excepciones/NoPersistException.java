/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package excepciones;

/**
 *
 * @author Dany
 */
public class NoPersistException extends Exception{

    public NoPersistException() {
    }

    public NoPersistException(Exception exception) {
        super(exception);
    }
    
    @Override
    public String getMessage() {
        String message = "Error tratando de persistir la prueba";
        return message;
    }   
}
