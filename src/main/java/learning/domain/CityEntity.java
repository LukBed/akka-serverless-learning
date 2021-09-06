package learning.domain;

import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.google.protobuf.Empty;
import learning.CityApi;

@EventSourcedEntity(entityType = "city-entity")
public class CityEntity {
    private final String id;
    private boolean active = false;
    private String name = "";
    private int gold = 0;
    private int cityLevel = 0;
    private int creaturesLevel = 0;

    public CityEntity(@EntityId String id) {
        this.id = id;
    }

    @CommandHandler
    public CityApi.FoundCity getCity(CityApi.GetCityQuery query, CommandContext ctx) {
        failIfNotActive(ctx);

        return CityApi.FoundCity.newBuilder()
                .setCityId(id)
                .setName(name)
                .setGold(gold)
                .setCityLevel(cityLevel)
                .setCreaturesLevel(creaturesLevel)
                .build();
    }

    @CommandHandler
    public Empty createCity(CityApi.CreateCityCommand command, CommandContext ctx) {
        if (active) {
            throw ctx.fail("City already activated");
        }

        CityDomain.CityCreated event = CityDomain.CityCreated.newBuilder()
                .setName(command.getName())
                .build();
        ctx.emit(event);

        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void cityCreated(CityDomain.CityCreated event) {
        this.name = event.getName();
        this.active = true;
        this.cityLevel = 1;
    }

    @CommandHandler
    public Empty executeNextTour(CityApi.NextTourCommand command, CommandContext ctx) {
        failIfNotActive(ctx);

        CityDomain.NextTour event = CityDomain.NextTour.newBuilder().build();
        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void nextTour(CityDomain.NextTour event) {
        this.gold += this.cityLevel * 1000;
    }

    @CommandHandler
    public Empty buildCityLevel(CityApi.BuildCityLevelCommand command, CommandContext ctx) {
        failIfNotActive(ctx);
        int requiredGold = (this.cityLevel + 1) * 1500;
        failIfNoGold(requiredGold, ctx);

        CityDomain.CityLeveledUp event = CityDomain.CityLeveledUp.newBuilder()
                .setTakeGold(requiredGold)
                .build();

        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void cityLeveledUp(CityDomain.CityLeveledUp event) {
        this.gold -= event.getTakeGold();
        this.cityLevel++;
    }

    @CommandHandler
    public Empty buildCreatures(CityApi.BuildCreaturesCommand command, CommandContext ctx) {
        failIfNotActive(ctx);
        int requiredGold = (this.creaturesLevel + 1) * 500;
        failIfNoGold(requiredGold, ctx);

        CityDomain.CreaturesLeveledUp event = CityDomain.CreaturesLeveledUp.newBuilder()
                .setTakeGold(requiredGold)
                .build();

        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void creaturesLeveledUp(CityDomain.CreaturesLeveledUp event) {
        this.gold -= event.getTakeGold();
        this.creaturesLevel++;
    }

    @CommandHandler
    public Empty renameCity(CityApi.RenameCityCommand command, CommandContext ctx) {
        failIfNotActive(ctx);

        CityDomain.CityRenamed event = CityDomain.CityRenamed.newBuilder()
                .setName(command.getName())
                .build();

        ctx.emit(event);
        return Empty.getDefaultInstance();
    }

    @EventHandler
    public void cityRenamed(CityDomain.CityRenamed event) {
        this.name = event.getName();
    }

    private void failIfNotActive(CommandContext ctx) {
        if (!active) {
            throw ctx.fail("City must be activated");
        }
    }

    private void failIfNoGold(int minGold, CommandContext ctx) {
        if (this.gold < minGold) {
            throw ctx.fail("Not have " + minGold + " gold, only " + this.gold);
        }
    }
}
