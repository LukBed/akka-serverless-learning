/* This code was initialised by Akka Serverless tooling.
 * As long as this file exists it will not be re-generated.
 * You are free to make changes to this file.
 */

package learning.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.valueentity.*;
import com.google.protobuf.Empty;
import learning.CounterApi;
import learning.TankApi;

/**
 * A value entity.
 */
@ValueEntity(entityType = "tank")
public class Tank extends AbstractTank {
    @SuppressWarnings("unused")
    private final String entityId;

    public Tank(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @Override
    public Reply<TankApi.FoundTank> upsert(TankApi.UpsertTank command, CommandContext<TankDomain.TankState> context) {

        TankDomain.TankState state = TankDomain.TankState.newBuilder()
                .setName(command.getName())
                .setNationality(command.getNationality())
                .setTankType(command.getTankType())
                .build();

        context.updateState(state);

        return Reply.message(tankFromState(command.getTankId(), state));
    }

    @Override
    public Reply<TankApi.FoundTank> rename(TankApi.RenameTank command, CommandContext<TankDomain.TankState> context) {
        TankDomain.TankState state = getStateOrThrow(context);
        TankDomain.TankState updatedState = state.toBuilder().setName(command.getName()).build();
        context.updateState(updatedState);
        return Reply.message(tankFromState(command.getTankId(), updatedState));
    }

    @Override
    public Reply<TankApi.FoundTank> find(TankApi.GetTank command, CommandContext<TankDomain.TankState> context) {
        TankDomain.TankState state = getStateOrThrow(context);
        return Reply.message(tankFromState(command.getTankId(), state));
    }

    private TankApi.FoundTank tankFromState(String tankId, TankDomain.TankState state) {
        return TankApi.FoundTank.newBuilder()
                .setTankId(tankId)
                .setName(state.getName())
                .setNationality(state.getNationality())
                .setTankType(state.getTankType()).build();
    }

    private TankDomain.TankState getStateOrThrow(CommandContext<TankDomain.TankState> context) {
        return context.getState().orElseThrow(() -> context.fail("Tank not found"));
    }
}