/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package learning.domain;

import com.akkaserverless.javasdk.valueentity.CommandContext;
import com.google.protobuf.Empty;
import learning.TankApi;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.assertThrows;

public class TankTest {
    private String entityId = "entityId1";
    private Tank entity;
    private CommandContext<TankDomain.TankState> context = Mockito.mock(CommandContext.class);
    
    @Test
    public void upsertTest() {
        entity = new Tank(entityId);
        
        // TODO: write your mock here
        // Mockito.when(context.[...]).thenReturn([...]);
        
        // TODO: set fields in command, and update assertions to verify implementation
        // assertEquals([expected],
        //    entity.upsert(TankApi.UpsertTank.newBuilder().build(), context);
        // );
    }
    
    @Test
    public void findTest() {
        entity = new Tank(entityId);
        
        // TODO: write your mock here
        // Mockito.when(context.[...]).thenReturn([...]);
        
        // TODO: set fields in command, and update assertions to verify implementation
        // assertEquals([expected],
        //    entity.find(TankApi.GetTank.newBuilder().build(), context);
        // );
    }
}