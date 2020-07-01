package com.discape.javaquarium.business;

import com.discape.javaquarium.business.model.Aquarium;
import com.discape.javaquarium.business.model.Fish;
import com.discape.javaquarium.business.model.FishSpecies;
import org.junit.Test;

import java.io.File;

import static javafx.collections.FXCollections.observableArrayList;
import static org.junit.Assert.*;

public class AquariumFileTest {

    @Test
    public void io() throws Exception {
        Aquarium aquarium = new Aquarium(observableArrayList(
                new Fish("Dave", FishSpecies.BLUETANG),
                new Fish("Jack", FishSpecies.BLUETANG),
                new Fish("Luke", FishSpecies.COD),
                new Fish("Bob", FishSpecies.COD)
        ));
        String testFilePath = "/tmp/aquariumtest.txt";
        AquariumFile.setAquarium(aquarium, new File(testFilePath));
        /* Dave BLUETANG 131 #0d00ff 100
        Jack BLUETANG 184 #9900ff 100
        Luke COD 162 #dceeaa 100
        Bob COD 229 #cfeeaa 100 */
        assertEquals(aquarium.toString(), AquariumFile.getAquarium(new File(testFilePath)).toString());
    }
}