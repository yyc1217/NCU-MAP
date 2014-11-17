package tw.edu.ncu.cc.location.client.tool.response;

import java.util.List;

public interface ResponseListener<T> {
    public void onResponse( List<T> responses );
    public void onError( Throwable throwable );
}
