/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package learning;

import com.akkaserverless.javasdk.AkkaServerless;
import learning.domain.CityDomain;
import learning.domain.CityEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static learning.MainComponentRegistrations.withGeneratedComponentsAdded;

public final class Main {
    
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public static final AkkaServerless SERVICE =
        // This withGeneratedComponentsAdded wrapper automatically registers any generated Actions, Views or Entities,
        // and is kept up-to-date with any changes in your protobuf definitions.
        // If you prefer, you may remove this wrapper and manually register these components.
        withGeneratedComponentsAdded(new AkkaServerless())
                .registerEventSourcedEntity(
                        CityEntity.class,
                        CityApi.getDescriptor().findServiceByName("CityService"),
                        CityDomain.getDescriptor())
            ;
    
    public static void main(String[] args) throws Exception {
        LOG.info("starting the Akka Serverless service");
        SERVICE.start();
    }
}