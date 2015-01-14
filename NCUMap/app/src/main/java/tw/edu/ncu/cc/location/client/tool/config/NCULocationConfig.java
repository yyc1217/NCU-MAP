package tw.edu.ncu.cc.location.client.tool.config;

import tw.edu.ncu.cc.location.client.tool.exception.LocationClientException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NCULocationConfig implements LocationConfig {

    private String serverAddress;

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public LocationConfig setServerAddress( String serverAddress ) {
        this.serverAddress = serverAddress;
        return this;
    }

    @Override
    public LocationConfig configure( String configFilePath ) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream( configFilePath );
        Properties  properties  = new Properties();
        try {
            properties.load( inputStream );
        } catch ( IOException e ) {
            throw new LocationClientException( "cannot read config file:" + configFilePath, e );
        }
        return buildConfig( properties );
    }

    private LocationConfig buildConfig( Properties properties ) {
        setServerAddress( properties.getProperty( "location.server_address" ) );
        return this;
    }

}
