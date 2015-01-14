package tw.edu.ncu.cc.location.client.tool.converter;

import tw.edu.ncu.cc.location.data.wrapper.ResultWrapper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ResponseConverter {

    public static <T> List<T> convert( ResultWrapper<T> resultWrapper ) {
        if( resultWrapper.getResult() == null ) {
            return new LinkedList<>();
        } else {
            return new LinkedList<>( Arrays.asList( resultWrapper.getResult() ) );
        }
    }

}
