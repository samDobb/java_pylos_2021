package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGameIF;
import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;
import be.kuleuven.pylos.player.PylosPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Ine on 5/05/2015.
 */
public class StudentPlayerRandomFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
		/* add a reserve sphere to a feasible random location */

        PylosLocation[] allLocations = board.getLocations();

        //list with all the usable locations
        List<PylosLocation> safeLocations = new ArrayList<>();

        //getting a reserve sphere
        PylosSphere myReserveSphere = board.getReserve(this);

        for(PylosLocation loc:allLocations){
            if(loc.isUsable())safeLocations.add(loc);
        }

        int randomNum = ThreadLocalRandom.current().nextInt(0, safeLocations.size());

        game.moveSphere(myReserveSphere, safeLocations.get(randomNum));
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
		/* removeSphere a random sphere */
        PylosSphere[] mySpheres = board.getSpheres(this);
        List<PylosSphere> usableSpheres = new ArrayList<>();

        for(PylosSphere sphere:mySpheres){
            if(sphere.canRemove())usableSpheres.add(sphere);
        }

        int randomNum = ThreadLocalRandom.current().nextInt(0, usableSpheres.size());

        PylosSphere removingSphere = usableSpheres.get(randomNum);

        game.removeSphere(removingSphere);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* always pass */
        game.pass();
    }
}
