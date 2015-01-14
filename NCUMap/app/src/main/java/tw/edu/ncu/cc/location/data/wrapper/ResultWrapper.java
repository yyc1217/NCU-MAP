package tw.edu.ncu.cc.location.data.wrapper;

public class ResultWrapper<T> {

    private T[] result;

    public T[] getResult() {
        return result;
    }

    public void setResult( T[] result ) {
        this.result = result;
    }

}
