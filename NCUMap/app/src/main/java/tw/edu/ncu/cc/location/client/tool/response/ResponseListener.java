package tw.edu.ncu.cc.location.client.tool.response;

import java.util.Set;

public interface ResponseListener<T> {
    public void onResponse( Set<T> responses );
    public void onError( Throwable throwable );
}
