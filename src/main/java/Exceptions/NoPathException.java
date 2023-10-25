package Exceptions;

import agent.explorer.Vertex;

public class NoPathException extends Exception{
    public NoPathException(Vertex start, Vertex end){
        super("A* cannot find a path from " + start.getTile().toString() + " to " + end.getTile().toString() + "!");
    }
}
