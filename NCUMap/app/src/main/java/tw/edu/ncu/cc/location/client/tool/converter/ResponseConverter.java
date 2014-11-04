package tw.edu.ncu.cc.location.client.tool.converter;

import tw.edu.ncu.cc.location.data.wrapper.ResultWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResponseConverter {

    public static <T> Set<T> convert( ResultWrapper<T> resultWrapper ) {
        if( resultWrapper.getResult() == null ) {
            return new HashSet<T>();
        } else {
            return new HashSet<T>( Arrays.asList( resultWrapper.getResult() ) );
        }
    }

}
