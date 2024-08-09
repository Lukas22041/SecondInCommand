package second_in_command.specs;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import second_in_command.SCData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SCBaseAptitudePlugin {

    public SCAptitudeSpec spec;
    private final List<SCAptitudeSection> sections = new ArrayList<>();

    public final void addSection(SCAptitudeSection section) {
        sections.add(section);
    }

    public final List<SCAptitudeSection> getSections() {
        return sections;
    }

    public final void clearSections() {
        sections.clear();
    }

    public abstract String getOriginSkillId();

    public final SCSkillSpec getOriginSkillSpec() {
        return SCSpecStore.getSkillSpec(getOriginSkillId());
    }

    public final SCBaseSkillPlugin getOriginSkillPlugin() {
        return getOriginSkillSpec().getPlugin();
    }

    public abstract void createSections();

    public abstract Float getNPCFleetSpawnWeight(SCData data, CampaignFleetAPI fleet);

    public Float getMarketSpawnweight(MarketAPI market) {
        return spec.getSpawnWeight();
    }

    public Float getCryopodSpawnWeight(StarSystemAPI System) {
        return spec.getSpawnWeight();
    }

    public final String getId() {
        return spec.getId();
    }

    public String getName() {
        return spec.getName();
    }

    public Color getColor() {
        return spec.getColor();
    }

    public List<String> getTags() {
        return spec.getTags();
    }
}
