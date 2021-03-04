package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class StudentPlayerRandomFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {

        /* add a reserve sphere to a feasible random location */
        Random random = this.getRandom();

        ArrayList<PylosLocation> allUsableLocations = new ArrayList<>();
        for (PylosLocation bl : board.getLocations()) {
            if (bl.isUsable()) {
                allUsableLocations.add(bl);
            }
        }

        PylosSphere myReserveSphere = board.getReserve(this);

        PylosSquare[] allSquares = board.getAllSquares();

        boolean ballPlaced = false;

        // ALS CENTER PIECE AVAILABLE (slechte waarden)
        /*
        if (!ballPlaced) {
            for (PylosLocation pl: allUsableLocations) {
                if (pl.Z == 1 && pl.X == 2 && pl.Y == 2) {
                    game.moveSphere(myReserveSphere, pl);
                    ballPlaced = true;
                    break;
                }
            }
        }
        */

        // PLACE BALL HIGHER
        if (!ballPlaced) {
            ArrayList<PylosLocation> allUsedLocations = new ArrayList<>();
            // alle locaties waar een bal staat
            for (PylosLocation pl: board.getLocations()) {
                // alle gebruikte ballen en er staat niks op
                if (pl.isUsed() && !pl.hasAbove()) {
                    // eigen bal
                    if (pl.getSphere().PLAYER_COLOR.equals(PLAYER_COLOR)) {
                        // check verplaats naar hoger niveau
                        boolean badLocation = false;
                        for (PylosLocation newpl: allUsableLocations) { // alle lege locatie die niveau hoger zijn
                            if (newpl.Z > pl.Z) {
                                badLocation = false;
                                for (PylosLocation plunder: newpl.getBelow()) {
                                    if (plunder.getSphere().equals(pl.getSphere())) {
                                        badLocation = true;
                                    }
                                }
                                if (!badLocation) {
                                    game.moveSphere(pl.getSphere(), newpl);
                                    ballPlaced = true;
                                    break;
                                }
                            }
                        }
                        if (ballPlaced) {
                            break;
                        }
                    }
                }
            }
        }

        // BLOCK OTHER PLAYER SQUARE
        if (!ballPlaced) {
            PylosSquare[] squares = getSquaresByNumberOfBallsAndPlayer(board, PLAYER_COLOR.other(), 3);
            for (PylosSquare ps: squares) {
                PylosLocation newLocation = null;
                for (int i = 0; i < 4; i++) {
                    if (!ps.getLocations()[i].isUsed()) {
                        newLocation = ps.getLocations()[i];
                    }
                }

                if (newLocation.isUsable()) {
                    game.moveSphere(myReserveSphere, newLocation);
                    ballPlaced = true;
                    break;
                }
            }
        }

        // MAKE SQUARE
        if (!ballPlaced) {
            PylosSquare[] squares = getSquaresByNumberOfBallsAndPlayer(board, PLAYER_COLOR, 3);
            for (PylosSquare ps: squares) {
                PylosLocation newLocation = null;
                for (int i = 0; i < 4; i++) {
                    if (!ps.getLocations()[i].isUsed()) {
                        newLocation = ps.getLocations()[i];
                    }
                }

                if (newLocation.isUsable()) {
                    game.moveSphere(myReserveSphere, newLocation);
                    ballPlaced = true;
                    break;
                }
            }
        }

        // als no technieken mogelijk
        if (!ballPlaced) {
            Collections.shuffle(allUsableLocations);
            for (PylosLocation pl: allUsableLocations) {
                game.moveSphere(myReserveSphere, pl);
                break;
            }
        }
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        boolean ballRemoved = false;

        // REMOVE BALL FROM SQUARE (betere resultaten tegen level 5)
        PylosSquare[] fullSquares = getSquaresByNumberOfBallsAndPlayer(board, PLAYER_COLOR, 4);
        for (PylosSquare ps: fullSquares) {
            if (!ps.getTopLocation().isUsed()) {
                for (int i = 0; i < 4; i++) {
                    if (ps.getLocations()[i].getSphere().canRemove()) {
                        game.removeSphere(ps.getLocations()[i].getSphere());
                        ballRemoved = true;
                        break;
                    }
                }
                if (ballRemoved) {
                    break;
                }
            }
        }

        if (!ballRemoved) {
            PylosSphere[] allSpheres = board.getSpheres(this);
            // first remove spheres on the lower levels
            PylosSphere[] spheresSorted = sortBallsHeight(allSpheres);
            for (int i = 0; i < spheresSorted.length; i++) {
                if (spheresSorted[i].canRemove()) {
                    game.removeSphere(spheresSorted[i]);
                    break;
                }
            }
        }
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        int removeableBalls = 0;
        for (PylosLocation pl: board.getLocations()) {
            if (pl.isUsed()) {
                if (pl.getSphere().canRemove() && pl.getSphere().PLAYER_COLOR.equals(PLAYER_COLOR)) {
                    removeableBalls++;
                }
            }
        }

        if (removeableBalls > 0) {
            doRemove(game, board);
        } else {
            game.pass();
        }
    }

    /**
     *
     * @param spheres geef een lijst van spheres mee
     * @return geeft een lijst van sphere terug gesorteerd volgens laagste locatie naar hoogste locatie
     */
    public PylosSphere[] sortBallsHeight(PylosSphere[] spheres) {
        ArrayList<PylosLocation> sphereLocations = new ArrayList<>();
        for (PylosSphere ps: spheres) {
            if (!ps.isReserve()) {
                sphereLocations.add(ps.getLocation());
            }
        }

        sphereLocations.sort(Comparator.comparingInt((PylosLocation pl) -> pl.Z));

        PylosSphere[] result = new PylosSphere[spheres.length];
        for (int i = 0; i < sphereLocations.size(); i++) {
            result[i] = sphereLocations.get(i).getSphere();
        }

        return result;
    }

    /**
     *
     * @param board geef ook het bord mee
     * @param color geef de kleur mee van welke speler je een aantal spheres opvraagt
     * @param numberOfBalls geef het aantal ballen mee die in de squares moeten voorkomen
     * @return geeft alle squares terug die voldoen aan een bepaald aantal ballen van een bepaalde speler
     */
    public PylosSquare[] getSquaresByNumberOfBallsAndPlayer(PylosBoard board, PylosPlayerColor color, int numberOfBalls) {
        PylosSquare[] allSquares = board.getAllSquares();

        ArrayList<PylosSquare> result = new ArrayList<>();

        for (PylosSquare ps: allSquares) {
            int askedColorAmount = 0; // aantal gevraagde ballen in square
            int otherColorAmount = 0;

            if (!ps.getTopLocation().isUsed()) {
                for (int i = 0; i < 4; i++) {
                    if (ps.getLocations()[i].isUsed()) {
                        if (ps.getLocations()[i].getSphere().PLAYER_COLOR.equals(color)) {
                            askedColorAmount++;
                        } else {
                            otherColorAmount++;
                        }
                    }
                }

                if (askedColorAmount == numberOfBalls && otherColorAmount == 0) {
                    result.add(ps);
                }
            }
        }

        PylosSquare[] resultSquare = new PylosSquare[result.size()];
        for (int i = 0; i < result.size(); i++) {
            resultSquare[i] = result.get(i);
        }

        return resultSquare;
    }
}
