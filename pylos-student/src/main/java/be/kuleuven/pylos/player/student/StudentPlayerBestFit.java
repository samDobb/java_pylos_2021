package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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

        // place ball if the other one is getting a square
        for (PylosSquare square : allSquares){
            if (square.getInSquare(this.OTHER) == 3  || square.getInSquare(this.OTHER) == 2 && square.getInSquare(this) == 0  ){
                // tegenstander heeft 3 of 2 ballen in een vierkant
                if (square.getInSquare() == 3 || square.getInSquare() == 2){
                    PylosLocation[] squareLocations = square.getLocations();
                    for (PylosLocation bl : squareLocations) {
                        if (bl.isUsable()) {
                            bestLocation = bl;
                        }
                    }
                }
            }
        }
        // try to get a square ourselves
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
        // place ball on a higher lvl
        if (bestLocation == null){
            // find free ball
            // -> get all your balls
            // -> check if there are balls that a laying on it
            ArrayList<PylosSphere> myMovableSpheres = new ArrayList<>();

            for (PylosLocation bl : board.getLocations()) {
                if (bl.isUsed()){
                    if(bl.getSphere().PLAYER_COLOR == this.PLAYER_COLOR && bl.getSphere().canMove()){
                        boolean shootInFoot = false;
                        for(PylosSquare square :bl.getSquares()){
                            if(square.getInSquare(this.OTHER) == 3){
                                shootInFoot = true;
                            }
                        }
                        if(!shootInFoot){
                            myMovableSpheres.add(bl.getSphere());
                        }
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
            boolean prettyGoodLocation = false;
            for (PylosLocation location: allPossibleLocations){
                if (location.getMaxInSquare(this) > max && location.isUsable()){
                    // TODO zorgen dat we geen square maken voor zo ver als mogelijk als onze tegenstander nog een vrij bal heeft
                    // --> kijken of we een square maken
                    // --> kijken of tegenstander nog een vrije bal heeft -> zo niet is een square maken zelfs goed peis ik
                    boolean makeASquare = false;
                    PylosSquare newSquare = null; // TODO list maken en werken met list, want mogelijk dat ge meerdere vierkanten maakt met 1 zet.
                    List<PylosSquare> squares = location.getSquares();
                    for (PylosSquare square : squares){
                        if (square.getInSquare() == 3){
                            makeASquare = true;
                            newSquare = square;
                        }
                    }

                    boolean enemyHasFreeBall = false;
                    ArrayList<PylosSphere> enemyMovableSpheres = new ArrayList<>();

                    for (PylosLocation bl : board.getLocations()) {
                        if (bl.isUsed()) {
                            if (bl.getSphere().PLAYER_COLOR == this.OTHER.PLAYER_COLOR && bl.getSphere().canMove()) {
                                enemyMovableSpheres.add(bl.getSphere());
                                enemyHasFreeBall = true;
                            }
                        }
                    }

                    if (makeASquare && enemyHasFreeBall){
                        //this is no good location since we would give the enemy a extra sphere for free

                    }else if(!makeASquare && enemyHasFreeBall){
                        //check if there are squares that we can fill to block enemy from extra sphere
                        for( PylosSquare square :board.getAllSquares()){
                            if (square.getTopLocation().isUsable()){
                                bestLocation = square.getTopLocation();
                                prettyGoodLocation = true;
                            }
                        }
                    }else if(makeASquare && !enemyHasFreeBall){
                        // we do make a square but the enemy can't really do anything with it since they have no free ball
                        // but still i don't like making squares so i'm initially not going to use it
                    }else {
                        max = location.getMaxInSquare(this);
                        bestLocation = location;
                    }

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
                        return;
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
            game.pass();
        }
        /* always pass */

    }
}
