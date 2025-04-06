package second_in_command;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import second_in_command.specs.SCOfficer;

public class Runcodes {

    public void test() {



       /* //Get the SiC Data for the fleet. Replace the fleet variable with your target fleet here
        CampaignFleetAPI fleet;
        SCData data = SCUtils.getFleetData(fleet);

        //Create a new Vanilla Person, or use one you already have.
        PersonAPI person = Global.getFactory().createPerson();
        person.setName(new FullName("Test", "Person", FullName.Gender.ANY));

        //Fill out data about that person here, mostly just needs a portrait.

        //Create the SiC officer by providing it with the person we created, and giving them the ID of the aptitude they should have.
        SCOfficer execOfficer = new SCOfficer(person, "aptitudeIDHere");
        execOfficer.addSkill("exampleSkillIDHere");
        execOfficer.addSkill("exampleSkillIDHere");
        execOfficer.addSkill("exampleSkillIDHere");
        execOfficer.addSkill("exampleSkillIDHere");
        execOfficer.addSkill("exampleSkillIDHere");

        data.addOfficerToFleet(execOfficer); //Adds the officer to the fleet.
        data.setOfficerInSlot(0, execOfficer); //Set Officer in to slot somewhere between 0-2*/
    }

}
