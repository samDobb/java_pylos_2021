package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;

public class StudentPlayerBestFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        /* add a reserve sphere to a feasible random location */
        ArrayList<PylosLocation> allPossibleLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (bl.isUsable()) {
                allPossibleLocations.add(bl);
            }
        }
        ArrayList<PylosLocation> ownLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {

            PylosSquare ps = bl.getFilledSquare(this);

        }
        ArrayList<PylosLocation> enemyLocations = new ArrayList<>();

        PylosSphere reserveSphere = board.getReserve(this);
        PylosLocation location = allPossibleLocations.size() == 1 ? allPossibleLocations.get(0) : allPossibleLocations.get(getRandom().nextInt(allPossibleLocations.size() - 1));
        game.moveSphere(reserveSphere, location);
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* removeSphere a random sphere */

    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        /* always pass */

    }
}
