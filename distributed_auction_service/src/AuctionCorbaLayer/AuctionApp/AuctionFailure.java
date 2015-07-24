package AuctionApp;


/**
* AuctionApp/AuctionFailure.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from auction.idl
* Sunday, April 6, 2014 2:45:20 AM CDT
*/

public final class AuctionFailure extends org.omg.CORBA.UserException
{
  public AuctionApp.ErrorType error = null;
  public String description = null;

  public AuctionFailure ()
  {
    super(AuctionFailureHelper.id());
  } // ctor

  public AuctionFailure (AuctionApp.ErrorType _error, String _description)
  {
    super(AuctionFailureHelper.id());
    error = _error;
    description = _description;
  } // ctor


  public AuctionFailure (String $reason, AuctionApp.ErrorType _error, String _description)
  {
    super(AuctionFailureHelper.id() + "  " + $reason);
    error = _error;
    description = _description;
  } // ctor

} // class AuctionFailure
