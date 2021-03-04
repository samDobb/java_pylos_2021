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
        PylosSquare[] allSquares = board.getAllSquares();

        PylosLocation bestLocation = null;
        PylosSphere usedSphere = null;


        for (PylosSquare square : allSquares){
            if (square.getInSquare(this.OTHER) == 3){
                // tegenstander heeft 3 ballen in een vierkant
                if (square.getInSquare() == 3){
                    PylosLocation[] squareLocations = square.getLocations();
                    for (PylosLocation bl : squareLocations) {
                        if (bl.isUsable()) {
                            bestLocation = bl;
                        }
                    }
                }
            }
        }
        if (bestLocation == null){
            for (PylosSquare square : allSquares){
                if (square.getInSquare(this) == 3){
                    // we hebben 3 ballen in een vierkant
                    if (square.getInSquare() == 3){
                        PylosLocation[] squareLocations = square.getLocations();
                        for (PylosLocation bl : squareLocations) {
                            if (bl.isUsable()) {
                                bestLocation = bl;
                            }
                        }
                    }
                }
            }
        }
        if (bestLocation == null){
            // find free ball
            // -> get all your balls
            // -> check if there are balls that a laying on it
            ArrayList<PylosSphere> myMovableSpheres = new ArrayList<>();

            for (PylosLocation bl : board.getLocations()) {
                if (bl.isUsed()){
                    if(bl.getSphere().PLAYER_COLOR == this.PLAYER_COLOR && bl.getSphere().canMove()){
                        myMovableSpheres.add(bl.getSphere());
                    }
                }
            }

            // find free location on a higher lvl
            for (PylosSquare square : allSquares){
                if (square.isSquare() && square.getTopLocation().isUsable()){
                    for (PylosSphere sphere: myMovableSpheres){
                        if (sphere.canMoveTo(square.getTopLocation())){
                            bestLocation = square.getTopLocation();
                            usedSphere = sphere;
                        }
                    }
                }
            }
        }
        if (bestLocation == null){
            int max = 0;
            for (PylosLocation location: allPossibleLocations){
                if (location.getMaxInSquare(this) > max && location.isUsable()){
                    max = location.getMaxInSquare(this);
                    bestLocation = location;
                }
            }
        }


        if (usedSphere == null){
            usedSphere = board.getReserve(this);
        }
        if (bestLocation == null){
            bestLocation = allPossibleLocations.size() == 1 ? allPossibleLocations.get(0) : allPossibleLocations.get(getRandom().nextInt(allPossibleLocations.size() - 1));

        }
        game.moveSphere(usedSphere, bestLocation);
    }




    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        /* removeSphere from a filled square */
        PylosSquare[] allSquares = board.getAllSquares();
        for(PylosSquare square : allSquares){
            if (square.isSquare() && !square.getTopLocation().isUsed()){
                PylosLocation[] locations =square.getLocations();
                for (PylosLocation location: locations){
                    if(location.getSphere().PLAYER_COLOR == this.PLAYER_COLOR && location.getSphere().canMove()){
                        game.removeSphere(location.getSphere());

                    }
                }
            }
        }

    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        boolean foundOne = false;
        PylosSphere sphereToRemove = null;

        PylosSquare[] allSquares = board.getAllSquares();
        for(PylosSquare square : allSquares){
            if (square.isSquare() && !square.getTopLocation().isUsed()){
                PylosLocation[] locations =square.getLocations();
                for (PylosLocation location: locations){
                    if(location.getSphere().PLAYER_COLOR == this.PLAYER_COLOR && location.getSphere().canMove()){
                        foundOne = true;
                        game.removeSphere(location.getSphere());
                        return;
                    }
                }
            }
        }

        if (!foundOne){
            System.out.println("Geen 2e gevonden om te verwijderen");
            game.pass();
        }
        /* always pass */

    }
}
