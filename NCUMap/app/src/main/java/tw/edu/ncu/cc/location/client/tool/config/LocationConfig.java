package tw.edu.ncu.cc.location.client.tool.config;

public interface LocationConfig {

    public String getServerAddress();
    public LocationConfig setServerAddress( String serverAddress );

    public LocationConfig configure( String configFilePath );

}
