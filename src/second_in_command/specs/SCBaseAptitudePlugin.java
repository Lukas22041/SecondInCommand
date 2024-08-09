package second_in_command.specs;

import java.util.ArrayList;
import java.util.List;

public abstract class SCBaseAptitudePlugin {

    public SCAptitudeSpec spec;
    private  List<SCAptitudeSection> sections = new ArrayList<>();

    public void addSection(SCAptitudeSection section) {
        sections.add(section);
    }

    public List<SCAptitudeSection> getSections() {
        return sections;
    }

    public void clearSections() {
        
    }


}
