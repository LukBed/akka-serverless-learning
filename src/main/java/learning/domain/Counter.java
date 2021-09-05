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
import scala.Function2;

/**
 * A value entity.
 */
@ValueEntity(entityType = "counter")
public class Counter extends AbstractCounter {
    @SuppressWarnings("unused")
    private final String entityId;

    public Counter(@EntityId String entityId) {
        this.entityId = entityId;
    }

    @Override
    public Reply<Empty> increase(CounterApi.IncreaseValue command, CommandContext<CounterDomain.CounterState> context) {
        updateValue(command.getValue(), context, (oldValue, newValue) -> oldValue < newValue, "new value must be greater than old value");
        return Reply.message(Empty.getDefaultInstance());
    }


    @Override
    public Reply<Empty> decrease(CounterApi.DecreaseValue command, CommandContext<CounterDomain.CounterState> context) {
        updateValue(command.getValue(), context, (oldValue, newValue) -> oldValue > newValue, "new value must be lower than old value");
        return Reply.message(Empty.getDefaultInstance());
    }

    @Override
    public Reply<Empty> reset(CounterApi.ResetValue command, CommandContext<CounterDomain.CounterState> context) {
        context.updateState(CounterDomain.CounterState.getDefaultInstance());
        return Reply.message(Empty.getDefaultInstance());
    }

    @Override
    public Reply<CounterApi.CurrentCounter> getCurrentCounter(CounterApi.GetCounter command,
                                                              CommandContext<CounterDomain.CounterState> context) {

        CounterApi.CurrentCounter current = context.getState()
                .map(state -> CounterApi.CurrentCounter.newBuilder().setValue(state.getValue()).build())
                .orElseGet(() -> CounterApi.CurrentCounter.newBuilder().setValue(0).build());

        return Reply.message(CounterApi.CurrentCounter.newBuilder().setValue(current.getValue()).build());
    }

    private void updateValue(int newValue, CommandContext<CounterDomain.CounterState> context, Function2<Integer, Integer, Boolean> oldNewValuePredicate, String errorMessage) {
        if (newValue < 0) {
            throw context.fail("Value must be positive");
        }

        CounterDomain.CounterState state = context.getState().orElseGet(() -> CounterDomain.CounterState.newBuilder().build());
        int oldValue = state.getValue();

        if (!oldNewValuePredicate.apply(oldValue, newValue)) {
            throw context.fail("Invalid new value: " + errorMessage);
        }

        CounterDomain.CounterState newState = state.toBuilder().setValue(newValue).build();

        context.updateState(newState);
    }
}