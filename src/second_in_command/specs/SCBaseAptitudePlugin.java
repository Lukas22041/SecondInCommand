package second_in_command.specs;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import second_in_command.SCData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**Instantiated on application load and kept in memory, do not save class-scope variables within */
public abstract class SCBaseAptitudePlugin {

    public SCAptitudeSpec spec;

    /**Used for adding sections within the createSections() method. */
    public final void addSection(SCAptitudeSection section) {
        sections.add(section);
    }

    /**Required, this is the first skill in the aptitude that will always be unlocked by default. */
    public abstract String getOriginSkillId();

    /**Required, used to decide which skills are added, and how they are organised within. */
    public abstract void createSections();



    /**
     * Change the weight of an aptitude being picked for the fleet generation.
     * The average value should be 1, set it lower value for it to be rarer, higher for it to be common
     * If your aptitude is very specific to some specific use-case, for example dmods, change the returned value based on those conditions.
     *
     * @return weight, lower is rarer, 0 is never*/
    public abstract Float getNPCFleetSpawnWeight(SCData data, CampaignFleetAPI fleet);

    /**
     * Chance for this aptitude to be picker over others on comm-links.
     * @return weight, lower is rarer, 0 is never */
    public Float getMarketSpawnweight(MarketAPI market) {
        return spec.getSpawnWeight();
    }

    /**
     * Chance for the aptitude to be picked over others for cryo-pods.
     * @return weight, lower is rarer, 0 is never */
    public Float getCryopodSpawnWeight(StarSystemAPI System) {
        return spec.getSpawnWeight();
    }


    /**Only used for NPC fleet generation. Guarantees its picked when returned true, should be used sparingly.
     * This is mostly used in case the aptitude is incompatible with another, and this one should have priority over the other*/
    public Boolean guaranteePick(CampaignFleetAPI fleet) {
        return false;
    }

    public final String getId() {
        return spec.getId();
    }

    public String getName() {
        return spec.getName();
    }

    public List<SCCategorySpec> getCategories() {
        return spec.getCategories();
    }

    public Color getColor() {
        return spec.getColor();
    }

    public List<String> getTags() {
        return spec.getTags();
    }


    public void addCodexDescription(TooltipMakerAPI tooltip) {

    }




    //Internal Use Only
    public final SCSkillSpec getOriginSkillSpec() {
        return SCSpecStore.getSkillSpec(getOriginSkillId());
    }

    //Internal Use Only
    public final SCBaseSkillPlugin getOriginSkillPlugin() {
        return getOriginSkillSpec().getPlugin();
    }

    //Internal Use Only
    private final List<SCAptitudeSection> sections = new ArrayList<>();

    //Internal Use Only
    //Create sections, then immediately clear, to prevent memory leaks due to UI data in sections.
    //Used to be handled differently, before Aptitudes were hold for the entirety of the session duration.
    public final List<SCAptitudeSection> getSections() {
        createSections();
        List<SCAptitudeSection> list = new ArrayList<>(sections);
        sections.clear();
        return list;
    }

    //Internal Use Only
    /**@deprecated Does Nothing now. The aptitude itself no longer keeps sections around after creating it to prevent memory leaks*/
    @Deprecated
    public final void clearSections() {
        //sections.clear();
    }
}
