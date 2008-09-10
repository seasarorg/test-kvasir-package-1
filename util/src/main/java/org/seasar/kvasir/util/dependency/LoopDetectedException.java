package org.seasar.kvasir.util.dependency;


/**
 * @author YOKOTA Takehiko
 */
public class LoopDetectedException extends Exception
{
    private static final long serialVersionUID = -7127672555202627897L;


    private Dependency      requirement_;
    private Dependency      dependant_;


    public LoopDetectedException()
    {
        super();
    }


    /**
     * @param message
     */
    public LoopDetectedException(String message)
    {
        super(message);
    }


    public String toString()
    {
        String message = getMessage();
        if (message == null) {
            message = "Loop has been detected";
        }
        return new StringBuffer().append(message).append(": requirement=")
            .append(requirement_.getId()).append(", dependant=")
            .append(dependant_.getId()).toString();
    }


    public Dependency getDependant()
    {
        return dependant_;
    }


    public LoopDetectedException setDependant(Dependency dependant)
    {
        dependant_ = dependant;
        return this;
    }


    public Dependency getRequirement()
    {
        return requirement_;
    }


    public LoopDetectedException setRequirement(Dependency requirement)
    {
        requirement_ = requirement;
        return this;
    }
}
