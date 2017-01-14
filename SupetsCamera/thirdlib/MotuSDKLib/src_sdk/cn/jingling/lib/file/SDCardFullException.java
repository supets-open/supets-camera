
package cn.jingling.lib.file;

public class SDCardFullException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1387209667953537584L;

    public SDCardFullException() {
        super();
    }

    public SDCardFullException(String msg) {
        super(msg);
    }

    public SDCardFullException(Throwable cause) {
        super(cause);
    }

    public SDCardFullException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
